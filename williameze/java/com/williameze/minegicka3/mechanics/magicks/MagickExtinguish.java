package com.williameze.minegicka3.mechanics.magicks;

import java.util.Arrays;
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

public class MagickExtinguish extends Magick
{

    public MagickExtinguish()
    {
	super("Extinguish", "WD");
    }

    @Override
    public Object[] getAdditionalUnlockMaterials()
    {
	return new Object[] { Items.water_bucket };
    }

    @Override
    public List<String> getDescription()
    {
        return Arrays.asList(new String[]{
        	"Extinguishes nearby fire blocks and entities on-fire.",
        	"Radius of effect: 16 x staff's power (blocks)"
        });
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
	List<Entity> l = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(radius, radius, radius));
	if (!l.isEmpty())
	{
	    for (int a = 0; a < l.size(); a++)
	    {
		l.get(a).extinguish();
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
		    if (b == Blocks.fire)
		    {
			world.setBlockToAir(x1, y1, z1);
		    }
		}
	    }
	}
    }

}
