package com.kookykraftmc.CatBot;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.permission.Permission;

public class CommandGeneral implements CommandExecutor
{
	static Server server = Bukkit.getServer();
	static Plugin plugin;
	static String iresPrefix = "itemrestrict.bypass.";
	static List<String> promoteGroups = new Vector<String>();
	static Logger log = CatBot.log;
	static Permission perms;
	
	CommandGeneral(CatBot catBot)
	{
	    plugin = catBot;
	    perms = catBot.rspPerms.getProvider();
	    loadCfg();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command commd, String label, String[] args) 
	{
	    Player p;
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
				log.info(CatBot.cPrefix + "CatBot Reloaded by " + sender.getName() + ". Meow.");
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
			 * args[2] is item id/name
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
			}
			//Make sure all numeric arguments are numeric
			if (!StringUtils.isNumeric(newArgs[3] + newArgs[4]))
			{
				sender.sendMessage(CatBot.prefix + "Make sure your arguments are in the right order");
				sender.sendMessage(CatBot.prefix + "Usage: /catbot redeem <player> <itemName> (amount) (meta)");
				return true;
			}
			//Check that player is online(otherwise give won't work)
			if (Bukkit.getPlayer(newArgs[1]) == null)
			{
				sender.sendMessage(CatBot.prefix + "Player " + newArgs[1] + " not found.");
				return true;
			}
			
			//Make sure player has inv space
            p = Bukkit.getPlayer(newArgs[1]);
            if(p.getInventory().firstEmpty() == -1)
            {
                sender.sendMessage(CatBot.prefix + "Player " + p.getName() + " has a full inventory.");
                return true;
            }
            
            //Make sure item exists
            if(Material.getMaterial(newArgs[2]) == null)
            {
                sender.sendMessage(CatBot.prefix + "That item (" + newArgs[2] + ") does not exist.");
                return true;
            }
            
			//Give permissions
			perms.playerAdd(null, p, iresPrefix + "usage." + newArgs[2]);
			perms.playerAdd(null, p, iresPrefix + "ownership." + newArgs[2]);
			perms.playerAdd(null, p, iresPrefix + "equip." + newArgs[2]);

			//Give items
			ItemStack i = new ItemStack(Material.getMaterial(newArgs[2]),Integer.parseInt(newArgs[3]),Short.parseShort((newArgs[4])));
			p.getInventory().addItem(i);
			
