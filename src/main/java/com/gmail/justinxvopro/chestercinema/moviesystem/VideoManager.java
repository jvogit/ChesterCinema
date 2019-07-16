package com.gmail.justinxvopro.chestercinema.moviesystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import com.gmail.justinxvopro.chestercinema.Loggable;
import com.uddernetworks.emoji.ffmpeg.FFmpegManager;
import com.uddernetworks.emoji.gif.VideoGifProcessor;
import com.uddernetworks.emoji.player.video.Video;
import com.uddernetworks.emoji.player.video.VideoCreator;

public class VideoManager {
    private static List<Video> videos = new ArrayList<>();
    private static Logger LOGGER = Loggable.logger(VideoManager.class);
    private static FFmpegManager FFMPEG;
    private static VideoGifProcessor GIF_PROCESS;
    private static VideoCreator CREATOR;
    
    static {
	try {
	    FFMPEG = new FFmpegManager();
	    GIF_PROCESS = new VideoGifProcessor(FFMPEG);
	    CREATOR = new VideoCreator(FFMPEG, GIF_PROCESS);
	    LOGGER.info("Successfuly created VideoCreator");
	} catch (IOException e) {
	    e.printStackTrace();
	    LOGGER.error("No FFMPEG was found.");
	    System.exit(1);
	}
    }
    
    public static void load() {
	videos.clear();
        var videoDirectory = new File("videos");
        videoDirectory.mkdirs();

        for (var file : Objects.requireNonNull(videoDirectory.listFiles())) {
            if (file.isDirectory()) continue;

            try {
        	Video video = CREATOR.createVideo(file);
        	FrameLoader.loadBuffer(video);
                videos.add(video);
            } catch (Exception e) {
                e.printStackTrace();
                Loggable.logger(VideoManager.class).info("Error has occured while loading video {} : {}", file.getName(), e.getMessage());
            }
        }

        LOGGER.info("Got {} videos", videos.size());
    }
    
    public static Optional<Video> getVideo(String name) {
	LOGGER.info("comapre " + name);
        for (var video : videos) {
            String title = FilenameUtils.getBaseName(video.getVideoFile().getAbsolutePath());
            LOGGER.info("title " + title);
            if (title.toLowerCase().equals(name.toLowerCase())) {
                return Optional.of(video);
            }
        }
        return Optional.ofNullable(null);
    }
}
