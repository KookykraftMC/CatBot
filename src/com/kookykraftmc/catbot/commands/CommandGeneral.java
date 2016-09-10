package com.kookykraftmc.catbot.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.kookykraftmc.catbot.CatBot;
import com.kookykraftmc.catbot.listeners.JoinEvents;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class CommandGeneral implements CommandExecutor
{
	final static String IRES_PREFIX = "itemrestrict.bypass.";
	final static List<String> promoteGroups = new Vector<String>();
	static Plugin plugin;
	static Logger log = CatBot.log;
	static Permission perms;
	static Chat chat;
	private CommandSender sender;
	
	public CommandGeneral(CatBot catBot)
	{
	    plugin = catBot;
	    perms = catBot.rspPerms.getProvider();
	    chat = catBot.rspChat.getProvider();
	    loadCfg();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sndr, Command commd, String label, String[] args) 
	{
		sender = sndr;
	    Player p;
		if (args.length == 0)
		{
			msgSender("Meow.");
			return false;
		}
		switch(args[0])
		{
		case "reload":
			if (sender.hasPermission("catbot.reload"))
			{
				plugin.reloadConfig();
				msgSender("Catbot reloaded. Meow.");
				log.info(CatBot.cPrefix + "CatBot Reloaded by " + sender.getName() + ". Meow.");
			}
			else
			{
				msgSender("Hiss! (you do not have permission to do this!)");
			}
			break;
			
			
		case "pet":
			msgSender("Purr :3");
			log.info(CatBot.cPrefix + "Purr :3");
			break;
			
			
		case "redeem":
			/*
			 * args[0] is "redeem"
			 * args[1] is player name
			 * args[2] is item id/name
			 * args[3] is amount
			 * args[4] is metadata
			 */
			Material mat = null;
			
			//Check for permission
			if(!sender.hasPermission("catbot.redeem"))
			{
				msgSender("Hiss! (you do not have permission to do this!)");
				return true;
			}
			//Check all required arguments are present
			if(args.length < 3)
			{
				msgSender("Usage: /catbot redeem <player> <itemID> (amount) (meta)");
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
				if(sender instanceof Player)
				{
					msgSender("Make sure your arguments are in the right order");
					msgSender("Usage: /catbot redeem <player> <item> (amount) (meta)");
				}
				return true;
			}
			//Check that player is online(otherwise can't give items)
			if (Bukkit.getPlayer(newArgs[1]) == null)
			{
				msgSender("Player " + newArgs[1] + " not found.");
				return true;
			}
			//Make sure player has inv space
            p = Bukkit.getPlayer(newArgs[1]);
            if(p.getInventory().firstEmpty() == -1)
            {
            	msgSender("Player " + p.getName() + " has a full inventory.");
                return true;
            }
            //Make sure item exists and find if server uses numeric ids
            mat = Material.getMaterial(newArgs[2]);
            if(StringUtils.isNumeric(newArgs[2]) && mat == null)
            	mat = Material.getMaterial(Integer.parseInt(newArgs[2]));
            if(mat == null)
            {
            	sender.sendMessage(CatBot.prefix + "That item (" + args[2] + ") does not exist.");
            	return true;
            }
            
			//Give permissions
			perms.playerAdd(null, p, IRES_PREFIX + "usage." + newArgs[2]);
			perms.playerAdd(null, p, IRES_PREFIX + "ownership." + newArgs[2]);
			perms.playerAdd(null, p, IRES_PREFIX + "equip." + newArgs[2]);
			
			//Give items
			ItemStack i = new ItemStack(mat,Integer.parseInt(newArgs[3]),Short.parseShort((newArgs[4])));
			p.getInventory().addItem(i);
			
			//Tell sender (if player), reciever, and console
			if (sender instanceof Player) msgSender("Giving " + newArgs[1] + " " + newArgs[3] + " of item " + newArgs[2] + ":" + newArgs[4] + ".");
			p.sendMessage(CatBot.prefix + "You have receieved " + newArgs[3] + " of item " + newArgs[2] + ":" + newArgs[4] + ".");
			log.info(CatBot.cPrefix + sender.getName() + " redeemed " + " " + newArgs[3] + " of item " + newArgs[2] + ":" + newArgs[4] + " to " + newArgs[1] + ".");
			return true;
			
			
		case "warn":
			//Check for permission
			if(!sender.hasPermission("catbot.warn"))
			{
				msgSender("Hiss! (you do not have permission to do this!)");
				return true;
			}
			//Check all required arguments are present
			if(args.length < 2)
			{
				msgSender("Usage: /catbot warn <player>");
				return true;
			}
			//Check that the player is online and send message
			p = Bukkit.getPlayer(args[1]);
			if (p == null)
			{
				msgSender("Player " + args[1] + " not found.");
				return true;
			}
			p.sendMessage(CatBot.prefix + "Please mind your language, if you continue to bypass catbot you will be muted.");
			msgSender("Warning " + args[1] + " about their language.");
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
				msgSender("Hiss! (you do not have permission to do this!)");
				return true;
			}
			//Check all required arguments are present
			if(args.length < 2)
			{
				msgSender("Usage: /catbot showtps <player>");
				return true;
			}
			//Check that the player is online and show tps
			p = Bukkit.getPlayer(args[1]);
			if (p == null)
			{
				msgSender("Player " + args[1] + " not found.");
				return true;
			}
			p.performCommand("cofh tps o");
			msgSender("Showing " + args[1] + " the tps.");
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
            	msgSender("Hiss! (you do not have permission to do this!)");
               return true;
            }
            else if(args.length < 3)
			{
            	msgSender("./catbot promote (player) (toGroup)");
			    return true;
			}

			p = Bukkit.getPlayerExact(args[1]);
			if(p == null)
	        {
				msgSender("Player " + args[1] + " not found. Make sure to use " +
                        "their exact name.");
                return true;
            }
			else if(!promoteGroups.contains(args[2]))
			{
				msgSender("Group " + args[2] + " does not exist or is not compatible with this command.");
			    log.info(CatBot.cPrefix + sender.getName() + " tried to promote " + p.getName() + " to group " 
			                + args[2] + " but " + "the group did not exist or was not permitted.");
			    return true;
			}
			
			//Add to group, checking for error
			if(!perms.playerAddGroup(null, p, args[2]))
			{
				msgSender("Something went wrong!");
			    log.warning(CatBot.cPrefix + "Error while adding player " + p.getName() + " to group " + args[2] + ".");
			}
			else
			{
				msgSender("Added player " + p.getName() + " to group " + args[2] + ".");
			    log.info(CatBot.cPrefix + sender.getName() + " added player " + p.getName() + " to group " + args[2] + ".");
			    p.sendMessage(CatBot.prefix + "Your rank has been changed to " + args[2] + ".");
			}
			return true;
		
		case "op":
		    if(!sender.hasPermission("catbot.setop"))
		    {
		    	msgSender("Hiss! (you do not have permission to do this!");
		       return true;
		    }
		    if(args.length < 2)
		    {
		        if(sender instanceof Player)
		        {
		            sender.setOp(true);
		            msgSender("You have been opped.");
		            log.info(CatBot.cPrefix + sender.getName() + " opped self.");
		        }
		        else
		        {
		            log.info(CatBot.cPrefix + "Only players can be opped.");
		        }
		        break;
		    }
		    p = Bukkit.getPlayerExact(args[1]);
		    if(p == null)
		    {
		    	msgSender("Player " + args[1] + " not found. Did you forget to use " +
		                "their full exact name?");
		        break;
		    }
		    else if(!p.hasPermission("catbot.getop"))
		    {
		    	msgSender("You cannot op that player.");
		        log.info(CatBot.cPrefix + sender.getName() + " tried to op player " + args[1] + ", but they are "
		                + "not allowed to be opped.");
		        break;
		    }
		    else
		    {
		    	msgSender("Opped " + p.getName() + ".");
		        log.info(CatBot.cPrefix + sender.getName() + " opped " + p.getName() + ".");
		        p.setOp(true);
		    }
		    return true;
		case "prefix":
		    if(!sender.hasPermission("catbot.changeprefix"))
	          {
                msgSender("Hiss! (you do not have permission to do this!");
               return true;
            }
		    else if (args.length < 3)
		    {
		        msgSender("Usage: /catbot prefix (player) (prefix)");
		        return true;
		    }
		    p = Bukkit.getPlayer(args[1]);
		    if(p == null)
		    {
		        msgSender("Player " + args[1] + " not found.");
		        return true;
		    }
		    if(args[2].equals("\"\"")||args[2].equals("off"))
		    {
		        chat.setPlayerPrefix(null, p, null);
		        if(sender instanceof Player) msgSender("Removed " + p.getName() + "'s prefix.");
		        log.info(CatBot.cPrefix + sender.getName() + " removed " + p.getName() + "'s prefix.");
		        return true;
		    }
		    else
		    {
    		    chat.setPlayerPrefix(null, p, args[2]);
    		    if(sender instanceof Player) msgSender("Changed " + p.getName() + "'s prefix to "
    		                + chat.getPlayerPrefix(p));
    		    log.info(CatBot.cPrefix + sender.getName() + " changed " + p.getName() + "'s prefix to " + chat.getPlayerPrefix(p));
		    }
		    break;
		case "rmvip":
	          if(!sender.hasPermission("catbot.removevip"))
              {
	              msgSender("Hiss! (you do not have permission to do this!");
	              break;
              }
	          else if(args.length < 2)
	          {
	              msgSender("Usage: /catbot rmvip (player)");
	              break;
	          }
	          Player playerToRemove = Bukkit.getPlayerExact(args[1]);
	          if(playerToRemove == null)
	          {
	              msgSender("That player could not be found. Make sure to get their name exactly right, and that they're online.");
	              return true;
	          }
	          perms.playerAdd((Player) playerToRemove, "-essentials.warps.vip");
	          msgSender("Removed VIP warp permissions from " + playerToRemove.getName() + ". Make sure to report this!");
	          break;
		case "givechunkloader":
            if(!sender.hasPermission("catbot.bclperms"))
            {
                msgSender("Hiss! (you do not have permission to do this!");
                break;
            }
            else if(args.length < 2)
            {
                msgSender("Usage: /catbot bclperms (player)");
                break;
            }
            Player toAddPerms = Bukkit.getPlayerExact(args[1]);
            if(toAddPerms == null)
            {
                msgSender("That player could not be found. Make sure to get their name exactly right, and that they're online.");
                return true;
            }
            perms.playerAdd(toAddPerms, "betterchunkloader.onlineonly");
            msgSender("Given chunk loaders to " + args[1] + ". Remember this will not work for giving extra chunks.");
		case "whitelist":
            boolean isWL = JoinEvents.isWhitelisted;
            if(!sender.hasPermission("catbot.whitelist"))
            {
                msgSender("Hiss! (you do not have permission to do this!");
                break;
            }
            else if(args.length < 2)
            {
                msgSender("Usage: /catbot whitelist add||remove||on||off||toggle");
                msgSender("Is whitelisted: " + isWL);
                break;
            }
            switch(args[1])
            {
            case "on":
                msgSender(JoinEvents.enableWhitelist()?"Whitelist enabled. Do /catbot whitelist off to disable.":"Whitelist is already on.");
                break;
            case "off":
                msgSender(JoinEvents.disableWhitelist()?"Whitelist disabled.":"Whitelist is already disabled.");
                break;
            case "toggle":
                msgSender(JoinEvents.disableWhitelist()?"Whitelist enabled.":"Whitelist disabled.");
                break;
            case "add":
                if(args.length < 3)
                {
                    msgSender("Usage: /catbot whitelist add (player)");
                    break;
                }
                JoinEvents.addToWhitelist(args[2]);
                msgSender(args[2] + " added to whitelist.");
                break;
            case "remove":
                if(args.length < 3)
                {
                    msgSender("Usage: /catbot whitelist remove (player)");
                    break;
                }
                msgSender(JoinEvents.removeFromWhitelist(args[2])?args[2] + " removed from whitelist.":args[2] + " is not whitelisted.");
                break;
            case "list":
                msgSender("Currently whitelisted people: " + JoinEvents.getWhitelistString() + ".");
                msgSender("Whitelist is currently " + (JoinEvents.isWhitelisted?"ON":"OFF"));
                break;
            }
            break;
		default:
			msgSender("Meow.");
			break;
		}
		sender = null;
		return true;
	}
	
	void msgSender(String text)
	{
		if(sender instanceof Player||sender instanceof ConsoleCommandSender)
			sender.sendMessage(CatBot.prefix + text);
	}
	
	public static void loadCfg()
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
	            log.warning(CatBot.cPrefix + "Group " + grp + " was declared in config but cannot be found. Will be ignored.");
	        else
	            promoteGroups.add(grp);
	}
}
