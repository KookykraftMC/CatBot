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
		PluginDescriptionFile pdf = getDescription();
		File cfg = new File(getDataFolder(), "config.yml");
        if (!cfg.exists())
        {
            log.info(cPrefix + "Config not found, creating!");
            saveDefaultConfig();
        }
        else if(!this.getConfig().contains("Version")||Double.parseDouble((pdf.getVersion()))!=this.getConfig().getDouble("Version"))
        {
        	log.info(cPrefix + "Outdated config found, saving new version.");
        	saveDefaultConfig();
        }
        else
        {
            log.info(cPrefix + "Loading config.");
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

        this.getCommand("findname").setExecutor(new CommandFindName());
        this.getCommand("catbot").setExecutor(new CommandGeneral());
        log.info(cPrefix + "Commands Enabled.");

		log.info(pdf.getName() + " " + pdf.getVersion() + " is now enabled.");
		
		
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