package com.williameze.minegicka3.main.magicks;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class MagickFreezeMotion extends Magick
{

    public MagickFreezeMotion()
    {
	super("Freeze Motion", "ID");
    }

    @Override
    public Object[] getAdditionalCraftClickTabletMaterials()
    {
	return new Object[] { Blocks.ice, 4 };
    }

    @Override
    public double getBaseManaCost()
    {
	return 100;
    }

    @Override
    public void doTheMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	double[] props = getStaffMainProperties(additionalData);
	double radius = 16 * Math.min(props[0], 10);
	List<Entity> l = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(radius, radius * 2, radius));
	l.remove(caster);
	if (!l.isEmpty())
	{
	    for (int a = 0; a < l.size(); a++)
	    {
		Entity e = l.get(a);
		e.motionX = e.motionY = e.motionZ = 0;
		if (e instanceof EntityLivingBase)
		{
		    ((EntityLivingBase) e).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 60, 3));
		}
	    }
	}
    }

}
