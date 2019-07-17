package com.gmail.justinxvopro.chestercinema.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class YouTubeDL {
    public static void download(String query, File output_dir, String format, Consumer<String> consumer)
	    throws IOException, InterruptedException {
	String command = "youtube-dl " + "\"" + (query.startsWith("https://") ? query : "ytsearch:" + query) + "\""
		+ " -f bestvideo[ext=mp4][height=480]+bestaudio[ext!=webm]/best[ext!=webm] --output \"" + format + "\"";
	System.out.println(command);
	
	runCommand(command, output_dir, consumer);
    }
    
    private static void runCommand(String command, File dir, Consumer<String> consumer) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command, null, dir);

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = input.readLine()) != null) consumer.accept(line);
        }
    }
}
