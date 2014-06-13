package com.williameze.minegicka3.main.magicks;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.entities.magic.EntityHomingLightning;

public class MagickHomingLightning extends Magick
{

    public MagickHomingLightning()
    {
	super("Homing Lightning", "SSHAH");
    }

    @Override
    public Object[] getAdditionalCraftClickTabletMaterials()
    {
	return new Object[] { Items.ender_eye, 4, Items.iron_ingot, 8, ModBase.essenceLightning, 4 };
    }

    @Override
    public double getBaseManaCost()
    {
	return 600;
    }

    @Override
    public void doTheMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	if (!world.isRemote)
	{
	    double[] props = getStaffMainProperties(additionalData);
	    double range = Math.min(8 * props[0], 32);
	    int count = (int) Math.floor(6 + props[1] * 2D);
	    List<EntityLivingBase> l = world.selectEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB
		    .getBoundingBox(x, y, z, x, y, z).expand(range, range, range), new IEntitySelector()
	    {
		@Override
		public boolean isEntityApplicable(Entity var1)
		{
		    return !var1.isDead;
		}
	    });
	    l.remove(caster);
	    for (; count > 0; count--)
	    {
		if (l.isEmpty())
		{
		    EntityHomingLightning hl = new EntityHomingLightning(world);
		    hl.setPosition(x, y, z);
		    hl.targetX = x + (rnd.nextDouble() - 0.5) * 2 * range;
		    hl.targetY = y + (rnd.nextDouble() - 0.5) * 2 * range;
		    hl.targetZ = z + (rnd.nextDouble() - 0.5) * 2 * range;
		    hl.motionY = 0.7;
		    world.spawnEntityInWorld(hl);
		}
		else
		{
		    EntityLivingBase e = l.get(rnd.nextInt(l.size()));
		    EntityHomingLightning hl = new EntityHomingLightning(world);
		    hl.setPosition(x, y, z);
		    hl.targetX = e.posX;
		    hl.targetY = e.posY;
		    hl.targetZ = e.posZ;
		    hl.motionY = 0.7;
		    world.spawnEntityInWorld(hl);
		    l.remove(e);
		}
	    }
	}
    }
}
