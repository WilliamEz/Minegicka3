package com.williameze.api.selectors;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;

import com.williameze.minegicka3.main.entities.IEntityLivingNonHostile;

public class ESelectorNonHostile implements IEntitySelector
{

    @Override
    public boolean isEntityApplicable(Entity var1)
    {
	if (var1 instanceof EntityMob) return false;
	if (var1 instanceof EntityPlayer) return true;
	if (var1 instanceof EntityAnimal) return true;
	if (var1 instanceof EntityVillager) return true;
	if (var1 instanceof IEntityLivingNonHostile) return true;
	if (var1 instanceof IAnimals) return true;
	if (var1 instanceof IMob) return false;
	return false;
    }
}
