package com.williameze.minegicka3.main.entities.magic;

import java.awt.Color;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class EntitySprayWater extends EntitySpray
{
    public static Color color1 = new Color(20, 50, 247, 220);
    public static Color color2 = new Color(29, 69, 240, 210);

    public EntitySprayWater(World par1World)
    {
	super(par1World);
	color = rand.nextInt(2) == 0 ? color1 : color2;
    }

    @Override
    public void affectBlock(int x, int y, int z)
    {
	if (worldObj.isRemote) return;
	if (worldObj.getBlock(x, y, z).getMaterial() == Material.fire)
	{
	    worldObj.setBlockToAir(x, y, z);
	    setDead();
	}
	if (worldObj.getBlock(x, y, z) == Blocks.lava)
	{
	    worldObj.setBlock(x, y, z, Blocks.cobblestone);
	    setDead();
	}
    }

    @Override
    public void affectEntity(Entity e)
    {
	super.affectEntity(e);
	double power = 0.3 / e.width / e.width / e.height;
	e.motionX += motionX * power;
	e.motionY += motionY * power;
	e.motionZ += motionZ * power;
    }
}
