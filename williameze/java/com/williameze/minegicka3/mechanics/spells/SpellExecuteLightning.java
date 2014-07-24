package com.williameze.minegicka3.mechanics.spells;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;

import com.williameze.minegicka3.main.entities.magic.EntityLightning;
import com.williameze.minegicka3.main.entities.magic.EntityStorm;
import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.spells.Spell.CastType;

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
	    if (s.castType == CastType.Self)
	    {
		double consumeMana = s.countElements() * 100 + s.countElement(Element.Lightning) * 50 * s.getManaConsumeRate();
		if (s.consumeMana(consumeMana, true, true, 3) > 0)
		{
		    for (int a = 0; a < s.countElement(Element.Lightning); a++)
		    {
			EntityLightningBolt l = new EntityLightningBolt(caster.worldObj, caster.posX + (rnd.nextDouble() - 0.5) * 8, caster.posY,
				caster.posZ + (rnd.nextDouble() - 0.5) * 8);
			caster.worldObj.spawnEntityInWorld(l);
		    }
		    s.elements.removeAll(Collections.singleton(Element.Lightning));
		    s.damageEntity(caster, 1);
		}
		s.toBeInvalidated = true;
	    }
	    else
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
	if (s.castType != CastType.Self)
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
    }

    @Override
    public void stopSpell(Spell s)
    {
	lightnings.remove(s);
    }
}
