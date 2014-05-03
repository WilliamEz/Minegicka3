package com.williameze.minegicka3.main.spells;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import com.williameze.minegicka3.main.entities.EntityLightning;
import com.williameze.minegicka3.main.entities.EntityStorm;
import com.williameze.minegicka3.main.spells.Spell.CastType;

public class SpellExecuteLightning extends SpellExecute
{
    public static Map<Spell, EntityLightning> lightnings = new HashMap();

    @Override
    public void startSpell(Spell s)
    {
	Entity caster = s.getCaster();
	if (caster == null) return;
	if (!caster.isWet() || caster instanceof EntityPlayer && ((EntityPlayer) caster).capabilities.disableDamage)
	{
	    EntityLightning lightning = new EntityLightning(caster.worldObj);
	    lightning.spell = s;
	    lightning.setPosition(caster.posX, caster.posY, caster.posZ);
	    lightning.dieWithSpell = true;
	    if (caster instanceof EntityStorm) lightning.dieWithSpell = false;
	    lightning.maxTick = 75 + s.countElements() * 25;
	    if (!caster.worldObj.isRemote)
	    {
		caster.worldObj.spawnEntityInWorld(lightning);
	    }
	    lightnings.put(s, lightning);
	}
	else
	{
	    s.damageEntity(caster, 1);
	    s.toBeInvalidated = true;
	}
    }

    @Override
    public void updateSpell(Spell s)
    {
	if (consumeMana(s, s.countElements() * 2.2 * s.getManaConsumeRate(), true, false, 0) < 1)
	{
	    s.toBeInvalidated = true;
	}
	if (s.spellTicks > 75 + s.countElements() * 25 || s.castType == CastType.Single && s.getCaster().getLookVec() == null)
	{
	    s.toBeInvalidated = true;
	}
    }

    @Override
    public void stopSpell(Spell s)
    {
	lightnings.remove(s);
    }
}
