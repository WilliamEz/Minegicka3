package com.williameze.minegicka3.main.objects.items;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import scala.util.Random;

import com.williameze.api.HitObject;
import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;

public class ItemStaffHemmy extends ItemStaff
{
    public ItemStaffHemmy()
    {
	super();
	setBaseStats(10, 10, 0.1, 10);
    }

    @Override
    public EnumRarity getRarity(ItemStack is)
    {
	return EnumRarity.epic;
    }

    @Override
    public boolean hasActiveAbility(World world, EntityLivingBase user, ItemStack is)
    {
	return true;
    }

    @Override
    public void activeAbility(World world, EntityLivingBase e, ItemStack is)
    {
	if (e != null && e.getLookVec() != null)
	{
	    Random rnd = new Random();
	    Vector look = new Vector(e.getLookVec());
	    double range = 64;
	    Vector start = new Vector(e.posX, e.posY + e.getEyeHeight(), e.posZ);
	    HitObject hit = FuncHelper.rayTrace(world, start, start.add(look.multiply(range)), null, null, Arrays.asList(e));
	    EntityLightningBolt lgt = new EntityLightningBolt(world, hit.hitPosition.x, hit.hitPosition.y, hit.hitPosition.z);
	    lgt.renderDistanceWeight *= 12;
	    world.spawnEntityInWorld(lgt);
	}
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean advancedItemTooltip)
    {
	super.addInformation(is, p, l, advancedItemTooltip);
	l.add("Dedicated to Hemmy the almighty.");
	l.add(EnumChatFormatting.RED + "" + EnumChatFormatting.ITALIC + "I'm unobtainable!");
    }

    @Override
    public String getActiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Lightning strike";
    }
}
