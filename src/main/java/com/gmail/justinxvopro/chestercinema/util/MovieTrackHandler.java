package com.gmail.justinxvopro.chestercinema.util;

import java.util.function.Consumer;

import com.gmail.justinxvopro.chestercinema.Loggable;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * This class schedules tracks for the audio player. It contains the queue of
 * tracks.
 */
@RequiredArgsConstructor
public class MovieTrackHandler extends AudioEventAdapter implements Loggable {
    @NonNull
    private Consumer<AudioTrack> onStart;
    
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
	logger().info("Playing. . . " + track.getIdentifier());
	onStart.accept(track);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    }
    
}
