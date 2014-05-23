package com.williameze.minegicka3.main.magicks;

import java.util.Arrays;

import scala.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.williameze.api.HitObject;
import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;

public class MagickTeleport extends Magick
{

    public MagickTeleport()
    {
	super("Teleport", "HAH");
    }

    @Override
    public Object[] getAdditionalCraftClickTabletMaterials()
    {
	return new Object[] { Items.ender_pearl, 16 };
    }

    @Override
    public double getBaseManaCost()
    {
	return 400;
    }

    @Override
    public void doTheMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	if (caster != null && caster.getLookVec() != null)
	{
	    Random rnd = new Random();
	    Vector look = new Vector(caster.getLookVec());
	    double[] props = getStaffMainProperties(additionalData);
	    double range = 12 * props[0];
	    Vector start = new Vector(caster.posX, caster.posY + caster.getEyeHeight(), caster.posZ);
	    HitObject hit = FuncHelper.rayTrace(world, start, start.add(look.multiply(range)), null, null, Arrays.asList(caster));

	    for (int a = 0; a < 40; a++)
	    {
		world.spawnParticle("smoke", caster.posX, caster.posY + caster.getEyeHeight() / 2, caster.posZ,
			(rnd.nextDouble() - 0.5) * 0.3, (rnd.nextDouble() - 0.5) * 0.3, (rnd.nextDouble() - 0.5) * 0.3);
	    }
	    caster.setPosition(hit.hitPosition.x, hit.hitPosition.y, hit.hitPosition.z);
	    for (int a = 0; a < 40; a++)
	    {
		world.spawnParticle("cloud", hit.hitPosition.x, hit.hitPosition.y + caster.getEyeHeight(), hit.hitPosition.z,
			(rnd.nextDouble() - 0.5) * 0.3, (rnd.nextDouble() - 0.5) * 0.3, (rnd.nextDouble() - 0.5) * 0.3);
	    }
	}
    }
}
