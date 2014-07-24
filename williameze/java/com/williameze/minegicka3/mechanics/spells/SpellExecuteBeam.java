package com.williameze.minegicka3.mechanics.spells;

import net.minecraft.entity.Entity;

import com.williameze.minegicka3.main.entities.magic.EntityBeam;
import com.williameze.minegicka3.main.entities.magic.EntityBeamArea;
import com.williameze.minegicka3.mechanics.SpellDamageModifier;
import com.williameze.minegicka3.mechanics.spells.Spell.CastType;

import cpw.mods.fml.common.FMLCommonHandler;

public class SpellExecuteBeam extends SpellExecute
{
    @Override
    public void startSpell(Spell s)
    {
	Entity caster = s.getCaster();
	if (caster == null) return;
	if (s.castType == CastType.Single)
	{
	    EntityBeam beam = new EntityBeam(caster.worldObj);
	    beam.spell = s;
	    beam.setPosition(caster.posX, caster.posY, caster.posZ);
	    if (!caster.worldObj.isRemote)
	    {
		caster.worldObj.spawnEntityInWorld(beam);
	    }
	}
	else if (s.castType == CastType.Area)
	{
	    double manaToConsume = s.countElements() * s.countElements() * 100 * s.getManaConsumeRate();
	    if (consumeMana(s, manaToConsume, true, true, 3) > 0)
	    {
		EntityBeamArea beamA = new EntityBeamArea(caster.worldObj);
		beamA.spell = s;
		beamA.setPosition(caster.posX, caster.posY + caster.height / 2, caster.posZ);
		if (!caster.worldObj.isRemote)
		{
		    caster.worldObj.spawnEntityInWorld(beamA);
		}
	    }
	    s.toBeInvalidated = true;
	}
	else if (s.castType == CastType.Self)
	{
	    double manaToConsume = s.countElements() * 50 * s.getManaConsumeRate();
	    double consumedPercentage = consumeMana(s, manaToConsume, true, false, 0);
	    if (consumedPercentage > 0)
	    {
		s.damageEntity(caster, 0, new SpellDamageModifier(consumedPercentage));
	    }
	    s.toBeInvalidated = true;
	}
    }

    @Override
    public void updateSpell(Spell s)
    {
	if (s.castType == CastType.Single)
	{
	    if (consumeMana(s, s.countElements() * 2.2 * s.getManaConsumeRate(), true, false, 0) == 0)
	    {
		s.toBeInvalidated = true;
	    }
	    if (s.spellTicks > 75 + s.countElements() * 25 || s.castType == CastType.Single && s.getCaster().getLookVec() == null)
	    {
		s.toBeInvalidated = true;
	    }
	}
    }

}
