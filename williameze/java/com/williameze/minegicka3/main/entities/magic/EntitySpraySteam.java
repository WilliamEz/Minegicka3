package com.williameze.minegicka3.main.entities.magic;

import java.awt.Color;

import com.williameze.minegicka3.mechanics.spells.Spell;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntitySpraySteam extends EntitySpray
{
    public static Color color1 = new Color(151, 151, 151, 200);
    public static Color color2 = new Color(120, 120, 120, 170);

    public EntitySpraySteam(World par1World)
    {
	super(par1World);
	gravity = -0.03;
	color = rand.nextInt(2) == 0 ? color1 : color2;
	setSize(0.1F, 0.1F);
    }

    @Override
    public void setSpell(Spell s)
    {
	super.setSpell(s);
	gravity = -0.03 / getSpell().countElements() / getSpell().countElements();
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
    }

    @Override
    public void affectEntity(Entity e)
    {
	super.affectEntity(e);
	if (motionY > 0)
	{
	    e.fallDistance = (float) Math.max(fallDistance - motionY, 0);
	    e.motionY -= motionY / 3;
	}
    }

}
