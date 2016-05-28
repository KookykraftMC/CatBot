package com.kookykraftmc.catbot.commands;

import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kookykraftmc.catbot.CatBot;

public class CommandRTP implements CommandExecutor
{
	private static final double BORDER = 10000;
	private static final int MAX_USES = 3;
	private static final String CMD_NAME = "rtp";
	private static final Logger log = CatBot.log;
	private static final Random rdm = new Random(System.nanoTime());
	private static final HashSet<Biome> biomeBlacklist = new HashSet<Biome>();
	private static final HashSet<Material> allowedLandingBlocks = new HashSet<Material>();
	private static final int safeZoneRadius = 2;
	private static final int safeZoneHeight = 4;
	private static final int maxTries = 50;

	static CatBot plugin;

	public CommandRTP(CatBot cb)
	{
		plugin = cb;
		biomeBlacklist.add(Biome.OCEAN);
		biomeBlacklist.add(Biome.DEEP_OCEAN);
		biomeBlacklist.add(Biome.FROZEN_OCEAN);
		biomeBlacklist.add(Biome.RIVER);
		biomeBlacklist.add(Biome.FROZEN_RIVER);
		biomeBlacklist.add(Biome.BEACH);
		biomeBlacklist.add(Biome.COLD_BEACH);
		biomeBlacklist.add(Biome.STONE_BEACH);
		allowedLandingBlocks.add(Material.DIRT);
		allowedLandingBlocks.add(Material.GRASS);
		allowedLandingBlocks.add(Material.SAND);
		allowedLandingBlocks.add(Material.HARD_CLAY);
		allowedLandingBlocks.add(Material.MYCEL);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
        if(!(sender instanceof Player) && args.length == 0)
        {
            sender.sendMessage(CatBot.cPrefix + "The console cannot RTP. You can use /rtp (player)");
            return true;
        }
        else if(!(sender instanceof Player))
        {
        	Player p = Bukkit.getPlayer(args[0]);
        	if(p==null)
        	{
        		log.info(CatBot.cPrefix + "Could not find player with name " + args[0]);
        		return true;
        	}
        	else
        	{
        		rtp(p);
        		log.info(CatBot.cPrefix + "RTPing " + p.getName());
        		return true;
        	}
        }
        Player p = (Player) sender;
        String name = p.getName();

		if(args.length == 0)
		{
	        if(!CommandLimiter.tryUsing(p,MAX_USES,CMD_NAME))
            {
                p.sendMessage(CatBot.prefix + "You cannot use RTP again until the next restart.");
                log.info(CatBot.cPrefix + name + " tried to RTP, but they had none left.");
                return true;
            }
	        else
	        {
	        	p.sendMessage(CatBot.prefix + "Teleporting to a random location. You have " + CommandLimiter.usesLeft(p, CMD_NAME)
	        			+ " rtp uses left until the next server restart.");
	        	log.info(CatBot.cPrefix + "Attempting to RTP " + name);
	        	rtp(p);
	        }
		}
		return true;
	}
	
	private boolean rtp(final Player p)
	{
	    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run()
            {
                final World w = p.getLocation().getWorld();
                final int max = w.getMaxHeight();
                final String name = p.getName();
                boolean locationDone = false;

                int counter = 0;
                while (!locationDone)
                {
                    if(++counter > maxTries)
                    {
                        p.sendMessage(CatBot.prefix + "Couldn't find a safe place to teleport to :( please contact staff, preferably Kraise.");
                        log.severe(CatBot.cPrefix + "Could not find a place to RTP " + name + ". This is a big problem, tell someone!");
                        log.info(CatBot.cPrefix + name + " is at " + p.getLocation().toString());
                        return;
                    }
                    final Location loc = new Location(w, (rdm.nextDouble() - 0.5 ) * 2 * BORDER, max, (rdm.nextDouble() - 0.5 ) * 2 * BORDER);
                    if(biomeBlacklist.contains(loc.getBlock().getBiome()))
                        continue;

                    //Try to find a surface to land on
                    for(int i = max - 150;i > 0;i--)
                    {
                        loc.setY(i);
                        if(!allowedLandingBlocks.contains(loc.getBlock().getType()))
                            continue;

                        Location checkLoc = loc.clone();
                        boolean isOk = true;
                        for(;checkLoc.getY() < loc.getY() + 3;checkLoc.setY(checkLoc.getY() + 1));
                            if(!checkLoc.getBlock().getType().equals(Material.AIR))
                                    isOk = false;
                        if(!isOk)
                            continue;
                            
                        int safeNum = 0;
                        for(double x = -safeZoneRadius;x<=safeZoneRadius;x++)
                        {
                            for(double z = -safeZoneRadius; z<=safeZoneRadius; z++)
                            {
                                for(double y = 0; y <= safeZoneHeight; y++)
                                {
                                    checkLoc.setX(loc.getX() + x);
                                    checkLoc.setY(loc.getY() + y);
                                    checkLoc.setZ(loc.getZ() + z);
                                    if(checkLoc.getBlock().getType().equals(Material.AIR))
                                        safeNum++;
                                }
                            }
                        }
                        if(safeNum < 0.75 * safeZoneRadius * safeZoneRadius * safeZoneHeight)
                            continue;
                        else
                        {
                            locationDone = true;
                            break;
                        }
                        
                    }
                    log.info(CatBot.cPrefix + "Successfully RTPing " + name + " in world " + loc.getWorld().getName() + 
                            " at position " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " (x,y,z).");
                    loc.setY(loc.getY() + 2.0);
                    p.teleport(loc);
                }
            }
	    });
		return true;
	}

}
