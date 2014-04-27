package com.williameze.minegicka3.main.spells;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import com.williameze.minegicka3.main.entities.EntityIcicle;
import com.williameze.minegicka3.main.entities.EntityLightning;
import com.williameze.minegicka3.main.entities.EntitySpray;

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
	if (var1 instanceof EntityIcicle && ((EntityIcicle) var1).getSpell().equals(spell)) return false;
	if (var1 instanceof EntitySpray && ((EntitySpray) var1).getSpell().equals(spell)) return false;
	if (var1 instanceof EntityLightning && ((EntityLightning) var1).spell.equals(spell)) return false;
	if (var1 instanceof EntityPlayer && ((EntityPlayer) var1).capabilities.disableDamage) return false;
	if (var1 instanceof EntityPlayer && spell.getCaster() instanceof EntityPlayer && forceNonPVP) return false;
	if (var1 == spell.getCaster()) return false;
	return true;
    }

}
