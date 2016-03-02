package com.kookykraftmc.CatBot;

import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class SetNameplateListener implements Listener
{
	static CatBot plugin;
	static List<String> groupList;
	static final Logger log = Bukkit.getServer().getLogger();
    static ScoreboardManager boardManager = Bukkit.getScoreboardManager();
    static Scoreboard board;
    static Team[] groups;
    static Team cat;
    static Team senioradmin;
    static Team ultimatekat;
	static public Permission permsInfo;
	static public Chat chatInfo;

	public SetNameplateListener(CatBot catBot)
	{
		plugin = catBot;
	    RegisteredServiceProvider<Permission> rspPerms = plugin.getServer().getServicesManager().getRegistration(Permission.class);
	    permsInfo = rspPerms.getProvider();
	    RegisteredServiceProvider<Chat> rspChat = plugin.getServer().getServicesManager().getRegistration(Chat.class);
	    chatInfo = rspChat.getProvider();
	    board = boardManager.getNewScoreboard();
		loadCfg();
	    cat = board.registerNewTeam("Cat");
	    cat.setPrefix(ChatColor.DARK_GRAY + "[Cat]");
	}
    
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(p.getUniqueId());
		String name = p.getName();
		if((permsInfo.getPlayerGroups(p).length==0)||chatInfo.getPlayerPrefix(p).isEmpty())
		{
			log.warning(CatBot.prefix + name + " had no prefix and/or group!");
			return;
		}
		String pPrefix = chatInfo.getPlayerPrefix(p);
		pPrefix = pPrefix.substring(3, pPrefix.length()-1).toLowerCase();
		if(pPrefix.contains("helper"))
			pPrefix = "helper";
		else if(pPrefix.contains("developer"))
			pPrefix = "developer";
		board.resetScores(offPlayer);
		p.setScoreboard(board);
		int i;
		int prefIndex = groupList.indexOf(pPrefix);
		int groupIndex = -1;
		if(permsInfo.getPlayerGroups(p).length > 0)
		{
			for(String g:permsInfo.getPlayerGroups(p))
			{
				i = groupList.indexOf(g);
				groupIndex = i>groupIndex?i:groupIndex;
			}
		}
		i = groupIndex<prefIndex?prefIndex:groupIndex;
		
		if(i!=-1)
		{
			groups[i].addPlayer(offPlayer);
			log.info(CatBot.cPrefix + "Assigning [" + groups[i].getName() + "] tag to " + name + ".");
		}
		else
		{
			cat.addPlayer(offPlayer);
			log.info(CatBot.cPrefix + "Assigning [Cat] (default) tag to " + name + ".");
		}
	}


	public static void loadCfg()
	{
		clearTeams();
		groupList = plugin.getConfig().getStringList("GroupColours");
		groups = new Team[groupList.size()-1];
		String[] groupCol;
		String tag;
		char col;
		for(int i = 0;i<groupList.size()-1;i++)
		{
			groupCol = groupList.get(i).split(",");
			groups[i] = board.registerNewTeam(groupCol[0]);
			col = groupCol[1].charAt(0);
			switch(col)
			{
			case 's':
				tag = (ChatColor.DARK_PURPLE + "[Sr.Admin]" + ChatColor.LIGHT_PURPLE);
				break;
			case 'u':
				tag = (ChatColor.DARK_PURPLE + "[U" + ChatColor.LIGHT_PURPLE + "Ka" + ChatColor.DARK_PURPLE + "t]");
				break;
			case 'w':
				tag = (ChatColor.DARK_PURPLE + "[Owner]" + ChatColor.LIGHT_PURPLE);
				break;
			default:
				tag = (ChatColor.getByChar(col) + "[" + groupCol[0] + "]");
				break;
			}
			board.getTeam(groupCol[0]).setPrefix(tag);
			
			//Need to do this to make it searchable
			groupList.set(i, groupCol[0].toLowerCase());
		}
		log.info(CatBot.cPrefix + "Adding groups from config.");
	}
	
	public static void clearTeams()
	{
		for(Team t:board.getTeams())
			t.unregister();
		log.info(CatBot.cPrefix + "Removing groups.");
	}

}
