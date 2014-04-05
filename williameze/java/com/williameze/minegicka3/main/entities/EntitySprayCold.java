package com.williameze.minegicka3.main.entities;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.williameze.minegicka3.main.spells.Spell;

public class EntitySprayCold extends EntitySpray
{
    public static Color color1 = new Color(255, 255, 255, 230);
    public static Color color2 = new Color(255, 255, 255, 255);

    public EntitySprayCold(World par1World)
    {
	super(par1World);
	color = rand.nextInt(2) == 0 ? color1 : color2;
	setSize(0.09F, 0.09F);
    }

    @Override
    public void affectBlock(int x, int y, int z)
    {
	if (worldObj.isRemote) return;
	Block b = worldObj.getBlock(x, y, z);
	if (b == Blocks.water)
	{
	    worldObj.setBlock(x, y, z, Blocks.ice);
	    setDead();
	}
	else if (worldObj.isAirBlock(x, y, z) && Blocks.snow_layer.canPlaceBlockAt(worldObj, x, y, z))
	{
	    worldObj.setBlock(x, y, z, Blocks.snow_layer);
	    setDead();
	}
	else if (worldObj.isAirBlock(x, y + 1, z) && Blocks.snow_layer.canPlaceBlockAt(worldObj, x, y + 1, z))
	{
	    worldObj.setBlock(x, y + 1, z, Blocks.snow_layer);
	    setDead();
	}
    }

    @Override
    public void collideWithSpray(EntitySpray e)
    {
	if (!worldObj.isRemote && !e.isDead && e instanceof EntitySprayWater)
	{
	    e.setDead();
	    setDead();
	    boolean thisOrThat = rand.nextBoolean();
	    Spell s = thisOrThat ? getSpell() : e.getSpell();
	    // Spawn ice
	}
    }
}
