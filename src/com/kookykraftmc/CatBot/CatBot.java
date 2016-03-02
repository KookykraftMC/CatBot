package com.kookykraftmc.CatBot;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CatBot extends JavaPlugin 
{
	public static String prefix = ChatColor.DARK_RED + "[Bot]+" + ChatColor.LIGHT_PURPLE + "CatBot" + ChatColor.WHITE + ": " + ChatColor.BLUE;
	public static String cPrefix = "[CatBot]";
	static Logger log = Logger.getLogger("CatBot");
	public void onEnable() 
	{
		File cfg = new File(getDataFolder(), "config.yml");
        if (!cfg.exists())
        {
            log.info("Catbot config not found, creating!");
            saveDefaultConfig();
        } 
        else
        {
            log.info("Loading CatBot config");
        }
        if(getConfig().getStringList("BadWords").isEmpty()||getConfig().getStringList("ReplaceWords").isEmpty())
        {
        	log.info(cPrefix + "No chat filter words found. Chat filter disabled.");
        }
        else
        {
		getServer().getPluginManager().registerEvents(new CatFilterEvents(this), this);
		log.info(cPrefix + "Chat Filter Enabled.");
        }
		
        if(getConfig().getStringList("GroupColours").isEmpty())
        {
        	log.info(cPrefix + "No Group Colours found. Nameplate changer disabled.");
        }
        else
        {
        getServer().getPluginManager().registerEvents(new SetNameplateListener(this), this);
        log.info(cPrefix + "Nameplate Changer Enabled.");
        }
        
        this.getCommand("catbot").setExecutor(new CommandGeneral());
        log.info(cPrefix + "Commands Enabled.");
        
		PluginDescriptionFile pdf = getDescription();
		log.info(pdf.getName() + " " + pdf.getVersion() + " is now enabled");
		
	}
	public void onDisable()
	{
		SetNameplateListener.clearTeams();
		PluginDescriptionFile pdf = getDescription();
		log.info(pdf.getName() + pdf.getVersion() + " is now disabled");
	}
	public void reloadCfg()
	{
		this.reloadConfig();
		CatFilterEvents.loadCfg();
		SetNameplateListener.loadCfg();
	}
	
}