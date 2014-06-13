package com.williameze.minegicka3.main.magicks;

import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.entities.IEntityNullifiable;
import com.williameze.minegicka3.main.entities.magic.EntityEarthRumble;
import com.williameze.minegicka3.main.entities.magic.EntityLightning;
import com.williameze.minegicka3.main.entities.magic.EntityMine;
import com.williameze.minegicka3.main.entities.magic.EntityStorm;
import com.williameze.minegicka3.main.entities.magic.EntityVortex;
import com.williameze.minegicka3.main.objects.blocks.BlockShield;
import com.williameze.minegicka3.main.objects.blocks.BlockWall;

public class MagickDepotion extends Magick
{

    public MagickDepotion()
    {
	super("De-potion", "LD");
    }

    @Override
    public Object[] getAdditionalCraftClickTabletMaterials()
    {
	return new Object[] { Items.glass_bottle };
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
	List<EntityLivingBase> l = world.getEntitiesWithinAABB(EntityLivingBase.class,
		AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(radius, radius * 2, radius));
	if (!l.isEmpty())
	{
	    for (int a = 0; a < l.size(); a++)
	    {
		l.get(a).clearActivePotions();
	    }
	}
    }

}
