package com.google;

import java.util.*;

public class VideoPlayer {

  private final VideoLibrary videoLibrary;
  private final VideoPlaylistLibrary videoPlaylistLibrary;

  //A field to store the current video being played so it can be stopped at any point.
  private static Video currentVideo;
  // A field to indicate whether or not the current video is paused.
  private static boolean videoPaused;

  private HashMap<String, String> flaggedVideos = new HashMap<>();

  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
    this.videoPlaylistLibrary = new VideoPlaylistLibrary();
    currentVideo = null;
    videoPaused = false;
  }

  /**
   * Display the number of videos in the video library.
   */
  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public String getVideoAsString(Video video){
    String videoString = video.getTitle() + " (" +video.getVideoId() + ") [";

    String tags = "";

    for (String tag:video.getTags()){
      tags += tag;
    }
    tags = tags.replace("#", " #").trim() + "]";

    videoString += tags ;

    return videoString;
  }

  /**
   * Display all videos, including their titles, id's and tags, from the video library.
   */
  public void showAllVideos() {
    List<Video> videos = new ArrayList<>(videoLibrary.getVideos());

    videos.sort(new Comparator<Video>() {
      @Override
      public int compare(Video v1, Video v2) {
        return v1.getTitle().compareTo(v2.getTitle());
      }

    });

    System.out.println("Here's a list of all available videos:");

    for (Video v:videos) {
      String flag = "";
      if (flaggedVideos.get(v.getVideoId()) != null){
        flag = " - FLAGGED (reason: " + flaggedVideos.get(v.getVideoId()) + ")";
      }
      System.out.println(getVideoAsString(v) + flag);
    }
  }
  /**
   * Play a given video. If a video is already playing, stop it first.
   * @param videoId The id of the video to be played.
   */
  public void playVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);

    if (video==null){
      System.out.println("Cannot play video: Video does not exist");
      return;
    }
    if (flaggedVideos.get(videoId) != null){
      System.out.printf("Cannot play video: Video is currently flagged (reason: %s)%n", flaggedVideos.get(videoId));
    }
    else {
      if (currentVideo!=null){
        System.out.println("Stopping video: " + currentVideo.getTitle());
      }
      currentVideo = video;
      System.out.println("Playing video: " + video.getTitle());
      videoPaused = false;
    }
  }

  /**
   * Stop the video currently playing, if any.
   * If a video has been stopped, set the currentVideo field to null to
   * indicate that there is no video playing now.
   */
  public void stopVideo() {
    if (currentVideo!=null) {
      System.out.println("Stopping video: " + currentVideo.getTitle());
      currentVideo = null;
    }
    else{
      System.out.println("Cannot stop video: No video is currently playing");
    }
  }

  /**
   * Choose a random video from the video library and play it.
   */
  public void playRandomVideo() {
    Random rand = new Random();

    List<Video> videos = videoLibrary.getVideos();

    if (videos.size() == 0){
      System.out.println("No videos available");
    }
    else {
      int randomIndex = rand.nextInt(videos.size() - 1);
      String videoId = videos.get(randomIndex).getVideoId();

      if (flaggedVideos.get(videoId) != null){
        System.out.println("No videos available");
        return;
      }
      playVideo(videoId);
    }
  }


  /**
   * Pause the video currently playing, if any.
   */
  public void pauseVideo() {
    if (currentVideo == null){
      System.out.println("Cannot pause video: No video is currently playing");
    }
    else if (videoPaused){
      System.out.println("Video already paused: "+ currentVideo.getTitle());
    }
    else {
      System.out.println("Pausing video: " + currentVideo.getTitle());
      videoPaused = true;
    }
  }

    /**
     * Continue the video that is currently paused, if any.
     */
  public void continueVideo() {
    if (currentVideo==null){
      System.out.println("Cannot continue video: No video is currently playing");
      return;
    }

    if (videoPaused){
      if (flaggedVideos.get(currentVideo.getVideoId()) != null){
        currentVideo = null;
        System.out.println("Cannot continue video: No video is currently playing");
        return;
      }
      System.out.println("Continuing video: " + currentVideo.getTitle());
      videoPaused = false;
    }
    else{
      System.out.println("Cannot continue video: Video is not paused");
    }
  }

  /**
   * Print out the video currently playing, if any.
   */
  public void showPlaying() {
    String pauseMessage = "";
    if (currentVideo==null){
      System.out.println("No video is currently playing");
    }
    else {
      if (videoPaused){
        if (flaggedVideos.get(currentVideo.getVideoId()) != null){
          currentVideo = null;
          System.out.println("Cannot continue video: No video is currently playing");
          return;
        }
        pauseMessage = " - PAUSED";
      }
      System.out.printf("Currently playing: %s%s%n", getVideoAsString(currentVideo), pauseMessage);
    }
  }

  /**
   * Create a new unique playlist with no whitespace and add it to a collection.
   * @param playlistName
   */
  public void createPlaylist(String playlistName) {
    if (playlistName.contains(" ")){
      System.out.println("Cannot create playlist: Playlist name cannot contain whitespace");
      return;
    }

    if (videoPlaylistLibrary.getVideoPlaylists().get(getPlaylistName(playlistName)) != null){
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
      return;
    }

    System.out.println("Successfully created new playlist: " + playlistName);
    VideoPlaylist playlist = new VideoPlaylist(playlistName);
    videoPlaylistLibrary.addPlaylist(playlist);
  }

  /**
   * Get the stored playlist name which matches the given playlist name which may have different cases.
   * @param playlist
   * @return The stored playlist name if exists, null otherwise.
   */
  private String getPlaylistName(String playlist){
    String playlistName = null;

    for (String pl:videoPlaylistLibrary.getVideoPlaylists().keySet()) {
      if (pl.toLowerCase().equals(playlist.toLowerCase())) {
        playlistName = pl;
      }
    }
    return playlistName;
  }

  /**
   * Add a new video to a playlist if that video and playlist exist.
   * @param playlistName
   * @param videoId
   */
  public void addVideoToPlaylist(String playlistName, String videoId) {
    if (getPlaylistName(playlistName) == null){
      System.out.printf("Cannot add video to %s: Playlist does not exist%n", playlistName);
      return;
    }

    if (videoLibrary.getVideo(videoId) == null){
      System.out.printf("Cannot add video to %s: Video does not exist%n", playlistName);
      return;
    }

    if (flaggedVideos.get(videoId) != null){
      System.out.printf("Cannot add video to %s: Video is currently flagged (reason: %s)%n", playlistName, flaggedVideos.get(videoId));
      return;
    }

    if (videoPlaylistLibrary.getVideoPlaylists().get(getPlaylistName(playlistName)).getVideos().contains(videoId)){
      System.out.printf("Cannot add video to %s: Video already added%n", playlistName);
      return;
    }

    videoPlaylistLibrary.getVideoPlaylists().get(getPlaylistName(playlistName)).videos.add(videoId);
    System.out.printf("Added video to %s: %s%n", playlistName, videoLibrary.getVideo(videoId).getTitle());
  }

  /**
   * Display all playlist names, if any.
   */
  public void showAllPlaylists() {
    if (videoPlaylistLibrary.getVideoPlaylists().size()==0){
      System.out.println("No playlists exist yet");
    }
    else{
      System.out.println("Showing all playlists:");
      for (String pl:videoPlaylistLibrary.getVideoPlaylists().keySet()){
        System.out.println(pl);
      }
    }
  }

  /**
   * Show the videos in a given playlist, if any.
   * Show an error if the playlist doesn't exist.
   * @param playlistName
   */
  public void showPlaylist(String playlistName) {
    if (getPlaylistName(playlistName) == null){
      System.out.printf("Cannot show playlist %s: Playlist does not exist%n", playlistName);
      return;
    }

    System.out.printf("Showing playlist: %s%n", playlistName);

    List<String> videoIds = videoPlaylistLibrary.getVideoPlaylists().get(getPlaylistName(playlistName)).getVideos();

    if (videoIds.size()==0){
      System.out.println("No videos here yet");
      return;
    }

    for (String videoId:videoIds){
      String flag = "";
      if (flaggedVideos.get(videoId) != null){
        flag = " - FLAGGED (reason: " + flaggedVideos.get(videoId) + ")";
      }
        Video video = videoLibrary.getVideo(videoId);

        System.out.println(getVideoAsString(video) + flag);
    }
  }

  /**
   * Remove a video from a playlist if the video and playlist exist.
   * Show an error if the video is not in the playlist.
   * @param playlistName
   * @param videoId
   */
  public void removeFromPlaylist(String playlistName, String videoId) {
    if (getPlaylistName(playlistName) == null){
      System.out.printf("Cannot remove video from %s: Playlist does not exist%n", playlistName);
      return;
    }
    if (videoLibrary.getVideo(videoId) == null){
      System.out.printf("Cannot remove video from %s: Video does not exist%n", playlistName);
      return;
    }

    if(videoPlaylistLibrary.getVideoPlaylists().get(getPlaylistName(playlistName)).getVideos().remove(videoId)){
      System.out.printf("Removed video from %s: %s%n", playlistName, videoLibrary.getVideo(videoId).getTitle());
    }
    else{
      System.out.printf("Cannot remove video from %s: Video is not in playlist%n", playlistName);
    }
  }

  /**
   * Remove all videos from a given playlist, if it exists.
   * @param playlistName
   */
  public void clearPlaylist(String playlistName) {
    if (getPlaylistName(playlistName) == null){
      System.out.printf("Cannot clear playlist %s: Playlist does not exist%n", playlistName);
    }
    else {
      videoPlaylistLibrary.getVideoPlaylists().get(getPlaylistName(playlistName)).getVideos().clear();
      System.out.printf("Successfully removed all videos from %s%n", playlistName);
    }
  }

  /**
   * Delete a playlist, if it exists.
   * @param playlistName
   */
  public void deletePlaylist(String playlistName) {
    if (getPlaylistName(playlistName) == null){
      System.out.printf("Cannot delete playlist %s: Playlist does not exist", playlistName);
    }
    else{
      videoPlaylistLibrary.getVideoPlaylists().remove(playlistName);
      System.out.printf("Deleted playlist: %s%n", playlistName);
    }
  }

  /**
   * Search the collection of videos for the search term, displaying the matching videos.
   * Allow the user to play one of them if they choose.
   * @param searchTerm
   */
  public void searchVideos(String searchTerm) {
    List<String> matches = new ArrayList<>();
    List<Video> videoMatches = new ArrayList<>();

    for (Video v:videoLibrary.getVideos()){
      if (v.getTitle().toLowerCase().contains(searchTerm.toLowerCase())){
        if (flaggedVideos.get(v.getVideoId()) != null){
          continue;
        }
        matches.add(getVideoAsString(v));
        videoMatches.add(v);
      }
    }

    Collections.sort(matches);

    if (matches.size()>0) {
      System.out.printf("Here are the results for %s:%n", searchTerm);
      for (int i=0; i<matches.size(); i++){
        System.out.printf("%s) %s%n", i+1, matches.get(i));
      }

      System.out.println("Would you like to play any of the above? If yes, specify the number of the video.");
      System.out.println("If your answer is not a valid number, we will assume it's a no.");
      Scanner scanner = new Scanner(System.in);
      var input = scanner.nextLine();

      try {
        Integer.parseInt(input);
      }
      catch (Exception e){
        return;
      }
      if (input == null || Integer.parseInt(input)>matches.size()){
        return;
      }

      String s = matches.get(Integer.parseInt(input)-1);
      s = s.split("\\(")[1];
      s = s.split("\\)")[0];

      playVideo(s);

    }
    else{
      System.out.println("No search results for " + searchTerm);
    }
  }

  /**
   * Search the collection of videos for the given tag, displaying the matching videos.
   * Allow the user to play one of them if they choose.
   * @param videoTag
   */
  public void searchVideosWithTag(String videoTag) {
    if (!videoTag.contains("#")){
      System.out.println("No search results for " + videoTag);
      return;
    }

    List<String> matches = new ArrayList<>();
    List<Video> videoMatches = new ArrayList<>();

    for (Video v:videoLibrary.getVideos()){
      if (v.getTags().contains(videoTag.toLowerCase())){
        if (flaggedVideos.get(v.getVideoId()) != null){
          continue;
        }
        matches.add(getVideoAsString(v));
        videoMatches.add(v);
      }
    }

    Collections.sort(matches);

    if (matches.size()>0) {
      System.out.printf("Here are the results for %s:%n", videoTag);
      for (int i=0; i<matches.size(); i++){
        System.out.printf("%s) %s%n", i+1, matches.get(i));
      }

      System.out.println("Would you like to play any of the above? If yes, specify the number of the video.");
      System.out.println("If your answer is not a valid number, we will assume it's a no.");

      Scanner scanner = new Scanner(System.in);
      var input = scanner.nextLine();

      try {
        Integer.parseInt(input);
      }
      catch (Exception e){
        return;
      }
      if (input == null || Integer.parseInt(input)>matches.size()-1){
        return;
      }

      String s = matches.get(Integer.parseInt(input)-1);
      s = s.split("\\(")[1];
      s = s.split("\\)")[0];

      playVideo(s);

    }
    else{
      System.out.println("No search results for " + videoTag);
    }
  }

  /**
   * Flag a video without a reason, if that video exists and is not already flagged.
   * @param videoId
   */
  public void flagVideo(String videoId) {
    if (videoLibrary.getVideo(videoId) == null){
      System.out.println("Cannot flag video: Video does not exist");
      return;
    }
    if (flaggedVideos.get(videoId) != null){
      System.out.println("Cannot flag video: Video is already flagged");
      return;
    }
    if (currentVideo != null && currentVideo.getVideoId().equals(videoId)){
      stopVideo();
    }
    else{
      flaggedVideos.put(videoId, "Not supplied");
      System.out.printf("Successfully flagged video: %s (reason: %s)%n", videoLibrary.getVideo(videoId).getTitle(), flaggedVideos.get(videoId));
    }
  }

  /**
   * Flag a video with a reason, if that video exists and if not already flagged.
   * @param videoId
   * @param reason
   */
  public void flagVideo(String videoId, String reason) {
    if (videoLibrary.getVideo(videoId) == null){
      System.out.println("Cannot flag video: Video does not exist");
      return;
    }
    if (flaggedVideos.get(videoId) != null){
      System.out.println("Cannot flag video: Video is already flagged");
    }
    else{
      flaggedVideos.put(videoId, reason);
      if (currentVideo != null && currentVideo.getVideoId().equals(videoId)){
        stopVideo();
      }
      System.out.printf("Successfully flagged video: %s (reason: %s)%n", videoLibrary.getVideo(videoId).getTitle(), flaggedVideos.get(videoId));
    }
  }

  /**
   * Remove a flag from a video, if that video and flag exist.
   * @param videoId
   */
  public void allowVideo(String videoId) {
    if (videoLibrary.getVideo(videoId) == null){
      System.out.println("Cannot remove flag from video: Video does not exist");
      return;
    }
    if (flaggedVideos.get(videoId) == null){
      System.out.println("Cannot remove flag from video: Video is not flagged");
      return;
    }
    flaggedVideos.remove(videoId);
    System.out.println("Successfully removed flag from video: " + videoLibrary.getVideo(videoId).getTitle());
  }
}