package com.kookykraftmc.CatBot;

import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
	Permission permsInfo;
	Chat chatInfo;

	public SetNameplateListener(CatBot catBot)
	{
		plugin = catBot;
	    permsInfo = plugin.rspPerms.getProvider();
	    chatInfo = plugin.rspChat.getProvider();
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
		String name = p.getName();
		//Warn if player has no prefix/group because that's pretty strange
		if((permsInfo.getPlayerGroups(p).length==0)||chatInfo.getPlayerPrefix(p).isEmpty())
		{
			log.warning(CatBot.prefix + name + " had no prefix and/or group!");
			return;
		}

		//Reset player's scoreboard just in case
		board.resetScores(p);
		p.setScoreboard(board);
		
		//Iterate through player's groups to find one with highest priority
		int pIndex = -1;
		if(permsInfo.getPlayerGroups(p).length > 0)
			for(String g:permsInfo.getPlayerGroups(p))
			{
				int i = groupList.indexOf(g);
				pIndex = i>pIndex?i:pIndex;
			}

		//Now iterate through prefixes
		String pPrefix = chatInfo.getPlayerPrefix(p);
		for(String grp:groupList)
		{
		    int i = groupList.indexOf(grp);
		    if(pPrefix.toLowerCase().contains(grp.toLowerCase())&&i>pIndex)
		        pIndex = groupList.indexOf(grp);
		}
		
		//Set player's prefix as long as they have one
		if(pIndex!=-1)
		{
			groups[pIndex].addPlayer(p);
			log.info(CatBot.cPrefix + "Assigning [" + groups[pIndex].getName() + "] tag to " + name + ".");
		}
		else
		{
			cat.addPlayer(p);
			log.info(CatBot.cPrefix + "Assigning [Cat] (default) tag to " + name + ".");
		}
	}

	public static void loadCfg()
	{
		clearTeams();
		groupList = plugin.getConfig().getStringList("GroupColours");
		groups = new Team[groupList.size()];
		String[] groupCol;
		String tag;
		char col;
		for(int i = 0;i<groupList.size();i++)
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
			case 'g':
				tag = (ChatColor.WHITE + "[Guest]");
				break;
			default:
				tag = (ChatColor.getByChar(col) + "[" + groupCol[0] + "]");
				break;
			}
			board.getTeam(groupCol[0]).setPrefix(tag);
			groupList.set(i, groupCol[0].toLowerCase());
		}
		log.info(CatBot.cPrefix + "Adding groups from config.");
	}
	
	public static void clearTeams()
	{
		for(Team t:board.getTeams())
			t.unregister();
	}

}
