package com.williameze.minegicka3.main.magicks;

import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.entities.EntityEarthRumble;
import com.williameze.minegicka3.main.entities.EntityLightning;
import com.williameze.minegicka3.main.entities.EntityMine;
import com.williameze.minegicka3.main.entities.EntityStorm;
import com.williameze.minegicka3.main.entities.EntityVortex;
import com.williameze.minegicka3.main.entities.IEntityNullifiable;
import com.williameze.minegicka3.main.objects.BlockShield;
import com.williameze.minegicka3.main.objects.BlockWall;

public class MagickNullify extends Magick
{

    public MagickNullify()
    {
	super("Nullify", "AD");
    }

    @Override
    public Object[] getAdditionalCraftClickTabletMaterials()
    {
	return new Object[] { ModBase.thingy, 4 };
    }

    @Override
    public double getBaseManaCost()
    {
	return 150;
    }

    @Override
    public void doTheMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	double[] props = getStaffMainProperties(additionalData);
	double radius = 12 * Math.min(props[0], 8);
	List<Entity> l = world.getEntitiesWithinAABB(Entity.class,
		AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(radius, radius, radius));
	if (!l.isEmpty())
	{
	    for (int a = 0; a < l.size(); a++)
	    {
		Entity e = l.get(a);
		if (e instanceof IEntityNullifiable)
		{
		    e.setDead();
		}
		if (e instanceof EntityLivingBase)
		{
		    ((EntityLivingBase) e).clearActivePotions();
		}
	    }
	}
	int minX = (int) Math.floor(x - radius);
	int minY = (int) Math.floor(y - radius);
	int minZ = (int) Math.floor(z - radius);
	int maxX = (int) Math.ceil(x + radius);
	int maxY = (int) Math.ceil(y + radius);
	int maxZ = (int) Math.ceil(z + radius);
	for (int x1 = minX; x1 <= maxX; x1++)
	{
	    for (int y1 = minY; y1 <= maxY; y1++)
	    {
		for (int z1 = minZ; z1 <= maxZ; z1++)
		{
		    Block b = world.getBlock(x1, y1, z1);
		    if (b instanceof BlockWall || b instanceof BlockShield)
		    {
			world.setBlockToAir(x1, y1, z1);
		    }
		}
	    }
	}
    }

}
