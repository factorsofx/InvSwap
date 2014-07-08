package com.elan_oots.invswap;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InvIO
{
	public static String invToString(Inventory inv)
	{
		Gson gson = new Gson();
		
		List<JSONItemStack> items = new ArrayList<JSONItemStack>();
		
		for(int i = 0; i < inv.getSize(); i++)
		{
			if(inv.getItem(i) != null)
			{
				items.add(new JSONItemStack(inv.getItem(i), i));
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for(JSONItemStack item : items)
		{
			sb.append(gson.toJson(item) + ";");
		}
		
		return sb.toString();
	}
	
	public static List<JSONItemStack> stringToItemList(String string)
	{
		List<JSONItemStack> inv = new ArrayList<JSONItemStack>();
		
		Gson gson = new Gson();
		
		String[] items = string.split(";");
		
		for(String itemstr : items)
		{
			JSONItemStack item = gson.fromJson(itemstr, JSONItemStack.class);
			inv.add(item);
		}
		
		return inv;
	}
	
	public static class JSONItemStack
	{
		public int amount;
		public String material;
		public short damage;
		public int position;
		
		public JSONItemStack(ItemStack item, int position)
		{
			if(item != null)
			{
				this.amount = item.getAmount();
				this.material = item.getType().toString();
				this.damage = item.getDurability();
				this.position = position;
			}
			else
			{
				this.amount = -1;
				this.material = "null";
				this.damage = 0;
				this.position = position;
			}
		}
	}
}
