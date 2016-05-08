package com.kookykraftmc.catbot.commands;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kookykraftmc.catbot.CatBot;

public class CommandRTP implements CommandExecutor
{
	static final Logger log = CatBot.log;
	static List<UUID> usedList;
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player && args.length == 0))
		{
			sender.sendMessage(CatBot.cPrefix + "Only players can use this command");
		}
		
		return false;
	}

}
