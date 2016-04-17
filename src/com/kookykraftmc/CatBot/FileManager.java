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
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public class FileManager
{
	static FileConfiguration cmds;
	static File cmdsFile;
	static Logger log = CatBot.log;
	static CatBot plugin;
	SimpleDateFormat df = CatBot.dateFormat;
	
	FileManager(CatBot cb)
	{
		plugin = cb;
	}
	
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

	public static void copyDefault()
	{
        if(!cmdsFile.exists())
    	{
        	cmdsFile.getParentFile().mkdirs();
        	copy(plugin.getResource("commands.yml"), cmdsFile);
    	}
	}

	public static void saveCmds()
	{
	    try {
	        cmds.save(cmdsFile);
	    } catch (IOException e) {
	    	log.severe(CatBot.cPrefix + "Error saving commands file!");
	        e.printStackTrace();
	    }
	}

	public static void loadCmds()
	{
	        try {
				cmds.load(cmdsFile);
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

	@SuppressWarnings("deprecation")
	public boolean createCmd(String cmd, CommandSender sender, String target) //done
	{
		//Find next available index to save command
		Set<String> existingIndices = cmds.getConfigurationSection("Commands").getKeys(false);
		boolean isFull = true;
		String index = null;
		String targetPlayer;

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
			targetPlayer = cmdArgs[2];
			if(!Bukkit.getOfflinePlayer(cmdArgs[2]).hasPlayedBefore())
				sender.sendMessage(CatBot.prefix + "That player has never joined the server before! Command will " + 
						"still be saved, but make sure you've checked the name.");
		}
		
		//Save command to file
		cmds.set("Commands." + index + ".PlayerName", targetPlayer);
		cmds.set("Commands." + index + ".Sender", sender.getName());
		cmds.set("Commands." + index + ".Command", cmd);
		cmds.set("Commands." + index + ".DateSent", df.format(new Date()));
		
		return true;
	}
	
	public boolean sendCmd(String index) //done
	{
		//Make sure it exists
		if(cmds.getConfigurationSection(index) == null)
			return false;
		String[] args = cmds.getString("Commands." + index + ".Command").split("\\b");
		@SuppressWarnings("deprecation")
		CommandSender sender = (CommandSender) Bukkit.getOfflinePlayer(cmds.getString("Commands." + index + ".Sender"));
		plugin.getCommand("catbot").getExecutor().onCommand(sender, null, null, args);
		return true;
	}

	public boolean removeCmd(String index) //done
	{
		if(cmds.getConfigurationSection("Commands." + index) == null)
			return false;
		cmds.set(index, null);
		saveCmds();
		return true;
	}

	public void purgeCmds() //done
	{
		cmds.set("Commands", null);
		cmds.createSection("Commands");
	}
	
	/*TODO:
	 * Purge before certain date?
	 */
}
