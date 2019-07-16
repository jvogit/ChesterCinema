package com.gmail.justinxvopro.chestercinema.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class YouTubeDL {
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    
    public static int download(String query, String output_dir, String format, Consumer<String> consumer)
	    throws IOException, InterruptedException {
	ProcessBuilder builder = new ProcessBuilder();
	String command = "youtube-dl " + "\"" + (query.startsWith("https://") ? query : "ytsearch:" + query) + "\""
		+ " -f bestvideo[ext=mp4][height=480]+bestaudio[ext!=webm]/best[ext!=webm] --output \"" + format + "\"";
	System.out.println(command);
	if (isWindows) {
	    builder.command("cmd.exe", "/c", command);
	} else {
	    builder.command("sh", "-c", command);
	}
	builder.directory(new File(output_dir));
	Process process = builder.start();
	StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), consumer);
	streamGobbler.run();
	return process.waitFor();
    }

    public static List<String> get_id_title(String query) throws IOException {
	ProcessBuilder builder = new ProcessBuilder();
	String command = "youtube-dl " + "\"" + "ytsearch:" + query + "\"" + " --get-id --get-title";
	System.out.println(command);
	if (isWindows) {
	    builder.command("cmd.exe", "/c", command);
	} else {
	    builder.command("sh", "-c", command);
	}
	Process process = builder.start();
	List<String> to = new ArrayList<>();
	System.out.println("ran!");
	try (BufferedReader reader = new BufferedReader(
		new InputStreamReader(process.getInputStream()))) {
	    String line;
	    while ((line = reader.readLine()) != null) {
		System.out.println(line);
		to.add(line);
	    }
	}catch(Exception e) {
	    e.printStackTrace();
	}
	System.out.println("end");
	return to;
    }
}
