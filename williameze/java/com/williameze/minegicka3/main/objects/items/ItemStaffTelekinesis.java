package com.williameze.minegicka3.main.objects.items;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import scala.util.Random;

import com.williameze.api.HitObject;
import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;
import com.williameze.api.selectors.ESelectorNonHostile;
import com.williameze.api.selectors.ESelectorProjectiles;

public class ItemStaffTelekinesis extends ItemStaff
{
    public ItemStaffTelekinesis()
    {
	super();
	setBaseStats(0.5, 2, 0.5, 2);
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
	    ESelectorNonHostile sel = new ESelectorNonHostile();
	    ESelectorProjectiles sel1 = new ESelectorProjectiles();
	    double range = 16;
	    List<Entity> list = world.getEntitiesWithinAABB(Entity.class,
		    AxisAlignedBB.getBoundingBox(user.posX, user.posY, user.posZ, user.posX, user.posY, user.posZ).expand(range, range, range));
	    list.remove(user);
	    for (Entity e : list)
	    {
		Vector v = FuncHelper.vectorToEntity(e, user).normalize();
		double power = 0.4 * user.getDistanceToEntity(e);
		if (sel1.isEntityApplicable(e) || e instanceof EntityLivingBase && sel.isEntityApplicable(e) != sel.isEntityApplicable(user))
		{
		    v = v.reverse();
		    power = 3;
		}
		v = v.multiply(power);
		e.motionX += v.x;
		e.motionY += v.y * 0.5;
		e.motionZ += v.z;
	    }
	}
    }

    @Override
    public void passiveAbility(World world, EntityLivingBase user, ItemStack is)
    {
	double range = 8;
	List<Entity> list = world.selectEntitiesWithinAABB(Entity.class,
		AxisAlignedBB.getBoundingBox(user.posX, user.posY, user.posZ, user.posX, user.posY, user.posZ).expand(range, range, range),
		new ESelectorProjectiles());
	for (Entity e : list)
	{
	    Vector v = FuncHelper.vectorToEntity(user, e).normalize();
	    e.motionX += v.x * 0.35;
	    e.motionY += v.y * 0.35;
	    e.motionZ += v.z * 0.35;
	}
    }

    @Override
    public String getActiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Selective gravitating";
    }

    @Override
    public String getDetailedActiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Pull friendly entities, items and push monsters away";
    }

    @Override
    public String getPassiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Projectile repulsion";
    }

    @Override
    public String getDetailedPassiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Deflect nearby projectiles";
    }
}
