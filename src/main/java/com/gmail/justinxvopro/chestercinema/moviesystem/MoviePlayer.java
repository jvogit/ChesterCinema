package com.gmail.justinxvopro.chestercinema.moviesystem;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.gmail.justinxvopro.chestercinema.Loggable;
import com.gmail.justinxvopro.chestercinema.util.ThreadUtil;
import com.uddernetworks.emoji.player.video.Video;
import com.uddernetworks.emoji.player.video.VideoPlayerState;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

@RequiredArgsConstructor
public class MoviePlayer implements Loggable {
    private static final String LOADING_GIF = "https://cdn.discordapp.com/attachments/594376521175531530/594376568961237011/433d83f7e481f35245f8c6bb7c7591d8.gif";
    public static final int SEEK_DURATION_IN_SECONDS = 10;
    @Getter
    private final Video video;
    private MovieAudioPlayer audio;
    private TextChannel output;
    private VoiceChannel audiochannel;
    @Getter
    @Setter
    private volatile VideoPlayerState videoState = VideoPlayerState.EMPTY;
    private Message playerMessage;
    private FrameLoader frameLoader;
    private volatile int SEEK_IN_SECONDS = 0;
    private volatile String CURRENT_FRAME = LOADING_GIF;

    public void start(TextChannel output, VoiceChannel audio) {
	this.output = output;
	this.audiochannel = audio;
	this.audio = new MovieAudioPlayer(video);

	initializationThenTick(true);
    }

    public void stop(int remaining) {
	if(videoState == VideoPlayerState.END) return;
	videoState = VideoPlayerState.END;
	this.audio.stop();
	this.playerMessage.editMessage(getMessage(LOADING_GIF)).queueAfter(remaining, TimeUnit.SECONDS, (msg) -> {
	    this.playerMessage.delete().queueAfter(2, TimeUnit.SECONDS);
	});
    }

    public void initializationThenTick(boolean preloadsync) {
	createPlayer(CURRENT_FRAME);
	frameLoader = new FrameLoader(this);
	try {
	    frameLoader.loadFrame(0).get();
	    frameLoader.load();
	    if(!this.video.getBuffer().allFilled()) {
		ThreadUtil.delay(10000);
	    }
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}
	var gAudioManager = audiochannel.getGuild().getAudioManager();
	if(!audiochannel.getGuild().getSelfMember().getVoiceState().inVoiceChannel() || (!gAudioManager.isConnected() && !gAudioManager.isAttemptingToConnect())) {
	    gAudioManager.openAudioConnection(audiochannel);
	}
	this.audio.loadThenPlay(audiochannel, track -> {
	    CompletableFuture.runAsync(()->this.tick(preloadsync));
	});
    }

    public void tick(boolean audiosync) {
	var nextFrame = this.SEEK_IN_SECONDS / 10;
	logger().info("Running {} : {}", this.SEEK_IN_SECONDS, nextFrame);
	if (isDone()) {
	    logger().info("Trying to stop!");
	    stop(0);
	    return;
	}
	if(audiosync) {
	    this.audio.setposition(nextFrame * 10 * 1000);
	}
	var before = System.currentTimeMillis();
	var offset = 0l;
	this.CURRENT_FRAME = this.getFromBuffer(nextFrame).orElse(null);
	while ((this.CURRENT_FRAME = this.getFromBuffer(nextFrame).orElse(null)) == null) {
	    this.audio.pause();
	    updatePlayer(LOADING_GIF);
	    logger().warn("Current Frame is null!");
	    ThreadUtil.delay(20000);
	}
	this.audio.unpause();
	logger().info("Loading frame " + (nextFrame));
	this.updatePlayer(CURRENT_FRAME);
	offset = System.currentTimeMillis() - before;
	logger().info("Loaded frame " + (nextFrame));
	long delay = MoviePlayer.SEEK_DURATION_IN_SECONDS * 1000l;
	this.SEEK_IN_SECONDS += MoviePlayer.SEEK_DURATION_IN_SECONDS;
//	if (this.SEEK_IN_SECONDS >= video.getLength()) {
//	    delay = (this.SEEK_IN_SECONDS - video.getLength()) * 1000l;
//	}
	logger().info("Delaying. . . offset - " + offset);
	ThreadUtil.delay(delay <= 0 ? 1 : delay);
	tick(audiosync);
    }

    private Optional<String> getFromBuffer(int frame) {
	return this.video.getBuffer().get(frame);
    }

    private boolean isDone() {
	return SEEK_IN_SECONDS >= video.getLength() || getVideoState() == VideoPlayerState.END;
    }

    private Message createPlayer(String frameUrl) {
	return this.playerMessage = output.sendMessage(getMessage(frameUrl)).complete();
    }

    private void updatePlayer(String frameUrl) {
	logger().info(frameUrl + " edit!");
	playerMessage.editMessage(getMessage(frameUrl)).queue();
    }

    private MessageEmbed getMessage(String frameUrl) {
	EmbedBuilder builder = new EmbedBuilder();

	builder.setImage(frameUrl);

	return builder.build();
    }

}
