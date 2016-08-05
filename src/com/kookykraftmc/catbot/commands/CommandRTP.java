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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.kookykraftmc.catbot.CatBot;

public class CommandRTP implements CommandExecutor
{
    private static final Logger log = CatBot.log;
    private static final Random rdm = new Random(System.nanoTime());
    private static final HashSet<Biome> biomeBlacklist = new HashSet<Biome>();
    private static final HashSet<Material> allowedLandingBlocks = new HashSet<Material>();
    private static final HashSet<String> allowedWorlds = new HashSet<String>();
    private static final String CMD_NAME = "rtp";

    //Assigning default values in case none are defined
	private static double BORDER = 10000;
	private static int MAX_USES = 3;
	private static int SAFE_ZONE_RADIUS = 2;
	private static int SAFE_ZONE_HEIGHT = 4;
	private static int MAX_TRIES = 50;

	static CatBot plugin;

	public CommandRTP(CatBot cb)
	{
		plugin = cb;
		loadCfg();
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
	
	/**
	 * TP player to random location in their world
	 * @param p Player to RTP
	 * @return success?
	 */
	private boolean rtp(final Player p)
	{
	    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run()
            {
                final World w = p.getLocation().getWorld();
                if(!allowedWorlds.contains(w.getName()))
                {
                    p.sendMessage(CatBot.prefix + "You cannot RTP in this world.");
                    return;
                }
                final int max = w.getMaxHeight();
                final String name = p.getName();
                boolean locationDone = false;

                int counter = 0;
                while (!locationDone)
                {
                    if(++counter > MAX_TRIES)
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
                        for(double x = -SAFE_ZONE_RADIUS;x<=SAFE_ZONE_RADIUS;x++)
                        {
                            for(double z = -SAFE_ZONE_RADIUS; z<=SAFE_ZONE_RADIUS; z++)
                            {
                                for(double y = 0; y <= SAFE_ZONE_HEIGHT; y++)
                                {
                                    checkLoc.setX(loc.getX() + x);
                                    checkLoc.setY(loc.getY() + y);
                                    checkLoc.setZ(loc.getZ() + z);
                                    if(checkLoc.getBlock().getType().equals(Material.AIR))
                                        safeNum++;
                                }
                            }
                        }
                        if(safeNum < 0.75 * SAFE_ZONE_RADIUS * SAFE_ZONE_RADIUS * SAFE_ZONE_HEIGHT)
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
	
	public static void loadCfg()
	{
	    FileConfiguration cfg = plugin.getConfig();
	    BORDER = cfg.getDouble("RTP.Border");
	    MAX_USES = cfg.getInt("RTP.MaxUses");
	    SAFE_ZONE_RADIUS = cfg.getInt("RTP.SafeZoneRadius");
	    SAFE_ZONE_HEIGHT = cfg.getInt("RTP.SafeZoneHeight");
	    MAX_TRIES = cfg.getInt("RTP.MaxTries");
	    //Add biomes to blacklist
	    for(String biomeName:cfg.getStringList("RTP.BiomeBlacklist"))
	    {
	        Biome biome = Biome.valueOf(biomeName);
	        if(biome == null)
	        {
	            log.warning(CatBot.cPrefix + "Unknown biome declared in config: " + biomeName + ". Ignoring.");
	            continue;
	        }
	        biomeBlacklist.add(biome);
	    }
	    if(biomeBlacklist.isEmpty())
	        log.warning(CatBot.cPrefix + "No biomes have been blacklisted. No valid biomes were declared in the config.");
	    
	    //Do the same with landing blocks
        for(String blockName:cfg.getStringList("RTP.LandingBlocks"))
        {
            Material mat = Material.getMaterial(blockName);
            if(mat == null)
            {
                log.warning(CatBot.cPrefix + "Unknown block declared in config: " + blockName + ". Ignoring.");
                continue;
            }
            allowedLandingBlocks.add(mat);
        }
        if(allowedLandingBlocks.isEmpty())
        {
            log.severe(CatBot.cPrefix + "No landing blocks have been found, no valid blocks were defined in config. Defaulting to grass blocks.");
            allowedLandingBlocks.add(Material.GRASS);
        }
        
        //And Worlds
        for(String worldName:cfg.getStringList("RTP.Worlds"))
        {
            if(Bukkit.getWorld(worldName) == null)
            {
                log.warning(CatBot.cPrefix + "Unknown world declared in config: " + worldName + ". Ignoring.");
                continue;
            }
            allowedWorlds.add(worldName);
        }
        if(allowedWorlds.isEmpty())
        {
            log.severe(CatBot.cPrefix + "No allowed worlds have been found, no valid worlds were defined in config. Defaulting to world.");
            allowedWorlds.add("world");
        }
	}

}
