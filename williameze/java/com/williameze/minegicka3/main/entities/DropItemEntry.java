package com.williameze.minegicka3.main.entities;

import java.util.Random;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;

import net.minecraft.entity.item.EntityItem;
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
		EntityItem entity = new EntityItem(world, x, y, z, is);
		Vector v = new Vector(velocity, velocity, 0);
		v = v.rotateAround(Vector.unitY, rnd.nextDouble() * 2 * Math.PI);
		entity.motionX = v.x;
		entity.motionY = v.y;
		entity.motionZ = v.z;
		entity.noClip = true;
		entity.moveEntity(v.x, v.y, v.z);
		entity.noClip = false;
		if (entity.ticksExisted == 0) entity.ticksExisted = 1;
		world.spawnEntityInWorld(entity);
	    }
	}
    }
}
