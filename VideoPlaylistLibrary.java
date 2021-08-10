package com.google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoPlaylistLibrary {

    private static HashMap<String, VideoPlaylist> videoPlaylists;

    VideoPlaylistLibrary(){
        this.videoPlaylists = new HashMap<>();
    }

    /**
     * @return videoPlaylists.
     */
    HashMap<String, VideoPlaylist> getVideoPlaylists(){
        return videoPlaylists;
    }

    /**
     * Add a playlist to the collection of playlists.
     * @param playlist
     */
    public void addPlaylist(VideoPlaylist playlist){
        videoPlaylists.put(playlist.getPlaylistName(), playlist);
    }
}
