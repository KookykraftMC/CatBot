package com.kookykraftmc.catbot.commands;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Limit the amount of times a
 * player can use a command
 * Optionally save 
 * @author Kraise
 *
 */
public class CommandLimiter
{
    private final static HashSet<CommandLimiter> currentRestrictions = new HashSet<CommandLimiter>();

    /**
     * UUID of player using command
     */
    UUID pID;
    
    /**
     * Amount of times they've used the command
     */
    int usesLeft = 0;
    
    /**
     * Command being restricted
     */
    String cmd;


    /**
     * Constructor
     * 
     * @param player Player using command
     * @param max Maximum uses of command
     * @param command Command to restrict
     */
    private CommandLimiter(Player p,int max, String command)
    {
        pID = p.getUniqueId();
        usesLeft = max;
        cmd = command;
    }
    
    /**
     * @param player Player to test
     * @param command Command to test
     * @return true if this relates to player using command
     */
    private boolean isFor(Player p, String command)
    {
    	return(this.pID.equals(p.getUniqueId()) && this.cmd.equals(command));
    }

    /**
     * Get number of uses remining
     * @param player for player
     * @param command of command
     * @return
     */
    static String usesLeft(Player p, String command)
    {
    	if(p.hasPermission("catbot.bypasscommandlimits"))
    		return "unlimited";

    	for(CommandLimiter c:currentRestrictions)
    	{
    		if(c.isFor(p, command))
    			return String.valueOf(c.usesLeft);
    	}
    	return "0";
    }

    /**
     * Give player an extra usage of command
     * @param player Player 
     * @param command Command
     */
    static void addUse(Player p,String command)
    {
    	for(CommandLimiter c:currentRestrictions)
    	{
    		if(c.isFor(p, command))
    			c.usesLeft++;
    	}
    }
    
    /**
     * Test if player can use command, and
     * create new limiter if one doesn't exist
     * already. This should be the only method
     * in the class used externally.
     * 
     * @param player Player to check
     * @param maxUses Maximum uses of command, in case of creating new limiter
     * @param command Command being used
     * @return true if player can use command
     */
    static boolean tryUsing(Player p, int maxUses, String command)
    {
    	if(p.hasPermission("catbot.bypasscommandlimits"))
    		return true;
    	//Try to find player's current usage
    	for(CommandLimiter c:currentRestrictions)
    	{
    		if(c.isFor(p, command))
    			if(c.usesLeft < 1)
    				return false;
    			else
    			{
    				c.usesLeft--;
    				return true;
    			}
    	}
    	
    	//At this stage it is clear that player doesn't have a current usage
    	currentRestrictions.add(new CommandLimiter(p, maxUses - 1, command));
    	return true;
    }
}
