package com.kookykraftmc.CatBot;

import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class CatFilterEvents implements Listener 
{
	static public CatBot plugin;
	static public List<String> badWords;
	static public String denyMsg;
	static public Random rdm = new Random();
	static public List<String> replaceWords;
	public String replaceWord;
	
	public CatFilterEvents(CatBot catBot)
	{
		plugin = catBot;
		loadCfg();
	}
	public static void loadCfg()
	{
		//Get things from the config file
		badWords = plugin.getConfig().getStringList("BadWords");
		denyMsg = plugin.getConfig().getString("DenyMsg");
		replaceWords = plugin.getConfig().getStringList("ReplaceWords");
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		String message = e.getMessage();
		Player p = e.getPlayer();
		boolean isBad = false;
		
		for (String bad:badWords)
		{
			if(message.toLowerCase().matches("(?iu).*" + bad + ".*"))
			{
				replaceWord = replaceWords.get(rdm.nextInt(replaceWords.size()));
				message = message.replaceAll("(?iu)" + bad,replaceWord);
				isBad = true;
			}
		}
		e.setMessage(message);
		if (isBad)
			p.sendMessage(CatBot.prefix + denyMsg);
		
	}
	
}