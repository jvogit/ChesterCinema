package com.gmail.justinxvopro.chestercinema;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.Getter;

public class Config {
    private static Config CONFIG;
    @Getter
    private String token;
    @Getter
    private String prefix;
    @Getter
    private String gif_output_textchannel_id;
    
    private Config() {}
    
    public static Config getInstance() {
	if(CONFIG == null) {
	    CONFIG = new Config();
	}
	
	return CONFIG;
    }
    
    public static void loadConfig(File file) throws JsonParseException, JsonMappingException, IOException {
	CONFIG = BotCore.OBJECT_MAPPER.readValue(file, Config.class);
    }
}
