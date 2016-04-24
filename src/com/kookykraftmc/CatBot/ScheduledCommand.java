package com.kookykraftmc.CatBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public class ScheduledCommand
{
    static FileConfiguration cmdsCfg;
    static File cmdsFile;
    static Logger log = CatBot.log;
    static CatBot plugin;
    static SimpleDateFormat df = CatBot.dateFormat;
    static List<ScheduledCommand> commands = new Vector<ScheduledCommand>();

	UUID targetID;
	String[] args;
	String sender;
	Date timeSent;
	
	static void enable(CatBot cb)
	{
	    plugin = cb;
	}
	
	/**
	 * 
	 * @param cmd Command sent
	 * @param cmdSender Command sender
	 * 
	 * @throws IllegalArgumentException when cmd is too short, or target does not exist.
	 */
	ScheduledCommand(String[] cmd, CommandSender cmdSender)
	{
		//Check for the millionth time that they have correct args
		if(args.length < 3)
		{
			cmdSender.sendMessage(CatBot.prefix + "Error scheduling command: arguments were too short.");
			throw new IllegalArgumentException("Command given did not have enough arguments. This error "
					+ "should never be seen.");
		}
		//Make sure player exists
		@SuppressWarnings("deprecation")
        OfflinePlayer p = Bukkit.getOfflinePlayer(cmd[2]);
		if(p == null)
		{
	          cmdSender.sendMessage(CatBot.prefix + "Error scheduling command: target does not exist.");
	            throw new IllegalArgumentException("Target player given did not exist. This error "
	                    + "should never be seen.");
		}
		
		targetID = p.getUniqueId();
		sender = cmdSender.getName();
		timeSent = new Date();
		args = cmd;
	}
	
	ScheduledCommand(int i)
	{
		
		/*Check for the billionth time that they have correct args
		if(cmdSplit.length < 3)
		{
			throw new IllegalArgumentException("Stored command did not have enough arguments. This error "
					+ "should never be seen.");
		}
		*/
	}
	
    /**
     * Copy contents of InputStream
     * into file
     * 
     * @param in InputStream to read
     * @param file File to write
     */
   private static void copy(InputStream in, File file)
    {
            try {
                OutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                while((len=in.read(buf))>0){
                    out.write(buf,0,len);
                }
                out.close();
                in.close();
            } catch (IOException e) {
                log.severe(CatBot.cPrefix + "Error loading commands file!");
                e.printStackTrace();
            }
    }

   /**
    * Save default commands.yml file
    */
    public static void copyDefault()
    {
        if(!cmdsFile.exists())
        {
            cmdsFile.getParentFile().mkdirs();
            copy(plugin.getResource("commands.yml"), cmdsFile);
        }
    }

    /**
     * save current commands to
     * commands.yml
     * Will overwrite contents
     * of file.
     * 
     */
    public static void saveCmds()
    {
        try {
            cmdsCfg.save(cmdsFile);
        } catch (IOException e) {
            log.severe(CatBot.cPrefix + "Error saving commands file!");
            e.printStackTrace();
        }
    }
    
    /**
     * Load all commands from
     * commands.yml
     */
    public static void loadCmds()
    {
            try {
                cmdsCfg.load(cmdsFile);
            } catch (FileNotFoundException e) {
                log.severe(CatBot.cPrefix + "Error finding commands file!");
                e.printStackTrace();
            } catch (IOException e) {
                log.severe(CatBot.cPrefix + "Error loading commands file!");
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                log.severe(CatBot.cPrefix + "Error reading commands file!");
                e.printStackTrace();
            }
    }


    public List<String> readCmds()
    {
        return null;
/////////////////////////////////////////////////////Do this!
    }

    /**
     * Create a command. Might
     * remove this eventually
     * 
     * @param cmd Command to schedule
     * @param sender Sender of command
     * @param target Player command should
     *          activate on
     * 
     * @return Successful?
     */
    @SuppressWarnings("deprecation")
    public boolean createCmd(String cmd, CommandSender sender, String target) //done
    {
        //Find next available index to save command
        Set<String> existingIndices = cmdsCfg.getConfigurationSection("Commands").getKeys(false);
        boolean isFull = true;
        String index = null;
        OfflinePlayer targetPlayer;

        for(int i = 0;i<1000;i++)
        {
            if(!existingIndices.contains(String.valueOf(i)))
            {
                isFull = false;
                index = String.valueOf(i);
                break;
            }
        }
        if(isFull||index == null)
        {
            log.warning(CatBot.cPrefix + "Command store is full (contains 1000+ entries)! Please check file.");
            return false;
        }
        
        //Get target player from command
        String[] cmdArgs = cmd.split("\\b");
        if(cmdArgs.length<3)
        {
            sender.sendMessage(CatBot.prefix + "Definitely not enough args in command");
            return false;
        }
        else
        {
            targetPlayer = Bukkit.getOfflinePlayer(cmdArgs[2]);
            if(!Bukkit.getOfflinePlayer(cmdArgs[2]).hasPlayedBefore())
                sender.sendMessage(CatBot.prefix + "That player has never joined the server before! Command will " + 
                        "still be saved, but make sure you've checked the name.");
        }
        
        //Save command to file
        cmdsCfg.set("Commands." + index + ".PlayerName", targetPlayer);
        cmdsCfg.set("Commands." + index + ".Sender", sender.getName());
        cmdsCfg.set("Commands." + index + ".Command", cmd);
        cmdsCfg.set("Commands." + index + ".DateSent", df.format(new Date()));
        //commands.add(new ScheduledCommand(targetPlayer, sender);
        return true;
    }
    

    /**
     * Perform command at index
     * (if possible)
     * 
     * @param index Command to send
     * @return Successful?
     */
    public boolean sendCmd(String index) //done
    {
        //Make sure it exists
        if(cmdsCfg.getConfigurationSection(index) == null)
            return false;
        String[] args = cmdsCfg.getString("Commands." + index + ".Command").split("\\b");
        @SuppressWarnings("deprecation")
        CommandSender sender = (CommandSender) Bukkit.getOfflinePlayer(cmdsCfg.getString("Commands." + index + ".Sender"));
        plugin.getCommand("catbot").getExecutor().onCommand(sender, null, null, args);
        return true;
    }


    /**
     * Deletes command at index
     * 
     * @param index Command to remove
     * 
     * @return Success?
     */
    public boolean removeCmd(String index) //done
    {
        if(cmdsCfg.getConfigurationSection("Commands." + index) == null)
            return false;
        cmdsCfg.set(index, null);
        saveCmds();
        return true;
    }

    /**
     * Deletes all scheduled commands
     * Use with caution!
     */
    public static void purgeCmds() //done
    {
        cmdsCfg.set("Commands", null);
        for(ScheduledCommand cmd:commands)
            commands.remove(cmd);
        cmdsCfg.createSection("Commands");
    }

}
