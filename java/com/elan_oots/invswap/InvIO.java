package com.elan_oots.invswap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InvIO
{
	public static String invToString(Inventory inv)
	{
		StringBuilder string = new StringBuilder(inv.getSize() * 2);
		for(ItemStack i : inv.getContents())
		{
			if(i != null)
			{
				string.append(i.getAmount());
				string.append(";");
				string.append(i.getType().toString());
				string.append(";");
				string.append(i.getDurability());
				string.append("@");
			}
		}
		
		return string.toString();
	}
	
	public static Inventory stringToInv(String string)
	{
		Inventory inv = Bukkit.createInventory(null, InventoryType.PLAYER);
		
		String[] entries = string.split("@");
		for(String s : entries)
		{
			String[] entry = s.split(";");
			ItemStack i = new ItemStack(Material.getMaterial(entry[1]), Integer.parseInt(entry[0]));
			i.setDurability(Short.parseShort(entry[2]));
			inv.addItem(i);
		}
		
		return inv;
	}
}
