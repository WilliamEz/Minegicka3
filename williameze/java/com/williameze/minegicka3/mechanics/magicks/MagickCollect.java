package com.williameze.minegicka3.mechanics.magicks;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.minegicka3.ModBase;

public class MagickCollect extends Magick
{

    public MagickCollect()
    {
	super("Collect", "SEES");
    }

    @Override
    public List<String> getDescription()
    {
	return Arrays.asList(new String[] { "Collects nearby items and xp-orbs.", "Collect range: 6 x staff's power(block)", });
    }

    @Override
    public Object[] getAdditionalUnlockMaterials()
    {
	return new Object[] { Items.bucket, Items.fishing_rod, Items.lead };
    }

    @Override
    public double getBaseManaCost()
    {
	return 100;
    }

    @Override
    public void doTheMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	if (!world.isRemote)
	{
	    double[] props = getStaffMainProperties(additionalData);
	    double radius = 6 * props[0];
	    List<Entity> l = world.selectEntitiesWithinAABB(Entity.class,
		    AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(radius, radius, radius), new ESelectorItemAndXP());
	    l.remove(caster);
	    if (!l.isEmpty())
	    {
		for (int a = 0; a < l.size(); a++)
		{
		    l.get(a).setPosition(x, y, z);
		}
	    }
	}
    }
}
