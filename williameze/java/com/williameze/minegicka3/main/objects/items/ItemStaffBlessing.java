package com.williameze.minegicka3.main.objects.items;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
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

public class ItemStaffBlessing extends ItemStaff
{
    public ItemStaffBlessing()
    {
	super();
	setBaseStats(0.75, 1.5, 1.25, 1.25);
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
	    double range = 12;
	    List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class,
		    AxisAlignedBB.getBoundingBox(user.posX, user.posY, user.posZ, user.posX, user.posY, user.posZ).expand(range, range, range));
	    ESelectorNonHostile sel = new ESelectorNonHostile();
	    boolean userNonHostile = sel.isEntityApplicable(user);
	    for (EntityLivingBase e : list)
	    {
		if (sel.isEntityApplicable(e) == userNonHostile)
		{
		    e.addPotionEffect(new PotionEffect(Potion.damageBoost.getId(), 200, 0));
		    e.addPotionEffect(new PotionEffect(Potion.digSpeed.getId(), 200, 0));
		    e.addPotionEffect(new PotionEffect(Potion.fireResistance.getId(), 200, 0));
		    e.addPotionEffect(new PotionEffect(Potion.jump.getId(), 200, 0));
		    e.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(), 200, 0));
		    e.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 200, 0));
		    e.addPotionEffect(new PotionEffect(Potion.regeneration.getId(), 100, 0));
		    e.addPotionEffect(new PotionEffect(Potion.resistance.getId(), 200, 0));
		    e.addPotionEffect(new PotionEffect(Potion.waterBreathing.getId(), 200, 0));
		}
		else
		{
		    e.addPotionEffect(new PotionEffect(Potion.blindness.getId(), 150, 0));
		    e.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 150, 0));
		    e.addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(), 150, 0));
		    e.addPotionEffect(new PotionEffect(Potion.hunger.getId(), 150, 0));
		    e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 150, 0));
		    e.addPotionEffect(new PotionEffect(Potion.poison.getId(), 75, 0));
		    e.addPotionEffect(new PotionEffect(Potion.weakness.getId(), 150, 0));
		}
	    }
	}
    }

    @Override
    public void passiveAbility(World world, EntityLivingBase user, ItemStack is)
    {
	if (user.ticksExisted % 20 == 0)
	{
	    double range = 8;
	    List<EntityLivingBase> list = world.selectEntitiesWithinAABB(EntityLivingBase.class,
		    AxisAlignedBB.getBoundingBox(user.posX, user.posY, user.posZ, user.posX, user.posY, user.posZ).expand(range, range, range),
		    new ESelectorNonHostile());
	    int index = (user.ticksExisted / 5) % Math.max(list.size(), 5);
	    if (index < list.size())
	    {
		EntityLivingBase e = list.get(index);
		if (!world.isRemote)
		{
		    list.get(index).heal(1);
		}
		else
		{
		    world.spawnParticle("heart", e.posX, e.posY + e.getEyeHeight(), e.posZ, 0, 1, 0);
		}
	    }
	}
    }

    @Override
    public String getActiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Buff friends and debuff foes";
    }

    @Override
    public String getPassiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Randomly distributed healing";
    }

    @Override
    public String getDetailedPassiveDesc(ItemStack is, EntityPlayer p)
    {
	return "Randomly heal nearby non-hostile entities or holder";
    }
}
