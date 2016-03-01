package com.kookykraftmc.CatBot;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CatBot extends JavaPlugin 
{
	static String prefix = ChatColor.DARK_RED + "[Bot]+" + ChatColor.LIGHT_PURPLE + "CatBot" + ChatColor.WHITE + ": " + ChatColor.BLUE;
	public void onEnable() 
	{
		Logger l = Logger.getLogger("CatBot");
		File cfg = new File(getDataFolder(), "config.yml");
        if (!cfg.exists())
        {
            l.info("Catbot config not found, creating!");
            saveDefaultConfig();
        } 
        else
        {
            l.info("Loading CatBot config");
        }
        if(getConfig().getStringList("BadWords").isEmpty()||getConfig().getStringList("ReplaceWords").isEmpty())
        {
        	l.info(prefix + "No chat filter words found. Chat filter disabled.");
        }
        else
        {
		getServer().getPluginManager().registerEvents(new CatFilterEvents(this), this);
		l.info(prefix + "Chat Filter Enabled.");
        }
		
        if(getConfig().getStringList("GroupColours").isEmpty())
        {
        	l.info(prefix + "No Group Colours found. Nameplate changer disabled.");
        }
        else
        {
        getServer().getPluginManager().registerEvents(new SetNameplateListener(this), this);
        l.info(prefix + "Nameplate Changer Enabled.");
        }
        
        this.getCommand("catbot").setExecutor(new CommandGeneral());
        l.info(prefix + "Commands Enabled.");
        
		PluginDescriptionFile pdf = getDescription();
		l.info(pdf.getName() + " " + pdf.getVersion() + " is now enabled");
		
	}
	public void onDisable()
	{
		SetNameplateListener.clearTeams();
		PluginDescriptionFile pdf = getDescription();
		Logger l = Logger.getLogger("CatBot");
		l.info(pdf.getName() + pdf.getVersion() + " is now disabled");
	}
	public void reloadCfg()
	{
		this.reloadConfig();
		CatFilterEvents.loadCfg();
		SetNameplateListener.loadCfg();
	}
	
}