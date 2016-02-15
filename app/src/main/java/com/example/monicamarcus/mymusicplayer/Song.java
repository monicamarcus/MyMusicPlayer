package com.example.monicamarcus.mymusicplayer;

/**
 * Created by monicamarcus on 2/9/16.
 */
public class Song {
    private String trackURL;
    private String trackName;
    private String artistName;
    private String imageURL;

    public Song() {
        trackURL = "";
        trackName = "";
        artistName = "";
        imageURL = "";
    }

    public Song(String trackURL, String trackName, String artistName, String imageURL) {
        this.trackURL = trackURL;
        this.trackName = trackName;
        this.artistName = artistName;
        this.imageURL = imageURL;
    }

    public void setTrackURL(String trackURL) {
        this.trackURL = trackURL;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTrackURL() {
        return trackURL;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String toString() {
        return "TRACK NAME: " + trackName + "\n" + "ARTIST NAME: " + artistName;// + "\n" + "TRACK URL: " + trackURL;
    }
}