			//Tell sender (if player), reciever, and console
			if (sender instanceof Player) sender.sendMessage(CatBot.prefix + "Giving " + newArgs[1] + " " + newArgs[3] + " of item " + newArgs[2] + ":" + newArgs[4] + ".");
			p.sendMessage(CatBot.prefix + "You have receieved " + newArgs[3] + " of item " + newArgs[2] + ":" + newArgs[4] + ".");
			log.info(CatBot.cPrefix + sender.getName() + " redeemed " + " " + newArgs[3] + " of item " + newArgs[2] + ":" + newArgs[4] + " to " + newArgs[1] + ".");
			return true;
			
			
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
				sender.sendMessage(CatBot.prefix + "Player " + args[1] + " not found.");
				return true;
			}
			p.sendMessage(CatBot.prefix + "Please mind your language, if you continue to bypass catbot you will be muted.");
			sender.sendMessage(CatBot.prefix + "Warning " + args[1] + " about their language.");
			log.info(CatBot.cPrefix + args[1] + " was warned by " + sender.getName() + ".");
			return true;
			
			
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
				sender.sendMessage(CatBot.prefix + "Player " + args[1] + " not found.");
				return true;
			}
			p.performCommand("cofh tps");
			sender.sendMessage(CatBot.prefix + "Showing " + args[1] + "the tps.");
			return true;
			
		case "promote":
	          /*
             * args[0] is "promote"
             * args[1] is player name
             * args[2] is group to promote to
             */
            //Check for permissions, arg length, and correct args 
            if(!sender.hasPermission("catbot.promote"))
            {
               sender.sendMessage(CatBot.prefix + "Hiss! (you do not have permission to do this!)");
               return true;
            }
            else if(args.length < 3)
			{
			    sender.sendMessage(CatBot.prefix + "./catbot promote (player) (toGroup)");
			    return true;
			}

			p = Bukkit.getPlayerExact(args[1]);
			if(p == null)
	        {
                sender.sendMessage(CatBot.prefix + "Player " + args[1] + " not found. Make sure to use " +
                        "their exact name.");
                return true;
            }
			else if(!promoteGroups.contains(args[2]))
			{
			    p.sendMessage(CatBot.prefix + "Group " + args[2] + " does not exist or is not compatible with this command.");
			    log.info(CatBot.cPrefix + sender.getName() + " tried to promote " + p.getName() + " to group " + args[2] + " but " + 
			                "the group did not exist or was not permitted.");
			    return true;
			}
			
			//Add to group, checking for error
			if(!perms.playerAddGroup(null, p, args[2]))
			{
			    sender.sendMessage(CatBot.prefix + "Something went wrong!");
			    log.warning(CatBot.cPrefix + "Error while adding player " + p.getName() + " to group " + args[2] + ".");
			}
			else
			{
			    if (sender instanceof Player) sender.sendMessage(CatBot.prefix + "Added player " + p.getName() + " to group " + args[2] + ".");
			    log.info(CatBot.cPrefix + sender.getName() + " added player " + p.getName() + " to group " + args[2] + ".");
			    p.sendMessage(CatBot.prefix + "Your rank has been changed to " + args[2] + ".");
			}
			return true;
		
		case "op":
		    if(!sender.hasPermission("catbot.setop"))
		    {
		        sender.sendMessage(CatBot.prefix + "Hiss! (you do not have permission to do this!");
		       return true;
		    }
		    if(args.length < 2)
		    {
		        if(sender instanceof Player)
		        {
		            sender.setOp(true);
		            sender.sendMessage(CatBot.prefix + "You have been opped.");
		            log.info(CatBot.cPrefix + sender.getName() + " opped self.");
		        }
		        else
		        {
		            sender.sendMessage(CatBot.cPrefix + "Only players can be opped.");
		        }
		        break;
		    }
		    p = Bukkit.getPlayerExact(args[1]);
		    if(p == null)
		    {
		        sender.sendMessage(CatBot.prefix + "Player " + args[1] + " not found. Did you forget to use " +
		                "their full exact name?");
		        break;
		    }
		    else if(!p.hasPermission("catbot.getop"))
		    {
		        sender.sendMessage(CatBot.prefix + "You cannot op that player.");
		        log.info(CatBot.cPrefix + sender.getName() + " tried to op player " + args[1] + ", but they are "
		                + "not allowed to be opped.");
		        break;
		    }
		    else
		    {
		        sender.sendMessage(CatBot.prefix + "Opped " + p.getName() + ".");
		        log.info(CatBot.cPrefix + sender.getName() + " opped " + p.getName() + ".");
		        p.setOp(true);
		    }
		    return true;
		   
		default:
			sender.sendMessage(CatBot.prefix + "Meow.");
			return false;
		}
		return true;
	}
	
	static void loadCfg()
	{
	    List<String> promoteGroupsCfg = plugin.getConfig().getStringList("AllowedGroups");
	    for(int i = 0;i<promoteGroupsCfg.size();i++)
	        promoteGroupsCfg.set(i, promoteGroupsCfg.get(i).toLowerCase());
	    
	    List<String> existingGroups = Arrays.asList(perms.getGroups());
	    for(int i = 0;i<existingGroups.size();i++)
	        existingGroups.set(i, existingGroups.get(i).toLowerCase());

	    //Check that groups from config exist to avoid problems later
	    for(String grp:promoteGroupsCfg)
	        if(!existingGroups.contains(grp))
	            log.warning(CatBot.cPrefix + "Group \"" + grp + "\" was declared in config but cannot be found. Will be ignored.");
	        else
	            promoteGroups.add(grp);
	}
}