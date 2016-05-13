package com.kookykraftmc.catbot.commands;

import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

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
	static final Logger log = CatBot.log;
	static final Random rdm = new Random(System.nanoTime());
	static final HashSet<Biome> biomeBlacklist = new HashSet<Biome>();
	static final HashSet<UUID> usedList = new HashSet<UUID>();
	static final HashSet<Material> allowedLandingBlocks = new HashSet<Material>();
	static final int safeZoneRadius = 2;
	static final int safeZoneHeight = 4;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
        if(!(sender instanceof Player) && args.length == 0)
        {
            sender.sendMessage(CatBot.cPrefix + "The console cannot RTP. You can use /rtp (player)");
            return true;
        }
        Player p = (Player) sender;

		if(args.length == 0)
		{
	        if(usedList.contains(p.getUniqueId()) && !p.hasPermission("catbot.rtpinfinite"))
            {
                p.sendMessage(CatBot.prefix + "You cannot use RTP again until the next restart.");
                return true;
            }
	        usedList.add(p.getUniqueId());
	        World w = p.getLocation().getWorld();
	        int max = w.getMaxHeight();
	        double border = w.getWorldBorder().getSize();
	        boolean locationDone = false;
	        
	        int counter = 0;
	        while (!locationDone)
	        {
	            if(++counter > 50)
	            {
	                sender.sendMessage(CatBot.prefix + "Couldn't find a safe place to teleport to :( please contact staff, preferably Kraise.");
	                return true;
	            }
	            Location loc = new Location(w, (rdm.nextDouble() - 0.5 ) * 2 * border, max, (rdm.nextDouble() - 0.5 ) * 2 * border);
	            if(biomeBlacklist.contains(loc.getBlock().getBiome()))
	                continue;
	            //Try to find a surface to land on
	            for(int i = max - 1;i > 0;i--)
	            {
	                loc.setY(i);
	                if(!allowedLandingBlocks.contains(loc.getBlock().getType()))
	                    continue;
	    
	                Location checkLoc = loc.clone();
	                boolean isOk = true;
	                for(;checkLoc.getY() < loc.getY() + 3;checkLoc.setY(checkLoc.getY() + 1));
	                    if(checkLoc.getBlock().getType().equals(Material.AIR))
	                            isOk = false;
	                if(!isOk)
	                    continue;
	                    
	                int safeNum = 0;
	                for(double x = -safeZoneRadius;x <=safeZoneRadius;x++)
	                {
	                    for(double z = -safeZoneRadius; z <= safeZoneRadius; z++)
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
	                if(safeNum < 0.5 * safeZoneRadius * safeZoneRadius * safeZoneHeight)
	                    continue;
	                
	                p.sendMessage(CatBot.prefix + "Teleporting now. Please wait ");////////////////////////
	                p.teleport(loc);
	            }
	        }
		}
		return false;
	}

}
