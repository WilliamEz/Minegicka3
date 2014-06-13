package com.williameze.minegicka3.main.entities.magic;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.williameze.minegicka3.main.spells.Spell;

public class EntitySprayFire extends EntitySpray
{
    public static Color color1 = new Color(255, 60, 0, 250);
    public static Color color2 = new Color(255, 160, 0, 235);
    public static Color color3 = new Color(255, 110, 0, 220);

    public EntitySprayFire(World par1World)
    {
	super(par1World);
	int a = rand.nextInt(3);
	if (a == 0) color = color1;
	else if (a == 1) color = color2;
	else color = color3;
    }

    @Override
    public void setSpell(Spell s)
    {
	super.setSpell(s);
	gravity = -0.01 / getSpell().countElements() / getSpell().countElements();
    }

    @Override
    public void affectBlock(int x, int y, int z)
    {
	if (worldObj.isRemote) return;
	if (Blocks.fire.canPlaceBlockAt(worldObj, x, y + 1, z))
	{
	    if (worldObj.isAirBlock(x, y + 1, z))
	    {
		worldObj.setBlock(x, y + 1, z, Blocks.fire);
		setDead();
	    }
	}
	Block b = worldObj.getBlock(x, y, z);
	if (b == Blocks.snow || b == Blocks.ice || b == Blocks.snow_layer)
	{
	    worldObj.setBlockToAir(x, y, z);
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
	    EntitySpraySteam steam = new EntitySpraySteam(worldObj);
	    steam.posX = (posX + e.posX) / 2;
	    steam.posY = (posX + e.posX) / 2;
	    steam.posZ = (posX + e.posX) / 2;
	    steam.setPosition(steam.posX, steam.posY, steam.posZ);
	    steam.setSpell(s);
	    steam.motionX = (motionX + e.motionX) / 2;
	    steam.motionY = (motionY + e.motionY) / 2;
	    steam.motionZ = (motionZ + e.motionZ) / 2;
	    steam.setVelocity(steam.motionX, steam.motionY, steam.motionZ);
	    worldObj.spawnEntityInWorld(steam);
	}
	if (!e.isDead && e instanceof EntitySprayCold)
	{
	    e.setDead();
	    setDead();
	}
    }
}
