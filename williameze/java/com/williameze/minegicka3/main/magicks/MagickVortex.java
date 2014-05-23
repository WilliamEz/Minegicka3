package com.williameze.minegicka3.main.magicks;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.entities.EntityVortex;

public class MagickVortex extends Magick
{

    public MagickVortex()
    {
	super("Vortex", "IAIDI");
    }

    @Override
    public Object[] getAdditionalCraftClickTabletMaterials()
    {
	return new Object[] { Items.ender_eye, 16, Blocks.hopper, Items.nether_star };
    }

    @Override
    public double getBaseManaCost()
    {
	return 666;
    }

    @Override
    public void doTheMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	if (!world.isRemote)
	{
	    double[] props = getStaffMainProperties(additionalData);
	    double posX = x;
	    double posY = y;
	    double posZ = z;
	    double power = Math.min(props[0], 6.66);
	    double speed = Math.min(props[1], 6.66);
	    if (caster.getLookVec() != null)
	    {
		posX += caster.getLookVec().xCoord * Math.max(3 * power, 5);
		posY += caster.getLookVec().yCoord * Math.max(3 * power, 5);
		posZ += caster.getLookVec().zCoord * Math.max(3 * power, 5);
	    }
	    EntityVortex vortex = new EntityVortex(world);
	    vortex.setPosition(posX, posY, posZ);
	    vortex.life = (int) Math.max(40D*speed, 80);
	    vortex.power = power;
	    vortex.range = Math.max(power * 2, 2);
	    vortex.interval = (int) Math.max(1,Math.min(10D/speed, 15));
	    world.spawnEntityInWorld(vortex);
	}
    }
}
