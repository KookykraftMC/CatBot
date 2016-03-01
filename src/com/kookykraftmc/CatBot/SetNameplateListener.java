package com.kookykraftmc.CatBot;

import java.util.List;
import java.util.logging.Logger;

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
	public Permission perms;
	
	public SetNameplateListener(CatBot catBot)
	{
		plugin = catBot;
	    RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
	    perms = rsp.getProvider();
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
		String pGroup;
		
		board.resetScores(offPlayer);
		p.setScoreboard(board);
		pGroup = perms.getPlayerGroups(p)[0].toLowerCase();

		int i = groupList.indexOf(pGroup);
		if(i==-1)
		{
			cat.addPlayer(offPlayer);
			log.info(CatBot.prefix + "Assigning [Cat] (default) tag to " + name + ".");
		}
		else
		{
			groups[i].addPlayer(offPlayer);
			log.info(CatBot.prefix + "Assigning " + pGroup + " tag to " + name + ".");
		}
	}


	public static void loadCfg()
	{
		clearTeams();
		groupList = plugin.getConfig().getStringList("GroupColours");
		groups = new Team[groupList.size()-1];
		String[] groupCol;
		String tag;
		for(int i = 0;i<groupList.size()-1;i++)
		{
			groupCol = groupList.get(i).split(",");
			groups[i] = board.registerNewTeam(groupCol[0]);
			switch(groupCol[1].charAt(0))
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
				tag = (ChatColor.getByChar(groupCol[1].charAt(0)) + "[" + groupCol[0] + "]");
				break;
			}
			board.getTeam(groupCol[0]).setPrefix(tag);
			
			//Need to do this to make it searchable
			groupList.set(i, groupCol[0].toLowerCase());
		}
		log.info(CatBot.prefix + "Nameplate groups loaded.");
	}
	
	public static void clearTeams()
	{
		for(Team t:board.getTeams())
			t.unregister();
		log.info(CatBot.prefix + "Refreshing Teams.");
	}

}
