package com.uddernetworks.emoji.player.video;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import com.gmail.justinxvopro.chestercinema.moviesystem.VideoBuffer;
import com.uddernetworks.emoji.ffmpeg.FFmpegManager;
import com.uddernetworks.emoji.gif.VideoGifProcessor;

public interface Video {

    FFmpegManager getfFmpegManager();

    VideoGifProcessor getVideoGifProcessor();

    File getVideoFile();

    String getTitle();

    /**
     * Returns the length of the video, in seconds.
     *
     * @return The seconds of the video
     */
    int getLength();

    CompletableFuture<File> convertToGif(int offset, int duration);
    
    VideoBuffer getBuffer();
    
//    void setBuffer(VideoBuffer buffer);
}
