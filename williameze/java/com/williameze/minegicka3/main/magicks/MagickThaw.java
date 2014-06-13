package com.williameze.minegicka3.main.magicks;

import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.material.Material;
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

public class MagickThaw extends Magick
{

    public MagickThaw()
    {
	super("Thaw", "FD");
    }

    @Override
    public Object[] getAdditionalCraftClickTabletMaterials()
    {
	return new Object[] { Items.flint_and_steel };
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
		    if (b == Blocks.snow_layer || b == Blocks.snow)
		    {
			world.setBlockToAir(x1, y1, z1);
		    }
		    if (b == Blocks.ice)
		    {
			boolean set = false;
			for (int x2 = x1 - 1; x2 <= x1 + 1 && !set; x2++)
			{
			    for (int y2 = y1 - 1; y2 <= y1 + 1 && !set; y2++)
			    {
				for (int z2 = z1 - 1; z2 <= z1 + 1 && !set; z2++)
				{
				    if (x2 != x1 || y2 != y1 || z2 != z1)
				    {
					Block b1 = world.getBlock(x2, y2, z2);
					if (b1 == Blocks.ice || b1 == Blocks.water)
					{
					    set = true;
					    world.setBlock(x1, y1, z1, Blocks.water);
					    break;
					}
				    }
				}
			    }
			}
			if (!set) world.setBlockToAir(x1, y1, z1);
		    }
		}
	    }
	}
    }

}
