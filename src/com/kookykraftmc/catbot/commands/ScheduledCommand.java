package com.kookykraftmc.catbot.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import com.kookykraftmc.catbot.CatBot;

public class ScheduledCommand
{
    final static SimpleDateFormat df = CatBot.dateFormat;
    final static List<ScheduledCommand> commands = new Vector<ScheduledCommand>();
    final static Logger log = CatBot.log;
    final static String SECTION_NAME = "Commands";
    final static int fileCmdsLimit = 1000;

    static FileConfiguration cmdsCfg;
    static File cmdsFile;
    static CatBot plugin;

	UUID targetID;
	String[] args;
	String sender;
	Date timeSent;
	
	static void enable(CatBot cb)
	{
	    plugin = cb;
	}	


	/**
	 * Invoked when a command is
	 * first scheduled by a player
	 * 
	 * @param cmd Command sent
	 * @param cmdSender Command sender
	 * 
	 * @throws IllegalArgumentException when cmd is too short, or target does not exist.
	 */
	ScheduledCommand(String[] cmd, CommandSender cmdSender)
	{
		//Ensure there are not too many commands
		if(commands.size() >= fileCmdsLimit)
		{
			log.severe(CatBot.cPrefix + "Too many commands have been created (over " + fileCmdsLimit + "). Please"
					+ " check fild and possibly reload commands if you know what you're doing.");
			cmdSender.sendMessage(CatBot.prefix + "Command could not be created, there were too many in"
					+ "file. Ask a Senior Admin to try and do something.");
			throw new IllegalArgumentException("Eror scheduling command: Command limit (" + fileCmdsLimit + ") reached.");
		}
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


	/**
	 * Invoke to read command i from
	 * file on server startup
	 * 
	 * @param id ID of command
	 */
	ScheduledCommand(String i)
	{
		ConfigurationSection section = cmdsCfg.getConfigurationSection(SECTION_NAME + "." + i);
		targetID = UUID.fromString(section.getString("Sender"));
		args = section.getString("Command").split("\\b");
		sender = section.getString("Sender");
		
		//Make sure there are no errors parsing date
		try {
			timeSent = df.parse(section.getString("TimeSent"));
		} catch (ParseException e) {
			log.severe(CatBot.cPrefix + "Date was stored wrongly in file! Will be reset to the epoch. "
					+ "Command should still work fine, but here is the error.");
			timeSent = new Date(0L);
			e.printStackTrace();
		}
	}


	/**
	 * Read all commands from file, and
	 * add to the commands list.
	 * Will not preserve ID because there
	 * is no need to.
	 */
	public static void readAllCmds()
	{
		/*int counter = 0;
		for(String id:cmdsCfg.getKeys(false))
		{
			commands.add(new ScheduledCommand(id));
			counter++;
		}
		log.info(CatBot.cPrefix + "Loaded " + counter + " commands from file.");
		*/
		//Commenting out for now so this can be left in without affecting anything else while I work on it
	}


	/**
	 * Add this command to
	 * configuration, ready
	 * to save.
	 */
	public void addToCfg()
	{
		final String sectionName = SECTION_NAME + "." + String.valueOf(commands.indexOf(this));
		final ConfigurationSection section = cmdsCfg.createSection(sectionName);
		String saveCommand = "";
		for(String s:args)
			saveCommand += s + " ";
		
		section.set("Target", targetID.toString());
		section.set("TimeSent", df.format(timeSent));
		section.set("Sender", sender);
		section.set("Command", saveCommand);
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
    * Save default commands.yml file.
    * Should only be used if commands.yml
    * doesn't exist.
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
     * Save current commands to
     * commands.yml
     * Will probably overwrite contents
     * of file.
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
    public static boolean loadCmdsFile()
    {
            try {
                cmdsCfg.load(cmdsFile);
            } catch (FileNotFoundException e) {
                log.severe(CatBot.cPrefix + "Error finding commands file!");
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                log.severe(CatBot.cPrefix + "Error loading commands file!");
                e.printStackTrace();
                return false;
            } catch (InvalidConfigurationException e) {
                log.severe(CatBot.cPrefix + "Error reading commands file!");
                e.printStackTrace();
                return false;
            }
            readAllCmds();
            return true;
    }

    

    /*
     * Perform command at index
     * (if possible)
     * 
     * @param index Command to send
     * @return Successful?
     
    public boolean sendCmd(String index)
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
     
    public static void purgeCmds()
    {
        cmdsCfg.set("Commands", null);
        for(ScheduledCommand cmd:commands)
            commands.remove(cmd);
        cmdsCfg.createSection("Commands");
    }
*/
}
