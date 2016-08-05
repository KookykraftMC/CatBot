package com.kookykraftmc.catbot;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.kookykraftmc.catbot.commands.CommandFindName;
import com.kookykraftmc.catbot.commands.CommandGeneral;
import com.kookykraftmc.catbot.commands.CommandRTP;
//import com.kookykraftmc.catbot.listeners.BungeeListener;
import com.kookykraftmc.catbot.listeners.CatFilterEvents;
import com.kookykraftmc.catbot.listeners.JoinEvents;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class CatBot extends JavaPlugin
{
	final public static String prefix = ChatColor.DARK_RED + "[Bot]+" + ChatColor.LIGHT_PURPLE + "CatBot" + ChatColor.WHITE + ": " + ChatColor.BLUE;
	final public static String cPrefix = "[CatBot]";
	public final static Logger log = Logger.getLogger("CatBot");
	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

	public RegisteredServiceProvider<Permission> rspPerms;
    public RegisteredServiceProvider<Chat> rspChat;


	public void onEnable()
	{
	    rspPerms = this.getServer().getServicesManager().getRegistration(Permission.class);
	    rspChat = this.getServer().getServicesManager().getRegistration(Chat.class);
		PluginDescriptionFile pdf = getDescription();
		
        /*
        CatBroadcast broadcaster = new CatBroadcast(this);
        this.getCommand("catbroadcast").setExecutor(broadcaster);
        Bukkit.getPluginManager().registerEvents(broadcaster, this);
        */

		File cfg = new File(getDataFolder(), "config.yml");
        if (!cfg.exists())
        {
            log.info(cPrefix + "Config not found, creating!");
            saveDefaultConfig();
        }
        else if(!this.getConfig().contains("Version")||!pdf.getVersion().equals(this.getConfig().getString("Version")))
        {
            log.info(cPrefix + "Outdated config found, saving new version.");
            cfg.delete();
        	saveDefaultConfig();
        }
        else
        {
            log.info(cPrefix + "Loading config.");
        }

        if(getConfig().getStringList("BadWords").isEmpty()||getConfig().getStringList("ReplaceWords").isEmpty())
        {
            log.warning(cPrefix + "No chat filter words found. Chat filter disabled.");
        }
        else
        {
            getServer().getPluginManager().registerEvents(new CatFilterEvents(this), this);
            log.info(cPrefix + "Chat Filter Enabled.");
        }
		
        if(getConfig().getStringList("GroupColours").isEmpty())
        {
            log.warning(cPrefix + "No Group Colours found. Nameplate changer disabled.");
        }
        else
        {
            getServer().getPluginManager().registerEvents(new JoinEvents(this), this);
            log.info(cPrefix + "Nameplate Changer Enabled.");
        }

        this.getCommand("findname").setExecutor(new CommandFindName());
        this.getCommand("catbot").setExecutor(new CommandGeneral(this));
        
        if(this.getConfig().getBoolean("RTP.Enabled"))
        {
            log.info(cPrefix + "Enabling RTP");
            this.getCommand("rtp").setExecutor(new CommandRTP(this));
        }
        else
        {
           log.warning(cPrefix + "RTP Command disabled in config.");
        }
        
        if(Bukkit.getServer().getOnlinePlayers().size() > 0)
        {
            for(Player p:Bukkit.getServer().getOnlinePlayers())
            {
                JoinEvents.assignPlate(p);
            }
        }
        
        //BungeeListener bungeeStuff = new BungeeListener(this);
        //Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bungeeStuff);
        //Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        log.info(pdf.getName() + " " + pdf.getVersion() + " is now enabled.");
	}

	public void onDisable()
	{
	    /* This doesn't work yet, as worlds are saved after plugins disable :(
	     * 
	     * 		if(!getConfig().getStringList("ResetWorlds").isEmpty())
		{
		    File serverDir = this.getServer().getWorldContainer();
		    log.info(serverDir.getAbsolutePath().subSequence(0, (int) (serverDir.getAbsolutePath().length() - 2)) + File.separator + "world" + File.separator + "DIM1");
		    for(String world:getConfig().getStringList("ResetWorlds"))
                deleteDir(new File(serverDir.getAbsolutePath().subSequence(0, (int) (serverDir.getAbsolutePath().length() - 2)) + File.separator + "world" + File.separator + world));
		}
		*/
	    JoinEvents.clearTeams();
	    PluginDescriptionFile pdf = getDescription();
	    log.info(pdf.getName() + pdf.getVersion() + " is now disabled");
	}
	
	/*public static void deleteDir(File element)
	{
	    if (element.isDirectory())
	    {
	        for (File sub : element.listFiles())
	        {
	            //recursion :(
	            deleteDir(sub);
	        }
	    }
	    element.delete();
	}*/

	public void reloadCfg()
	{
		this.reloadConfig();
		CatFilterEvents.loadCfg();
		JoinEvents.loadCfg();
		CommandGeneral.loadCfg();
		//FileManager.loadCmds();
	}
}
