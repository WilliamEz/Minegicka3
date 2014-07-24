package com.williameze.minegicka3.mechanics.magicks;

import java.util.Arrays;
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
import com.williameze.minegicka3.main.entities.magic.EntityVortex;

public class MagickVortex extends Magick
{

    public MagickVortex()
    {
	super("Vortex", "IAIDI");
    }

    @Override
    public List<String> getDescription()
    {
	return Arrays
		.asList(new String[] {
			"Creates a vortex at the caster's crosshair.",
			"Vortex radius: 6 x staff's power (blocks)",
			"Vortex suction power: 1 x staff's power (How fast entities and blocks move toward the vortex)",
			"Vortex lifespan: 2 * staff's attack speed (seconds)",
			"Vortex block-sucking interval: 0.5 / staff's attack speed (seconds) (The delay between 2 consecutive times the vortex tries to alleviate blocks)",
			"Distance between caster and newly spawned vortexes: far enough" });
    }

    @Override
    public Object[] getAdditionalUnlockMaterials()
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
	    double power = Math.min(props[0], 10);
	    double speed = Math.min(props[1], 10);
	    if (caster.getLookVec() != null)
	    {
		double d = Math.max(power * 7, 5);
		posX += caster.getLookVec().xCoord * d;
		posY += caster.getLookVec().yCoord * d;
		posZ += caster.getLookVec().zCoord * d;
	    }
	    EntityVortex vortex = new EntityVortex(world);
	    vortex.setPosition(posX, posY, posZ);
	    vortex.life = (int) Math.max(40D * speed, 80);
	    vortex.power = power;
	    vortex.range = Math.max(power * 6, 2);
	    vortex.interval = (int) Math.max(1, Math.min(10D / speed, 30));
	    world.spawnEntityInWorld(vortex);
	}
    }
}
