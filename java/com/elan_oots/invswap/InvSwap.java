package com.elan_oots.invswap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Color;
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
			
			if(args.length == 2)
			{
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
						sender.sendMessage(Color.RED + "Error, inventory is null");
						return true;
					}
					
					String inv = InvIO.invToString(currentinv);
					
					File invsave = new File(playerfile, invname);
					
					if(invsave.exists())
					{
						sender.sendMessage(Color.YELLOW + "Overwriting previous save");
					}
					
					try
					{
						PrintWriter writer = new PrintWriter(invsave);
						writer.write(inv);
						writer.close();
					}
					catch(FileNotFoundException e)
					{
						sender.sendMessage(Color.RED + "An error occurred while attempting to save your inventory.");
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
							
							Inventory inventory = InvIO.stringToInv(invstring);
							Inventory playerinv = player.getInventory();
							
							playerinv.clear();
							
							for(ItemStack item : inventory)
							{
								if(item != null)
								{
									playerinv.addItem(item);
								}
							}
						}
						catch(FileNotFoundException e)
						{
							player.sendMessage(Color.RED + "An error occurred while attempting to load your inventory.");
							this.getServer().getLogger().log(Level.WARNING, "Player " + player.getName() + " could not load inventory.");
						}
					}
					else
					{
						player.sendMessage(Color.RED + "Inventory could not be found. Use /invswap list to see your inventories.");
					}
					break;
				}
				if(args.length == 1)
				{
					switch(args[0])
					{
					case "list":
						File[] invs = playerfile.listFiles();
						
						StringBuilder list = new StringBuilder();
						
						for(File f : invs)
						{
							list.append(ChatColor.GREEN + f.getName());
						}
						return true;
					}
				}
			}
		}
		else
		{
			sender.sendMessage(Color.RED + "Only players can use that command");
			return true;
		}
		return false;
	}
}
