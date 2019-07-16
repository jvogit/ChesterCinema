package com.gmail.justinxvopro.chestercinema.moviesystem;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.gmail.justinxvopro.chestercinema.Loggable;
import com.gmail.justinxvopro.chestercinema.util.AudioResultHandler;
import com.gmail.justinxvopro.chestercinema.util.MovieTrackHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.uddernetworks.emoji.player.video.Video;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

@RequiredArgsConstructor
public class MovieAudioPlayer implements Loggable {
    private static final AudioPlayerManager audioManager = new DefaultAudioPlayerManager();
    static {
	AudioSourceManagers.registerLocalSource(audioManager);
    }
    private final Video video;
    private AudioPlayer player;
    private File audioFile;
    private Guild guild;
    
    public void loadThenPlay(@NonNull VoiceChannel channel, @NonNull Consumer<AudioTrack> onStart) {
	this.guild = channel.getGuild();
	createAudio(file ->{
	    this.audioFile = file;
	    loadAudio(track ->{
		var gAudioManager = channel.getGuild().getAudioManager();
		if(!channel.getGuild().getSelfMember().getVoiceState().inVoiceChannel() || (!gAudioManager.isConnected() && !gAudioManager.isAttemptingToConnect())) {
		    gAudioManager.openAudioConnection(channel);
		}
		playAudio(channel.getGuild(), track, onStart);
	    });
	});
    }
    
    public void pause() {
	this.player.setPaused(true);
    }
    
    public void unpause() {
	this.player.setPaused(false);
    }
    
    public boolean hasStopped() {
	return this.player.getPlayingTrack() == null;
    }
    
    public void stop() {
	this.player.destroy();
	this.guild.getAudioManager().closeAudioConnection();
    }
    
    public void setposition(long l) {
	this.player.getPlayingTrack().setPosition(l);
    }
    
    private void playAudio(Guild g, @NonNull AudioTrack track, @NonNull Consumer<AudioTrack> onStart) {
	this.player = audioManager.createPlayer();
	g.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(this.player));
	this.player.addListener(new MovieTrackHandler(onStart));
	this.player.startTrack(track, false);
    }
    
    private void loadAudio(@NonNull Consumer<AudioTrack> onLoad) {
	audioManager.loadItem(audioFile.getAbsolutePath(), new AudioResultHandler((track)->{
	    onLoad.accept(track);
	}));
    }
    
    private void createAudio(@NonNull Consumer<File> done) {
	generateAudio().thenAccept(done);
    }
    
    private CompletableFuture<File> generateAudio() {
        return CompletableFuture.supplyAsync(() -> {
            var outDir = new File("audio");
            outDir.mkdirs();
            var outFile = new File(outDir, video.getVideoFile().getName() + ".mp3");
            if (outFile.exists()) {
                logger().info("{} already exists, skipping!", outFile.getAbsolutePath());
                return outFile;
            }

            video.getfFmpegManager().createJob(new FFmpegBuilder()
                    .setInput(video.getVideoFile().getAbsolutePath())
                    .addOutput(outFile.getAbsolutePath())
                    .addExtraArgs("-f", "mp3", "-ab", "192000")
                    .done());
            return outFile;
        });
    }
}
