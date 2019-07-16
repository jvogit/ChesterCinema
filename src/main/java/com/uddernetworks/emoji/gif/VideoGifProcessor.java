package com.uddernetworks.emoji.gif;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.uddernetworks.emoji.ffmpeg.FFmpegManager;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

// This class was written hastily, and doesn't follow good code-reusing techniques and whatever, and assumes the com.uddernetworks.emoji.emoji package is gone
public class VideoGifProcessor {
    private FFmpegManager fFmpegManager;

    public VideoGifProcessor(FFmpegManager fFmpegManager) throws IOException {
        this.fFmpegManager = fFmpegManager;
    }

    public CompletableFuture<File> convertVideoToGif(File video, int offset, int duration) {
        return CompletableFuture.supplyAsync(() -> {
            var outDir = new File("gifs");
            outDir.mkdirs();
            var outFile = new File(outDir.getAbsolutePath(), + offset + "_" + duration + ".gif");
            this.fFmpegManager.createJob(new FFmpegBuilder()
                    .setInput(video.getAbsolutePath())
                    .addOutput(outFile.getAbsolutePath())
                    .addExtraArgs("-r", "15", "-hide_banner", "-vf", "scale=320:-1", "-ss", String.valueOf(offset), "-t", String.valueOf(duration))
                    .done());
            return outFile;
        });

    }

}
