package com.kookykraftmc.CatBot;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin 
{
	static String CatBotPrefix = ChatColor.DARK_RED + "[Bot]+" + ChatColor.BLUE + "CatBot" + ChatColor.WHITE + ": " + ChatColor.RED;
	public void onEnable() 
	{
		Logger l = Logger.getLogger("CatBot");
		getServer().getPluginManager().registerEvents(new CatFilterEvents(this), this);
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
        this.getCommand("catbot").setExecutor(new CommandGeneral());
		PluginDescriptionFile pdf = getDescription();
		l.info(pdf.getName() + " " + pdf.getVersion() + " is now enabled");
		
	}
	public void onDisable()
	{
		PluginDescriptionFile pdf = getDescription();
		Logger l = Logger.getLogger("CatBot");
		l.info(pdf.getName() + pdf.getVersion() + " is now disabled");
	}
	
}