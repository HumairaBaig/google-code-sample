package com.google;

import java.util.ArrayList;
import java.util.List;

/** A class used to represent a Playlist */
class VideoPlaylist {
    private final String playlistName;

    List<String> videos = new ArrayList<>();

    VideoPlaylist(String playlistName){
        this.playlistName = playlistName;
    }

    /**
     * Add a videoId to a playlist.
     * @param videoId
     */
    public void addToVideos(String videoId){
        if (videoId != null) {
            videos.add(videoId);
        }
    }

    /**
     * @return playListName.
     */
    String getPlaylistName(){
        return playlistName;
    }

    /**
     * @return videos.
     */
    List<String> getVideos(){
        return videos;
    }
}
