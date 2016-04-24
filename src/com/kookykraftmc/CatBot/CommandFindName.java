package com.kookykraftmc.CatBot;

import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandFindName implements CommandExecutor
{
	static Server server = Bukkit.getServer();
	static ConsoleCommandSender console = Bukkit.getConsoleSender();
	static String cmd;
	static Player p;
	static boolean isPlayer;
	static String itemName;
	Logger log = CatBot.log;
	static String msg;
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command commd, String label, String[] args) 
	{
		isPlayer = sender instanceof Player;
		if(isPlayer)
			p = (Player) sender;
		if (args.length == 0)
		{
			if(isPlayer)
				msg =  "Item in hand is \"" + p.getItemInHand().getType().name() + "\"";
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
					itemName = Material.getMaterial(Integer.parseInt(args[0])).name();
					msg = "Item with ID " + args[0] + " is \"" + itemName + "\"";
				}
		}

		if(isPlayer)
		{
			p.sendMessage(CatBot.prefix + msg);
		}
		else
			log.info(CatBot.cPrefix + msg);
		return true;
	}
}