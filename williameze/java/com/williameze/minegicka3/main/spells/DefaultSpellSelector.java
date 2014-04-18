package com.williameze.minegicka3.main.spells;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class DefaultSpellSelector implements IEntitySelector
{
    public Spell spell;
    public boolean forceSelectPlayer = false;
    public boolean forceNonPVP = false;
    public boolean isFromNonPlayer;

    public DefaultSpellSelector(Spell s)
    {
	spell = s;
	isFromNonPlayer = s.getCaster() == null || (s.getCaster() instanceof EntityPlayer == false);
    }

    @Override
    public boolean isEntityApplicable(Entity var1)
    {
	if (var1 instanceof EntityPlayer && ((EntityPlayer) var1).capabilities.disableDamage) return false;
	if (var1 == spell.getCaster()) return false;
	return true;
    }

}
