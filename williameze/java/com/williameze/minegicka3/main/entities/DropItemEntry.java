package com.williameze.minegicka3.main.entities;

import java.util.Random;

import com.williameze.api.lib.FuncHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DropItemEntry
{
    public static Random rnd = new Random();
    public ItemStack is;
    public int count;
    public double chanceEach;

    public DropItemEntry(ItemStack is, int count, double chance)
    {
	this.is = is;
	this.count = count;
	chanceEach = chance;
    }

    public void spawn(World world, double x, double y, double z, double velocity)
    {
	for (int a = 0; a < count; a++)
	{
	    if (rnd.nextDouble() < chanceEach)
	    {
		FuncHelper.spawnItem(world, is.copy(), x, y, z, velocity);
	    }
	}
    }
}
