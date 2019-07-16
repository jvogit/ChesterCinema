package com.gmail.justinxvopro.chestercinema.util;

import java.util.function.Consumer;

import com.gmail.justinxvopro.chestercinema.Loggable;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AudioResultHandler implements AudioLoadResultHandler, Loggable {
    @NonNull
    public Consumer<AudioTrack> onLoad;
    
    @Override
    public void trackLoaded(AudioTrack track) {
	logger().info("{} AudioTrack Loaded", track.getIdentifier());
	onLoad.accept(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
	this.loadFailed(new FriendlyException("Loaded playlist", Severity.COMMON, new Exception()));
    }

    @Override
    public void noMatches() {
	this.loadFailed(new FriendlyException("No matches", Severity.COMMON, new Exception()));
    }

    @Override
    public void loadFailed(FriendlyException exception) {
	logger().info("Load failed {} : {}", exception.getCause(), exception.getMessage());
    }

}
