package com.elan_oots.invswap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InvSwap extends JavaPlugin
{
	public YamlConfiguration config;
	public YamlConfiguration defaultconfig;
	
	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
		
		File userconfig = new File(this.getDataFolder(), "config.yml");
		if(!userconfig.exists())
		{
			try
			{
				this.getLogger().log(Level.INFO, "No configuration file exists, creating one now");
				userconfig.createNewFile();
				defaultconfig.save(userconfig);
			}
			catch(IOException e)
			{
				this.getLogger().log(Level.WARNING, "Could not create config file, using default");
				config = defaultconfig;
			}
		}
		config = YamlConfiguration.loadConfiguration(userconfig);
		this.getLogger().log(Level.CONFIG, "Max allowed inventories: " + config.getInt("maxinventories"));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			
			File playerfile = new File(new File(this.getDataFolder(), "inventories"), player.getUniqueId().toString());
			File publicsaves = new File(this.getDataFolder(), "public");
			
			if (args.length > 0) {
			    if (args[0].equalsIgnoreCase("save")) {
			        if (args.length > 1) {
			            return saveInventory(args[1], player, playerfile);
			        }
			    }
			    if (args[0].equalsIgnoreCase("load")) {
			        if (args.length > 1) {
			            return loadInventory(args[1], player, playerfile);
			        }
			    }
			    if (args[0].equalsIgnoreCase("remove")) {
			        if (args.length > 1) {
			            return removeInventory(args[1], player, playerfile);
			        }
			    }
			    if (args[0].equalsIgnoreCase("list")) {
			        return listInventories(player, playerfile);
			    }
			    if (args[0].equalsIgnoreCase("public")) {
			        if (args.length > 1) {
			            if (args[1].equalsIgnoreCase("list")) {
			                return publicList(player, publicsaves);
			            }
			            if (args[1].equalsIgnoreCase("load")) {
			                if (args.length > 2) {
			                    return publicLoad(args[2], player, publicsaves);
			                }
			            }
			            if (args[1].equalsIgnoreCase("save")) {
			                if (args.length > 2) {
			                    return publicSave(args[2], player, publicsaves);
			                }
			            }
			            if (args[1].equalsIgnoreCase("remove")) {
			                if (args.length > 2) {
			                    return publicRemove(args[2], player, publicsaves);
			                }
			            }
			        }
			        return publicHelp(player);
			    }
			}
			return helpMessage(player);
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Only players can use that command");
			return true;
		}
	}
	
	public boolean saveInventory(String invname, Player player, File playerfile)
	{
		if(!player.hasPermission("invswap.save"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that");
			return true;
		}
		
		if (!invname.matches("[A-Za-z0-9_]+")) {
		    player.sendMessage(ChatColor.RED + "Your inventory name must contain only letters, numbers, and underscores.");
		    return true;
		}
		
		if(!playerfile.exists())
		{
			playerfile.mkdirs();
		}
		
		Inventory currentinv = player.getInventory();
			
		YamlConfiguration inv = InvIO.invToConfig(currentinv);
		
		File invsave = new File(playerfile, invname);
		
		File[] saves = playerfile.listFiles();
		
		if(saves.length >= config.getInt("maxinventories"))
		{
			if(!player.hasPermission("invswap.unlimited"))
			{
				player.sendMessage(ChatColor.YELLOW + "The maximum amount of available inventories has been reached.");
				return true;
			}
		}
		
		if(invsave.exists())
		{
			player.sendMessage(ChatColor.YELLOW + "Overwriting previous save");
		}
		
		try
		{
			inv.save(invsave);
			player.sendMessage(ChatColor.GREEN + "Saved inventory as " + invname);
		}
		catch(IOException e)
		{
			player.sendMessage(ChatColor.RED + "An error occurred while attempting to save your inventory.");
			this.getLogger().log(Level.WARNING, "Player " + player.getName() + " could not save inventory (" + e.getClass().getCanonicalName() + ")");
		}
		return true;
	}
	
	public boolean loadInventory(String invname, Player player, File playerfile)
	{
		if(!player.hasPermission("invswap.load"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that");
			return true;
		}
		
		if (!invname.matches("[A-Za-z0-9_]+")) {
            player.sendMessage(ChatColor.RED + "Inventory could not be found. Use /invswap list to see your inventories.");
            return true;
        }
		
		File invfile = new File(playerfile, invname);
		if(invfile.exists())
		{
			player.getInventory().setContents(InvIO.fileToInventory(invfile).toArray(new ItemStack[0]));
			player.sendMessage(ChatColor.GREEN + "Loaded inventory " + invname);
			return true;
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Inventory could not be found. Use /invswap list to see your inventories.");
			return true;
		}
	}
	
	public boolean removeInventory(String invname, Player player, File playerfile)
	{
		if(!player.hasPermission("invswap.save"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that");
			return true;
		}
		
		if (!invname.matches("[A-Za-z0-9_]+")) {
            player.sendMessage(ChatColor.RED + "Could not find inventory specified");
            return true;
        }
		
		File todelete = new File(playerfile, invname);
		if(todelete.exists())
		{
			todelete.delete();
			player.sendMessage(ChatColor.GREEN + "Inventory deleted");
			return true;
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Could not find inventory specified");
			return true;
		}
	}
	
	public boolean publicList(Player player, File publicsaves)
	{
		File[] invs = publicsaves.listFiles();
		
		List<String> strings = new ArrayList<String>();
		
		for(File f : invs)
		{
			strings.add(ChatColor.GREEN + f.getName());
		}
		player.sendMessage(ChatColor.GRAY + "All public inventories:");
		for(String string : strings)
		{
			player.sendMessage(string);
		}
		return true;
	}
	
	public boolean publicHelp(Player player)
	{
		Scanner helpscan = new Scanner(this.getClass().getClassLoader().getResourceAsStream("publichelp.txt"));
		while(helpscan.hasNext())
		{
			player.sendMessage(helpscan.nextLine());
		}
		helpscan.close();
		
		return true;
	}
	
	public boolean listInventories(Player player, File playerfile)
	{
		if(!playerfile.exists())
		{
			player.sendMessage(ChatColor.YELLOW + "No inventories have been saved");
			return true;
		}
		File[] invs = playerfile.listFiles();
		
		List<String> strings = new ArrayList<String>();
		
		for(File f : invs)
		{
			strings.add(ChatColor.GREEN + f.getName());
		}
		player.sendMessage(ChatColor.GRAY + "" + invs.length + " Inventories (Out of " + config.getInt("maxinventories") + " allowed)");
		player.sendMessage(ChatColor.GRAY + "All of your inventories:");
		for(String string : strings)
		{
			player.sendMessage(string);
		}
		return true;
	}
	
	public boolean removeInventories(Player player, File playerfile)
	{
		if(!player.hasPermission("invswap.save"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that");
			return true;
		}
		if(!playerfile.exists())
		{
			player.sendMessage(ChatColor.YELLOW + "No inventories have been saved");
			return true;
		}
		File[] files = playerfile.listFiles();
		for(File file : files)
		{
			file.delete();
		}
		player.sendMessage(ChatColor.GREEN + "All inventories removed");
		return true;
	}
	
	public boolean helpMessage(Player player)
	{
		Scanner helpscan = new Scanner(this.getClass().getClassLoader().getResourceAsStream("help.txt"));
		while(helpscan.hasNext())
		{
			player.sendMessage(helpscan.nextLine());
		}
		helpscan.close();
		return true;
	}
	
	public boolean publicSave(String invname, Player player, File publicsaves)
	{
		if(!player.hasPermission("invswap.publish"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that.");
			return true;
		}
		
		if (!invname.matches("[A-Za-z0-9_]+")) {
            player.sendMessage(ChatColor.RED + "Your inventory name must contain only letters, numbers, and underscores.");
            return true;
        }
		
		YamlConfiguration saveinv = InvIO.invToConfig(player.getInventory());
		String name = invname;
		File savefile = new File(publicsaves, name);
		try
		{
			saveinv.save(savefile);
			player.sendMessage(ChatColor.GREEN + "Inventory added to public inventories");
			return true;
		}
		catch(IOException e)
		{
			player.sendMessage(ChatColor.RED + "An error occurred, and the inventory could not be saved.");
			return true;
		}
	}
	
	public boolean publicRemove(String invname, Player player, File publicsaves)
	{
		if(!player.hasPermission("invswap.publish"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that.");
			return true;
		}
		
		if (!invname.matches("[A-Za-z0-9_]+")) {
            player.sendMessage(ChatColor.RED + "Could not find inventory specified");
            return true;
        }
		
		File delfile = new File(publicsaves, invname);
		delfile.delete();
		player.sendMessage(ChatColor.GREEN + "Public save deleted");
		return true;
	}
	
	public boolean publicLoad(String invname, Player player, File publicsaves)
	{
		if(!player.hasPermission("invswap.load"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to do that");
			return true;
		}
		
		if (!invname.matches("[A-Za-z0-9_]+")) {
            player.sendMessage(ChatColor.RED + "Inventory could not be found. Use /invswap public list to see public inventories.");
            return true;
        }
		
		File invfile = new File(publicsaves, invname);
		if(invfile.exists())
		{
			player.getInventory().setContents(InvIO.fileToInventory(invfile).toArray(new ItemStack[0]));
			player.sendMessage(ChatColor.GREEN + "Loaded inventory " + invname);
			return true;
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Inventory could not be found. Use /invswap public list to see public inventories.");
			return true;
		}
	}
	
}
