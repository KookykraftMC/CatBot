package com.kookykraftmc.CatBot;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class CatFilterEvents implements Listener 
{
	private static final String SPLIT_STRING = "[,./:;-`~()\\[\\]{}+ !?^¨=><_'´]";
	static public CatBot plugin;
	static public List<String> badWords;
	static public String denyMsg;
	static public Random rdm = new Random();
	static public List<String> replaceWords;
	static Logger log = CatBot.log;

	/**
	 * Set of filtered words
	 */
	private static final Set<String> BLOCKED_WORDS = ImmutableSet.of(
			"fuck", "bitch", "pussy", "gay", "faggot", "fgt", "cunt", "nigger",
			"nigga", "niq", "n1g", "n1gg3r", "n1gger", "shit", "sh1t", "sh*t",
			"sh!t", "rape", "sex", "bitches", "fucking", "fagot", "fag", "queer",
			"fuk", "fk", "feg", "nig", "niqqa", "dick", "ddos", "dox", "ddosing",
			"doxing", "ddosing", "doxed", "ass", "titties", "boobs", "boobies",
			"vagina", "cock", "penis", "boner", "fagg", "jizz", "cum", "rekt",
			"wreckt", "reckt", "wrekt", "wrect", "rect", "ez", "ezpz", "fuked",
			"fked", "fucked", "fking", "fkin", "fukin", "fuking", "dik", "kys",
			"nazi", "swastika", "retard", "g@y", "d1ck", "fck", "fckin", "fcking",
			"fcked", "dck", "ddosed", "ddossed", "fukk", "ngg", "btch", "f4g",
			"qu33r", "b1tch", "nigg@", "nigg", "fqgt", "ngga", "fagitq", "queeer",
			"niga", "queerbag", "dic", "jackass", "shitty", "sh1itty", "fagit",
			"faggots", "fuq", "fegit", "fegits", "feg", "fuqing", "feqit", "fajit",
			"feqqit", "feggit", "feggits", "feqqits", "fuqk", "trashbag", "trash",
			"fajits", "pussys", "pussies", "fuxing", "fux", "fuxer", "fuxed",
			"wrecked", "wreck", "wrecking", "wreking", "wrekted", "wrekt", "faj",
			"lag"
	);
	
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
		if(p.hasPermission("catbot.bypassfilter")) {
			return;
		}

		//catch any badwords in chat, cancel out the message and warn the user.
		for (String word : message.toLowerCase().split(SPLIT_STRING)) {
			if (!BLOCKED_WORDS.contains(word.toLowerCase())) {
				continue;
			}

			for (Player online : Bukkit.getOnlinePlayers()) {
				if (online.hasPermission("catbot.staff.notify")) {
					online.sendMessage(CatBot.prefix + " player " + ChatColor.RED + p.getName() + " tried to say " + ChatColor.GOLD + word);
				}
			}

			p.sendMessage(CatBot.prefix + denyMsg);
			e.setCancelled(true);
			break;

		}

		/*for (String bad:badWords)
		{
			if(message.toLowerCase().matches("(?iu).*" + bad + ".*"))
			{
				String replaceWord = replaceWords.get(rdm.nextInt(replaceWords.size()));
				message = message.replaceAll("(?iu)" + bad,replaceWord);
				isBad = true;
			}
		}
		e.setMessage(message);
		if (isBad)
		{
			log.info(CatBot.cPrefix + p.getName() + " tried to use a bad word.");
			p.sendMessage(CatBot.prefix + denyMsg);
		}*/
	}
	
}