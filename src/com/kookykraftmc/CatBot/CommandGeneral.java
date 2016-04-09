package com.kookykraftmc.CatBot;

import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandGeneral implements CommandExecutor
{
	static Server server = Bukkit.getServer();
	static ConsoleCommandSender console = Bukkit.getConsoleSender();
	static String cmd;
	static Player p;
	Logger log = CatBot.log;
	@Override
	public boolean onCommand(CommandSender sender, Command commd, String label, String[] args) 
	{
		if (args.length == 0)
		{
			sender.sendMessage(CatBot.prefix + "Meow.");
			return false;
		}
		switch(args[0])
		{
		case "reload":
			if (sender.hasPermission("catbot.reload"))
			{
				CatFilterEvents.loadCfg();
				sender.sendMessage(CatBot.prefix + "Catbot reloaded. Meow.");
				log.info(CatBot.cPrefix + "CatBot Reloaded. Meow.");
			}
			else
			{
				sender.sendMessage(CatBot.prefix + "Hiss! (you do not have permission to do this!)");
			}
			break;
			
			
		case "pet":
			sender.sendMessage(CatBot.prefix +"Purr :3");
			break;
			
			
		case "redeem":
			/*
			 * args[0] is "redeem"
			 * args[1] is player name
			 * args[2] is item id
			 * args[3] is amount
			 * args[4] is metadata
			 */
			
			//Check for permission
			if(!sender.hasPermission("catbot.redeem"))
			{
				sender.sendMessage(CatBot.prefix + "Hiss! (you do not have permission to do this!)");
				return true;
			}
			//Check all required arguments are present
			if(args.length < 3)
			{
				sender.sendMessage(CatBot.prefix + "Usage: /catbot redeem <player> <itemID> (amount) (meta)");
				return true;
			}
			//Assign default values to metadata and amount
			String[] newArgs = {args[0], args[1], args[2], "1", "0"};
			switch(args.length)
			{
			case 5:
				newArgs[4] = args[4];
			case 4:
				newArgs[3] = args[3];
			default:
				break;
			}
			//Make sure all numeric arguments are numeric
			if (!StringUtils.isNumeric(newArgs[3] + newArgs[4]))
			{
				sender.sendMessage(CatBot.prefix + "Make sure your arguments are in the right order");
				sender.sendMessage(CatBot.prefix + "Usage: /catbot redeem <player> <itemName> (amount) (meta)");
				return true;
			}
			//Check that player is online (otherwise give won't work)
			if (Bukkit.getPlayer(newArgs[1]) == null)
			{
				sender.sendMessage(CatBot.prefix + "Player not found.");
				return true;
			}
			//Give permissions
			cmd = "pex user " + newArgs[1] + " add itemrestrict.bypass.Usage." + newArgs[2] + "-" + newArgs[4];
			server.dispatchCommand(console, cmd);
			cmd = "pex user " + newArgs[1] + " add itemrestrict.bypass.Ownership." + newArgs[2] + "-" + newArgs[4];
			server.dispatchCommand(console,cmd);
			cmd = "pex user " + newArgs[1] + " add itemrestrict.bypass.Equip." + newArgs[2] + "-" + newArgs[4];
			server.dispatchCommand(console,cmd);
			//Give items
			cmd = "give " + newArgs[1] + " " + newArgs[2] + " " + newArgs[3] + " " +newArgs[4];
			server.dispatchCommand(console, cmd);
			sender.sendMessage(CatBot.prefix + "Giving " + newArgs[1] + " " + newArgs[3] + " of item " + newArgs[2] + ":" + newArgs[4] + ".");
			log.info(CatBot.cPrefix + sender.getName() + " redeemed " + " " + newArgs[3] + " of item " + newArgs[2] + ":" + newArgs[4] + " to " + newArgs[1] + ".");
			break;
			
			
		case "warn":
			//Check for permission
			if(!sender.hasPermission("catbot.warn"))
			{
				sender.sendMessage(CatBot.prefix + "Hiss! (you do not have permission to do this!)");
				return true;
			}
			//Check all required arguments are present
			if(args.length < 2)
			{
				sender.sendMessage(CatBot.prefix + "Usage: /catbot warn <player>");
				return true;
			}
			//Check that the player is online and send message
			p = Bukkit.getPlayer(args[1]);
			if (p == null)
			{
				sender.sendMessage(CatBot.prefix + "Player not found.");
				return true;
			}
			p.sendMessage(CatBot.prefix + "Please mind your language, if you continue to bypass catbot you will be muted.");
			sender.sendMessage(CatBot.prefix + "Warning " + args[1] + " about their language.");
			log.info(CatBot.cPrefix + args[1] + " was warned by " + sender.getName() + ".");
			break;
			
			
		case "showtps":
			/*
			 * args[0] is "showtps"
			 * args[1] is player name
			 */
			//Check for permission
			if(!sender.hasPermission("catbot.showtps"))
			{
				sender.sendMessage(CatBot.prefix + "Hiss! (you do not have permission to do this!)");
				return true;
			}
			//Check all required arguments are present
			if(args.length < 2)
			{
				sender.sendMessage(CatBot.prefix + "Usage: /catbot showtps <player>");
				return true;
			}
			//Check that the player is online and show tps
			p = Bukkit.getPlayer(args[1]);
			if (p == null)
			{
				sender.sendMessage(CatBot.prefix + "Player not found.");
				return true;
			}
			p.performCommand("cofh tps");
			sender.sendMessage(CatBot.prefix + "Showing " + args[1] + "the tps.");
			break;
			
			
		default:
			sender.sendMessage(CatBot.prefix + "Meow.");
			return false;
		}
		return true;
	}
}