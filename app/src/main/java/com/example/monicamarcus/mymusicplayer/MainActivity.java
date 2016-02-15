package com.example.monicamarcus.mymusicplayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.view.View;

import android.media.MediaPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import static junit.framework.Assert.assertEquals;

public class MainActivity extends Activity {
    private static final String endpoint = "http://streaming.earbits.com/api/v1/track.json?stream_id=5654d7c3c5aa6e00030021aa";

    private static final String TAG_ARTIST_NAME = "artist_name";
    private static final String TAG_TRACK_NAME = "name";
    private static final String TAG_COVER_IMAGE_URL = "cover_image";
    private static final String TAG_TRACK_URL = "media_file";

    private static ArrayList<Song> songList = new ArrayList<>();
    private static Song currentSong = null;
    private static int currentPosition = -1;

    TextView tvResponse;
    TextView tvIsConnected;
    ImageView coverImage;
    Button nextTrackBtn;
    Button previousTrackBtn;
    ProgressDialog mProgressDialog;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResponse = (TextView) findViewById(R.id.tvResponse);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        coverImage = (ImageView) findViewById(R.id.image);
        previousTrackBtn = (Button) findViewById(R.id.previousTrackBt);
        previousTrackBtn.setOnClickListener(handlerPreviousTrack);
        nextTrackBtn = (Button) findViewById(R.id.nextTrackBt);
        nextTrackBtn.setOnClickListener(handlerNextTrack);

        assertEquals("text should be empty", "", tvResponse.getText().toString());

        // check if you are connected or not
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are connected");
        }
        else{
            tvIsConnected.setText("You are NOT connected");
        }
    }

    View.OnClickListener handlerNextTrack = new View.OnClickListener() {
        public void onClick(View v) {
            if (mp != null) mp.stop();
            if ((!songList.isEmpty()) && currentPosition < songList.size() - 1) {
                currentPosition++;
                currentSong = songList.get(currentPosition);
                tvResponse.setText(currentSong.toString());
                new getImageAsyncTask().execute(currentSong.getImageURL());
                play(currentSong.getTrackURL());
            } else
                new HttpAsyncTask().execute(endpoint);
            if (currentSong != null && !songList.contains(currentSong)) {
                songList.add(currentSong);
            }
            if (currentSong != null)
                currentPosition = songList.indexOf(currentSong);
        }
    };

    View.OnClickListener handlerPreviousTrack = new View.OnClickListener() {
        public void onClick(View v) {
            if (mp != null) mp.stop();
            songList.trimToSize();
            if (!songList.isEmpty()) {
                currentPosition = songList.indexOf(currentSong) - 1;
                if (currentPosition >= 0) {
                    currentSong = songList.get(currentPosition);
                    tvResponse.setText(currentSong.toString());
                    new getImageAsyncTask().execute(currentSong.getImageURL());
                    play(currentSong.getTrackURL());
                } else {
                    tvResponse.setText("There is no previous song in the list");
                    coverImage.setImageBitmap(null);
                }
            } else {
                currentSong = null;
                currentPosition = -1;
                tvResponse.setText("Get a new song, the list is empty.");
                coverImage.setImageBitmap(null);
            }
        }
    };

    //play the track from external URL
    public void play(String trackURL) {
        try {
            mp = new MediaPlayer();
            mp.setDataSource(trackURL);
            mp.prepare();
            mp.start();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    //When the Pause button is clicked
    public void pause(View view) {
        if (mp != null)
            mp.pause();
    }

    //When the Resume button is clicked
    public void resume(View view) {
        if (mp != null)
            mp.start();
    }

    //When the Stop button is clicked
    public void stop(View view) {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class getImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Download Cover Image");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... urls) {

            String imageURL = urls[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                Toast.makeText(getBaseContext(), "Image not available", Toast.LENGTH_LONG).show();
            }
            // Set the bitmap into ImageView
            coverImage.setImageBitmap(result);
            mProgressDialog.dismiss();
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_SHORT).show();
            try {
                JSONObject json = new JSONObject(result);
                currentSong = new Song(getTrackURL(json), getTrackName(json), getArtistName(json), getCoverImageURL(json));
                tvResponse.setText(currentSong.toString());
                assertEquals("text should contain", "TRACK NAME: ", tvResponse.getText().toString().substring(0, 12));
                String imageURL = getCoverImageURL(json);
                new getImageAsyncTask().execute(imageURL);
                String trackURL = getTrackURL(json);
                currentSong.setImageURL(imageURL);
                songList.add(currentSong);
                play(trackURL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getTrackName(JSONObject json) {
        String trackName = "";
        try {
            trackName = json.getString(TAG_TRACK_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return trackName;
        }
    }

    private String getArtistName(JSONObject json) {
        String artistName = "";
        try {
            artistName = json.getString(TAG_ARTIST_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return artistName;
        }
    }

    private String getCoverImageURL(JSONObject json){
        String imageURL = "";
        try {
            imageURL = json.getString(TAG_COVER_IMAGE_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return imageURL;
        }
    }

    private String getTrackURL(JSONObject json) {
        String trackURL = "";
        try {
            trackURL = json.getString(TAG_TRACK_URL);
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return trackURL;
        }
    }

}
