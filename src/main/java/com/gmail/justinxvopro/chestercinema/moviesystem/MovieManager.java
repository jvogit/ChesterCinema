package com.gmail.justinxvopro.chestercinema.moviesystem;

import java.util.HashMap;
import java.util.Map;

import com.gmail.justinxvopro.chestercinema.Loggable;
import com.uddernetworks.emoji.player.video.Video;
import com.uddernetworks.emoji.player.video.VideoPlayerState;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class MovieManager implements Loggable {
    private static Map<Guild, MovieManager> mapped = new HashMap<>();
    @Getter
    private MoviePlayer current;
    
    public void playMovie(TextChannel text, VoiceChannel voice, Video video) {
	if(current != null && current.getVideoState() != VideoPlayerState.END) {
	    text.sendMessage("You are already playing a movie!").queue();
	    return;
	}
	
	MoviePlayer player = new MoviePlayer(video);
	this.current = player;
	Message msg = text.sendMessage("Loading video. . .").complete();
	player.start(text, voice);
	logger().info("Started movie");
	msg.delete().queue();
    }
    
    public static MovieManager getMovieManager(Guild g) {
	if(!mapped.containsKey(g)) {
	    mapped.put(g, new MovieManager());
	}
	
	return mapped.get(g);
    }
}
