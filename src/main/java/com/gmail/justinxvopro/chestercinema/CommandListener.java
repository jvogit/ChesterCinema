package com.gmail.justinxvopro.chestercinema;

import java.util.stream.Stream;

import com.gmail.justinxvopro.chestercinema.commands.Command;
import com.gmail.justinxvopro.chestercinema.commands.HelpCommand;
import com.gmail.justinxvopro.chestercinema.commands.MovieCommand;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
    public static final Command[] COMMAND_LIST = {
	    new HelpCommand(),
	    new MovieCommand()
    };
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	Message msg = event.getMessage();
	
	if(!msg.isFromType(ChannelType.TEXT) || msg.getContentRaw().isEmpty() || !msg.getContentRaw().startsWith(Config.getInstance().getPrefix())) {
	    return;
	}
	
	String[] split = event.getMessage().getContentRaw().substring(Config.getInstance().getPrefix().length(), event.getMessage().getContentRaw().length()).split("\\s+");
	String commandBase = split[0];
	
	Stream.of(COMMAND_LIST)
	.filter(c -> c.getCommand().equalsIgnoreCase(commandBase) || c.getAliasAsList().stream().anyMatch(a->commandBase.equalsIgnoreCase(a)))
	.forEach(c -> c.execute(event, split));
    }
}