package com.kookykraftmc.catbot.commands;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Limit the usage of a command, based on
 * time limit or amount of times used.
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
    Player p;
    
    /**
     * Amount of times they've used the command
     */
    int usesLeft = 0;
    
    /**
     * Command being restricted
     */
    String cmd;

    /**
     * Whether the restriction should still apply
     * after restarts and be saved in file
     * Currently this should always be false
     **/
    boolean isPersistant = false;


    CommandLimiter(Player player,int max, String command)
    {
        p = player;
        usesLeft = max;
        cmd = command;
    }
}
