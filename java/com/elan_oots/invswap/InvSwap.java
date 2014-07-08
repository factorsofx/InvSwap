package com.elan_oots.invswap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InvSwap extends JavaPlugin
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			
			File playerfile = new File(this.getDataFolder(), player.getUniqueId().toString());
			
			switch(args.length)
			{
				case 2:
				switch(args[0])
				{
				case "save":
					if(!playerfile.exists())
					{
						playerfile.mkdirs();
					}
					
					Inventory currentinv = player.getInventory();
					String invname = args[1];
					
					if(currentinv == null)
					{
						sender.sendMessage(ChatColor.RED + "Error, inventory is null");
						return true;
					}
						
					String inv = InvIO.invToString(currentinv);
					
					File invsave = new File(playerfile, invname);
					
					if(invsave.exists())
					{
						sender.sendMessage(ChatColor.YELLOW + "Overwriting previous save");
					}
					
					try
					{
						PrintWriter writer = new PrintWriter(invsave);
						writer.write(inv);
						writer.close();
					}
					catch(FileNotFoundException e)
					{
						sender.sendMessage(ChatColor.RED + "An error occurred while attempting to save your inventory.");
						this.getServer().getLogger().log(Level.WARNING, "Player " + player.getName() + " could not save inventory.");
					}
					break;
				case "load":
					File invfile = new File(playerfile, args[1]);
					if(invfile.exists())
					{
						try
						{
							Scanner reader = new Scanner(invfile);
							String invstring = reader.nextLine();
							
							reader.close();
							
							Inventory playerinv = player.getInventory();
							List<InvIO.JSONItemStack> inventory = InvIO.stringToItemList(invstring);
							
							playerinv.clear();
							
							for(InvIO.JSONItemStack item : inventory)
							{
								ItemStack itemstack = new ItemStack(Material.getMaterial(item.material), item.amount);
								itemstack.setDurability(item.damage);
								playerinv.setItem(item.position, itemstack);
							}
						}
						catch(FileNotFoundException e)
						{
							player.sendMessage(ChatColor.RED + "An error occurred while attempting to load your inventory.");
							this.getServer().getLogger().log(Level.WARNING, "Player " + player.getName() + " could not load inventory.");
						}
					}
					else
					{
						player.sendMessage(ChatColor.RED + "Inventory could not be found. Use /invswap list to see your inventories.");
					}
					break;
				}
				case 1:
				switch(args[0])
				{
				case "list":
					File[] invs = playerfile.listFiles();
					
					List<String> strings = new ArrayList<String>();
					
					for(File f : invs)
					{
						strings.add(ChatColor.GREEN + f.getName());
					}
					player.sendMessage(ChatColor.GRAY + "All of your inventories:");
					for(String string : strings)
					{
						player.sendMessage(string);
					}
					return true;
				}
			}
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Only players can use that command");
			return true;
		}
		return false;
	}
}
