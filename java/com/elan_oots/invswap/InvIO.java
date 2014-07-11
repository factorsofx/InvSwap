package com.elan_oots.invswap;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InvIO
{
	public static YamlConfiguration invToConfig(Inventory inv)
	{
		YamlConfiguration config = new YamlConfiguration();
		
		config.set("inventory", inv.getContents());
		
		return config;
	}
	
	@SuppressWarnings("unchecked")
	public static List<ItemStack> fileToInventory(File invfile, boolean compressed)
	{
		YamlConfiguration config = null;
		
		config = YamlConfiguration.loadConfiguration(invfile);
		
		return (List<ItemStack>) config.get("inventory");
	}
}
