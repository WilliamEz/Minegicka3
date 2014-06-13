package com.williameze.minegicka3.main.magicks;

import java.util.Arrays;

import scala.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.williameze.api.HitObject;
import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.ModBase;

public class MagickExplosion extends Magick
{

    public MagickExplosion()
    {
	super("Explosion", "HFAF");
    }

    @Override
    public Object[] getAdditionalCraftClickTabletMaterials()
    {
	return new Object[] { Items.gunpowder, 8 };
    }

    @Override
    public double getBaseManaCost()
    {
	return 250;
    }

    @Override
    public void doTheMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	if (caster != null && caster.getLookVec() != null)
	{
	    Vector look = new Vector(caster.getLookVec());
	    double[] props = getStaffMainProperties(additionalData);
	    double range = 12 * props[0];
	    Vector start = new Vector(caster.posX, caster.posY + caster.getEyeHeight(), caster.posZ);
	    HitObject hit = FuncHelper.rayTrace(world, start, start.add(look.multiply(range)), null, null, Arrays.asList(caster));

	    world.createExplosion(caster, hit.hitPosition.x, hit.hitPosition.y, hit.hitPosition.z, (float) (Math.sqrt(props[0]) * 2D), true);
	}
    }
}
