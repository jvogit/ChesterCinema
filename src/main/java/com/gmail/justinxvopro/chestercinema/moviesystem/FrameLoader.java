package com.gmail.justinxvopro.chestercinema.moviesystem;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.gmail.justinxvopro.chestercinema.BotCore;
import com.gmail.justinxvopro.chestercinema.Config;
import com.gmail.justinxvopro.chestercinema.Loggable;
import com.gmail.justinxvopro.chestercinema.moviesystem.VideoBuffer.SavedVideoBuffer;
import com.uddernetworks.emoji.player.video.Video;
import com.uddernetworks.emoji.player.video.VideoPlayerState;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class FrameLoader implements Loggable {
    private Executor executor = Executors.newFixedThreadPool(2);
    @NonNull
    private MoviePlayer player;
    
    public void load() {
	var video = player.getVideo();
	var frames = video.getBuffer().size();
	for (int i = 0; i < frames; i++) {
	    int frame = i;
	    if (!video.getBuffer().get(i).isPresent()) {
		logger().info("{} Frame {} unloaded! Loading. . .", video.getTitle(), frame);
		executor.execute(() -> {
		    if(player.getVideoState() == VideoPlayerState.END) {
			return;
		    }
		    logger().info("Executing loading frame " + frame);
		    video.getBuffer().add(frame,
			    this.getVideoFrameGifLink(frame * MoviePlayer.SEEK_DURATION_IN_SECONDS));
		    
		    if(video.getBuffer().allFilled() || frame % 20 == 0) {
			logger().info("Saving frames");
			saveBufferFrames(video);
			logger().info("Frames saved!");
		    }
		});
	    }
	}
	
	logger().info("Done queuing loads.");
    }
    
    public CompletableFuture<String> loadFrame(int frame) {
	var video = player.getVideo();
	return video.getBuffer().get(frame).map(CompletableFuture::completedFuture).orElseGet(() -> {
	    logger().info("{} Frame {} unloaded! Loading. . .", video.getTitle(), frame);
	    return CompletableFuture.supplyAsync(() -> {
		String frameUrl = this.getVideoFrameGifLink(frame * MoviePlayer.SEEK_DURATION_IN_SECONDS);
		video.getBuffer().add(frame, frameUrl);

		if (video.getBuffer().allFilled()) {
		    logger().info("Saving frames");
		    saveBufferFrames(video);
		    logger().info("Frames saved!");
		}

		return frameUrl;
	    }, executor);
	});
    }

    @SneakyThrows
    private String getVideoFrameGifLink(int seek) {
	var video = player.getVideo();
	File file = video.convertToGif(seek, MoviePlayer.SEEK_DURATION_IN_SECONDS).get();
	return BotCore.BOT_JDA.getTextChannelById(Config.getInstance().getGif_output_textchannel_id()).sendFile(file).complete().getAttachments()
		.get(0).getUrl();
    }
    
    public static CompletableFuture<File> saveBufferFrames(Video video) {
	return CompletableFuture.supplyAsync(() -> {
	    var outDir = new File("frames");
	    outDir.mkdirs();
	    File file = new File(outDir, video.getVideoFile().getName() + ".json");
	    try {
		saveFile(file, video.getBuffer());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    
	    return file;
	});
    }
    
    public static void loadBuffer(Video video) throws JsonParseException, JsonMappingException, IOException {
	var outDir = new File("frames");
	var file = new File(outDir, video.getVideoFile().getName() + ".json");
	
	if(file.exists()) {
	    Loggable.logger(FrameLoader.class).info("Buffered Frames exists! Using them");
	    var sb = BotCore.OBJECT_MAPPER.readValue(file, SavedVideoBuffer.class);
	    video.getBuffer().setBuffer(sb.getBuffer());
	    video.getBuffer().setFRAME_DURATION(sb.getFrame_duration());
	}
    }
    
    private static void saveFile(File file, VideoBuffer buffer) throws IOException {
	file.createNewFile();
	BotCore.OBJECT_MAPPER.writeValue(file, SavedVideoBuffer.toImmutableSave(buffer));
    }
}
