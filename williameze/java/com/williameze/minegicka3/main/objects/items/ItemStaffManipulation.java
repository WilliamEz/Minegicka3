package com.williameze.minegicka3.main.objects.items;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemStaffManipulation extends ItemStaff
{
    public ItemStaffManipulation()
    {
	super();
	setBaseStats(1, 1, 0.5, 2);
    }

    @Override
    public boolean hasActiveAbility(World world, EntityLivingBase user, ItemStack is)
    {
	return true;
    }

    @Override
    public double getActiveAbilityCost(World world, EntityLivingBase user, ItemStack is)
    {
	return 100;
    }

    @Override
    public void activeAbility(World world, EntityLivingBase user, ItemStack is)
    {
	consumeMana(world, user, is);
	if (!world.isRemote)
	{
	    user.addPotionEffect(new PotionEffect(Potion.invisibility.getId(), 400, 1));
	}
    }

    @Override
    public void passiveAbility(World world, EntityLivingBase user, ItemStack is)
    {
	if (user.ticksExisted % 20 == 0)
	{
	    Random rnd = new Random();
	    double range = 12;
	    List<EntityMob> list = world.getEntitiesWithinAABB(EntityMob.class,
		    AxisAlignedBB.getBoundingBox(user.posX, user.posY, user.posZ, user.posX, user.posY, user.posZ).expand(range, range, range));
	    list.remove(user);
	    for (EntityMob e : list)
	    {
		EntityMob target = list.get(rnd.nextInt(list.size()));
		e.setAttackTarget(target);
		e.setTarget(target);
	    }
	}
    }

    @Override
    public String getActiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Invisibility";
    }

    @Override
    public String getPassiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Monsters confusion";
    }

    @Override
    public String getDetailedPassiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Cause nearby monsters to target each other";
    }
}
