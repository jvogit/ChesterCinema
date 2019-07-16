package com.gmail.justinxvopro.chestercinema.commands;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.gmail.justinxvopro.chestercinema.Config;
import com.gmail.justinxvopro.chestercinema.Loggable;
import com.gmail.justinxvopro.chestercinema.moviesystem.MovieManager;
import com.gmail.justinxvopro.chestercinema.moviesystem.VideoManager;
import com.gmail.justinxvopro.chestercinema.util.YouTubeDL;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MovieCommand implements Command, Loggable {

    @Override
    public boolean execute(MessageReceivedEvent e, String[] args) {
	Member member = e.getMember();
	TextChannel channel = e.getTextChannel();
	VoiceChannel voice = member.getVoiceState().getChannel();
	if (!member.hasPermission(Permission.ADMINISTRATOR)) {
	    channel.sendMessage("You do not have permission!").queue();
	    return true;
	}
	if(e.getJDA().getTextChannelById(Config.getInstance().getGif_output_textchannel_id()) == null) {
	    channel.sendMessage("Invalid gif output textchannel id!").queue();
	    return true;
	}
	if (!member.getVoiceState().inVoiceChannel()) {
	    channel.sendMessage("Please be in a voice channel!").queue();
	    return true;
	}

	if (args[0].equalsIgnoreCase("onlinemovie")) {
	    String original = Command.joinArguments(args);
	    String cleaned = original.replace(":", "#").replace("/", "#").replace("?", "#");
	    e.getMessage().delete().queue();
	    VideoManager.getVideo(cleaned).ifPresentOrElse(video -> {
		MovieManager.getMovieManager(e.getGuild()).playMovie(channel, voice, video);
	    }, () -> {
		channel.sendMessage("Downloading Video. . .").queue(msg -> {
		    var outDir = new File("videos");
		    outDir.mkdirs();
		    try {
			YouTubeDL.download(Command.joinArguments(args), outDir.getAbsolutePath(), cleaned, (out) -> {
			    if (!out.isEmpty())
				logger().info(out);
			});
		    } catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
			msg.editMessage("An error occured. . ." + e1.getMessage()).queue();
		    }
		    msg.editMessage("Done!").queue();
		    VideoManager.load();
		    CompletableFuture.runAsync(() -> {
			VideoManager.getVideo(cleaned).ifPresent(video -> {
			    MovieManager.getMovieManager(e.getGuild()).playMovie(channel, voice, video);
			});
		    });
		});
	    });

	    return true;
	}

	if (args.length > 1 && args[1].equalsIgnoreCase("stop")) {
	    Optional.ofNullable(MovieManager.getMovieManager(e.getGuild()).getCurrent()).ifPresent(mp -> mp.stop(0));
	    return true;
	}

	VideoManager.getVideo(Command.joinArguments(args)).ifPresentOrElse(video -> {
	    MovieManager.getMovieManager(e.getGuild()).playMovie(channel, voice, video);
	}, () -> {
	    channel.sendMessage("No video was found!").queue();
	});

	return true;
    }

    @Override
    public String getCommand() {
	return "movie";
    }

    @Override
    public String getDescription() {
	return "play movie";
    }

    @Override
    public String[] getAlias() {
	return new String[] { "onlinemovie" };
    }

    @Override
    public String getCategory() {
	return "entertainment";
    }

}
