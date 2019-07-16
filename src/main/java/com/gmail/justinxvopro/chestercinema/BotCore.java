package com.gmail.justinxvopro.chestercinema;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.justinxvopro.chestercinema.moviesystem.VideoManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotCore extends ListenerAdapter {
    private static String TOKEN;
    private final static Logger LOGGER = LoggerFactory.getLogger(BotCore.class);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static JDA BOT_JDA;

    public static void main(String args[]) {
	File configFile = new File("config.json");
	if (!configFile.exists()) {
	    try {
		Files.copy(BotCore.class.getResourceAsStream("/config.json"), configFile.toPath());
	    } catch (IOException e) {
		LOGGER.error("Unable to create config.json " + e.getMessage());
		return;
	    }
	}
	
	try {
	    Config.loadConfig(configFile);
	} catch (IOException e1) {
	    e1.printStackTrace();
	    LOGGER.error("Unable to load config " + e1.getMessage());
	}
	
	if (args.length >= 2 && args[0].equalsIgnoreCase("-token")) {
	    LOGGER.info("Detected -token arguments using token provided");
	    TOKEN = args[1];
	} else {
	    LOGGER.info("Using config.json token");
	    TOKEN = Config.getInstance().getToken();
	}

	try {
	    BOT_JDA = new JDABuilder().setToken(TOKEN).addEventListeners(new BotCore()).build();
	    LOGGER.info(BOT_JDA.getInviteUrl(Permission.values()));
	} catch (LoginException e) {
	    e.printStackTrace();
	    LOGGER.error("Unable to login: " + e.getMessage());
	}
    }
    
    @Override
    public void onReady(ReadyEvent event) {
	LOGGER.info("Bot is ready!");
	event.getJDA().addEventListener(new CommandListener());
	VideoManager.load();
    }
}
