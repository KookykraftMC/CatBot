package com.kookykraftmc.catbot.listeners;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.kookykraftmc.catbot.CatBot;


public class CatFilterEvents implements Listener 
{
    final static public Random rdm = new Random();
    final static Logger log = CatBot.log;
    static public CatBot plugin;
    static public HashSet<String> badWords;
    static public String denyMsg;
    static public List<String> replaceWords;
    static public HashSet<List<String>> que = new HashSet<List<String>>();
    
    public CatFilterEvents(CatBot catBot)
    {
        plugin = catBot;
        loadCfg();
    }
    public static void loadCfg()
    {
        //Get things from the config file
        badWords = new HashSet<String>(plugin.getConfig().getStringList("BadWords"));
        denyMsg = plugin.getConfig().getString("DenyMsg");
        replaceWords = plugin.getConfig().getStringList("ReplaceWords");
        /*que.removeAll(que);
        for(String s:plugin.getConfig().getStringList("Questions"))
        {
            String[] splitQuestion = s.split(";");
            if(splitQuestion.length != 2)
            {
                log.warning(CatBot.cPrefix + "Invalid question string in config: " + s);
                return;
            }
            que.add(Arrays.asList(splitQuestion));
            log.info(CatBot.cPrefix + "Will answer question \"" + splitQuestion[0] + "\" with answer \"" + splitQuestion[1] + "\".");
        }*/
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e)
    {
        String originalMsg = e.getMessage();
        Player p = e.getPlayer();

        //Answer Questions
        for(List<String> qAndA:que)
        {
            String toTest = "(?iu).*" + qAndA.get(0) + ".*";
            if(originalMsg.matches(toTest))
            {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    p.sendMessage(CatBot.prefix + qAndA.get(1));
                    log.info(CatBot.cPrefix + "Answering with " + qAndA.get(1));
                }, 10L);
            }
        }

        //Filter
        String msg = p.hasPermission("catbot.bypassfilter")?originalMsg:filter(originalMsg);

        if (!msg.equals(originalMsg))
        {
            e.setMessage(msg);
            log.info(CatBot.cPrefix + p.getName() + " tried to use a bad word.");
            log.info(CatBot.cPrefix + "Original message: " + originalMsg);
            p.sendMessage(CatBot.prefix + denyMsg);
        }
    }
    
    public String filter(String message)
    {
            for (String bad:badWords)
            {
                if(message.toLowerCase().matches("(?iu).*" + bad + ".*"))
                {
                    String replaceWord = replaceWords.get(rdm.nextInt(replaceWords.size()));
                    message = message.replaceAll("(?iu)" + bad,replaceWord);
                }
            }
            return message;
    }
}
