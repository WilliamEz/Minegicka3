package com.williameze.minegicka3.main.objects.items;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import scala.util.Random;

import com.williameze.api.HitObject;
import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;
import com.williameze.api.selectors.ESelectorNonHostile;

public class StaffDestruction extends Staff
{
    public StaffDestruction()
    {
	super();
	setBaseStats(1.5, 0.75, 0.8, 0.8);
    }

    @Override
    public boolean hasActiveAbility(World world, EntityLivingBase user, ItemStack is)
    {
	return true;
    }

    @Override
    public double getActiveAbilityCost(World world, EntityLivingBase user, ItemStack is)
    {
	return 200;
    }

    @Override
    public void activeAbility(World world, EntityLivingBase user, ItemStack is)
    {
	consumeMana(world, user, is);
	if (!world.isRemote && user != null && user.getLookVec() != null)
	{
	    Vector look = new Vector(user.getLookVec());
	    double range = 16;
	    Vector start = new Vector(user.posX, user.posY + user.getEyeHeight(), user.posZ);
	    HitObject hit = FuncHelper.rayTrace(world, start, start.add(look.multiply(range)), null, null, Arrays.asList(user));
	    world.createExplosion(user, hit.hitPosition.x, hit.hitPosition.y, hit.hitPosition.z, 1F + new Random().nextFloat() * 2F, true);
	}
    }

    @Override
    public void passiveAbility(World world, EntityLivingBase user, ItemStack is)
    {
	if (!world.isRemote && user.ticksExisted % 40 == 0)
	{
	    double range = 12;
	    Random rnd = new Random();
	    int minX = (int) Math.floor(user.posX - range);
	    int minY = (int) Math.floor(user.posY - range);
	    int minZ = (int) Math.floor(user.posZ - range);
	    int maxX = (int) Math.floor(user.posX + range);
	    int maxY = (int) Math.floor(user.posY + range);
	    int maxZ = (int) Math.floor(user.posZ + range);
	    for (int x = minX; x <= maxX; x++)
	    {
		for (int y = minY; y <= maxY; y++)
		{
		    for (int z = minZ; z <= maxZ; z++)
		    {
			if (Math.abs(x - user.posX) > 3 || Math.abs(y - user.posY) > 3 || Math.abs(z - user.posZ) > 3)
			{
			    if (world.getBlock(x, y, z) == Blocks.fire && rnd.nextInt(3) == 0)
			    {
				if (rnd.nextInt(3) == 0 && world.getBlock(x - 1, y, z).getMaterial().isReplaceable()
					&& Blocks.fire.canPlaceBlockAt(world, x - 1, y, z))
				{
				    world.setBlock(x - 1, y, z, Blocks.fire);
				}
				if (rnd.nextInt(3) == 0 && world.getBlock(x + 1, y, z).getMaterial().isReplaceable()
					&& Blocks.fire.canPlaceBlockAt(world, x + 1, y, z))
				{
				    world.setBlock(x + 1, y, z, Blocks.fire);
				}
				if (rnd.nextInt(3) == 0 && world.getBlock(x, y - 1, z).getMaterial().isReplaceable()
					&& Blocks.fire.canPlaceBlockAt(world, x, y - 1, z))
				{
				    world.setBlock(x, y - 1, z, Blocks.fire);
				}
				if (rnd.nextInt(3) == 0 && world.getBlock(x, y + 1, z).getMaterial().isReplaceable()
					&& Blocks.fire.canPlaceBlockAt(world, x, y + 1, z))
				{
				    world.setBlock(x, y + 1, z, Blocks.fire);
				}
				if (rnd.nextInt(3) == 0 && world.getBlock(x, y, z - 1).getMaterial().isReplaceable()
					&& Blocks.fire.canPlaceBlockAt(world, x, y + 1, z - 1))
				{
				    world.setBlock(x, y, z - 1, Blocks.fire);
				}
				if (rnd.nextInt(3) == 0 && world.getBlock(x, y, z + 1).getMaterial().isReplaceable()
					&& Blocks.fire.canPlaceBlockAt(world, x, y + 1, z + 1))
				{
				    world.setBlock(x, y, z + 1, Blocks.fire);
				}
			    }
			}
		    }
		}
	    }
	}
    }

    @Override
    public String getActiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Explosion";
    }

    @Override
    public String getPassiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Fire spread";
    }

    @Override
    public String getDetailedPassiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Cause nearby fire to spread to adjacent blocks";
    }
}
