package com.kookykraftmc.catbot.commands;

import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kookykraftmc.catbot.CatBot;

public class CommandFindName implements CommandExecutor
{
	final Logger log = CatBot.log;	

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command commd, String label, String[] args) 
	{
		Player p = null;
		String msg;
		Boolean isPlayer = sender instanceof Player;

		if(isPlayer)
			p = (Player) sender;
		if (args.length == 0)
		{
			if(isPlayer)
				msg =  "Item in hand is " + p.getItemInHand().getType().name();
			else
				msg = "Non-players must specify an ID.";
		}
		else if(!StringUtils.isNumeric(args[0]))
			msg = "Make sure to give a numerical ID.";
		else
		{
			if(Material.getMaterial(Integer.parseInt(args[0])) == null)
			{
				msg = "No such item with that ID.";
			}
			else
			{
				String itemName = Material.getMaterial(Integer.parseInt(args[0])).name();
				msg = "Item with ID " + args[0] + " is " + itemName;
			}
		}

		if(isPlayer)
			p.sendMessage(CatBot.prefix + msg);
		else
			log.info(CatBot.cPrefix + msg);

		return true;
	}
}
