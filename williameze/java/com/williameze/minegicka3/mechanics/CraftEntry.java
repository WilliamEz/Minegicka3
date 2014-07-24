package com.williameze.minegicka3.mechanics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

public class CraftEntry
{
    public static int nextID = 0;
    
    public int id;
    public ItemStack output;
    public List<Entry<ItemStack, Integer>> input = new ArrayList();
    
    public CraftEntry(ItemStack out, List<Entry<ItemStack, Integer>> in)
    {
	id = nextID++;
	output = out;
	input.addAll(in);
    }
}
