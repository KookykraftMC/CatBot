package com.kookykraftmc.CatBot;

import java.util.Date;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class ScheduledCommand
{
	static Logger log = CatBot.log;
	String target;
	String cmd;
	OfflinePlayer sender;
	Date timeSent;
	
	ScheduledCommand(String command, CommandSender cmdSender)
	{
		String[] cmdSplit = command.split("\\b");
		//Check for the millionth time that they have correct args
		if(cmdSplit.length < 3)
		{
			cmdSender.sendMessage(CatBot.prefix + "Error scheduling command, arguments were too short.");
			throw new IllegalArgumentException("Command given did not have enough arguments. This error "
					+ "should never be seen.");
		}
		
		target = cmdSplit[2];
		sender = (OfflinePlayer) cmdSender;
		timeSent = new Date();
		cmd = command;
	}
	
	ScheduledCommand(String command, String cmdSender, String targetName)
	{
		String[] cmdSplit = command.split("\\b");
		//Check for the billionth time that they have correct args
		if(cmdSplit.length < 3)
		{
			throw new IllegalArgumentException("Stored command did not have enough arguments. This error "
					+ "should never be seen.");
		}
		
	}
}
