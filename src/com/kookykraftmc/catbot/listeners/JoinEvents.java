package com.kookykraftmc.catbot.listeners;

import java.util.Collection;
import java.util.HashSet;
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

import com.kookykraftmc.catbot.CatBot;

public class JoinEvents implements Listener
{
    final static Logger log = Bukkit.getServer().getLogger();
    final static ScoreboardManager boardManager = Bukkit.getScoreboardManager();
    static private HashSet<String> whitelist = new HashSet<String>();
    public static boolean isWhitelisted = false;
    static private String kickMsg = "This server is currently whitelisted for maintenance. Sorry!";
    static CatBot plugin;
    static List<String> groupList;
    static Scoreboard board;
    static Team[] groups;
    static Team cat;
    static Team senioradmin;
    static Team ultimatekat;
    static Permission permsInfo;
    static Chat chatInfo;

    public JoinEvents(CatBot catBot)
    {
        plugin = catBot;
        permsInfo = plugin.rspPerms.getProvider();
        chatInfo = plugin.rspChat.getProvider();
        board = boardManager.getNewScoreboard();
        loadCfg();
        cat = board.registerNewTeam("Cat");
        cat.setPrefix(ChatColor.DARK_GRAY + "[Cat]");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        assignPlate(p);
        
        //Whitelisting
        /*if(isWhitelisted)
            if(!whitelist.contains(p.getName().toLowerCase()))
                p.kickPlayer(kickMsg);
                */
    }

    @SuppressWarnings("deprecation")
    public static void assignPlate(Player p)
    {
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
        
        
        //Whitelisting
        /*addToWhitelist(plugin.getConfig().getStringList("Whitelist.Players"));
        isWhitelisted = plugin.getConfig().getBoolean("Whitelist.Enabled");
        if(isWhitelisted)
        {
            log.severe(CatBot.cPrefix + "THIS SERVER IS WHITELISTED! Players will not be able to join until you run /catbot whitelist off");
        }*/
    }
    
    public static void clearTeams()
    {
        if(board != null)
            for(Team t:board.getTeams())
                t.unregister();
    }
    
    public static boolean enableWhitelist()
    {
        if(isWhitelisted) return false;
        isWhitelisted = true;
        for(Player p:Bukkit.getOnlinePlayers())
        {
            if(!whitelist.contains(p.getName().toLowerCase())) p.kickPlayer(kickMsg);
        }
        log.severe(CatBot.cPrefix + "WHITELISTING HAS BEEN ENABLED. To disable, use /catbot whitelist off");
        return true;
    }

   public static boolean disableWhitelist()
    {
        if(!isWhitelisted) return false;
        else isWhitelisted = false;
        log.severe(CatBot.cPrefix + "Whitelisting has been disabled.");
        return true;
    }

   public static boolean toggleWhitelist()
   {
       if(isWhitelisted)
           disableWhitelist();
       else
           enableWhitelist();
       return isWhitelisted;
   }
   
   public static void addToWhitelist(String s)
   {
       whitelist.add(s.toLowerCase());
       log.info(s + " was added to the whitelist.");
   }
   
   public static void addToWhitelist(Collection<String> ss)
   {
       String ls = "";
       for(String s:ss)
       {
           whitelist.add(s);
           ls += s + "; ";
       }
       log.info(CatBot.cPrefix + "Adding players to whitelist: " + ls);
   }
   
   public static boolean removeFromWhitelist(String s)
   {
       boolean didRemove = whitelist.remove(s);
       log.info(CatBot.cPrefix + "Attempting to remove " + s + " from blacklist. " + (didRemove?"Success":"Fail"));
       return didRemove;
   }
}
