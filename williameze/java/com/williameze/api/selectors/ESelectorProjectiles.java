package com.williameze.api.selectors;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;

import com.williameze.minegicka3.main.entities.IEntityLivingNonHostile;

public class ESelectorProjectiles implements IEntitySelector
{

    @Override
    public boolean isEntityApplicable(Entity var1)
    {
	if (var1 instanceof EntityThrowable) return true;
	if (var1 instanceof IProjectile) return true;
	if (var1 instanceof EntityFireball) return true;
	return false;
    }

}
