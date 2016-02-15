package com.example.monicamarcus.mymusicplayer;

import android.test.UiThreadTest;
import android.widget.Button;
import android.app.Activity;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ExampleUnitTest {

    private int currentPosition;
    private ArrayList<Song> songList;

    Activity myActivity = new MainActivity();
    Song mySong = new Song();

    public boolean isCurrentPositionCorrect() {
        return (songList.size() <= 0) || currentPosition < songList.size();
    }

    @Before
    public void setUp() {
        songList = new ArrayList<>();
        currentPosition = -1;
    }

    @Test
    public void testCurrentPosition() throws Exception {
        boolean p = isCurrentPositionCorrect();
        if (BuildConfig.DEBUG && !p) {
            throw new AssertionError();
        }
        assertTrue(currentPosition < songList.size());
    }

    @UiThreadTest
    public void testNextTrackButton() throws Exception {
        Button mButton = (Button) myActivity.findViewById(R.id.nextTrackBt);
        songList.add(mySong);
        mButton.performClick();
        mButton.performClick();
        boolean p = isCurrentPositionCorrect();
        if (BuildConfig.DEBUG && !p) {
            throw new AssertionError();
        }
        songList.trimToSize();
        assertTrue(currentPosition < songList.size());
    }

    @UiThreadTest
    public void testPreviousTrackButton_1() throws Exception {
        Button mButton = (Button) myActivity.findViewById(R.id.previousTrackBt);
        songList.add(mySong);
        mButton.performClick();
        mButton.performClick();
        boolean p = isCurrentPositionCorrect();
        if (BuildConfig.DEBUG && !p) {
            throw new AssertionError();
        }
        songList.trimToSize();
        assertTrue(currentPosition < songList.size());
    }

    @UiThreadTest
    public void testPreviousTrackButton_2() {
        TextView tvResponse = (TextView) myActivity.findViewById(R.id.tvResponse);
        Button mButton = (Button) myActivity.findViewById(R.id.previousTrackBt);
        mButton.performClick();
        assertEquals(tvResponse.getText().toString(), "a");
    }

}



/*
public class ApplicationTest extends MyMusicPlayerTestCase2<MainActivity> {
    public ApplicationTest() {
        super(MainActivity.class);
    }

    public void testListPrevious() {
        Activity activity = getActivity();
        //Button btnNext = (Button) activity.findViewById(R.id.nextTrack);
        Button btnPrev = (Button) activity.findViewById(R.id.previousTrack);

        int listSize = MainActivity.songList.size();
        int index = MainActivity.currentPosition;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //nextTrack(btnNext);
                previousTrack(btnPrev);
                assertEquals(listSize > 1 && index == 1,index == 0);
            }
        });
        activity.finish();

    }
}*/
