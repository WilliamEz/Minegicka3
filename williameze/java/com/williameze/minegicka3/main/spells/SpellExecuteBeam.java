package com.williameze.minegicka3.main.spells;

import net.minecraft.entity.Entity;

import com.williameze.minegicka3.main.entities.EntityBeam;
import com.williameze.minegicka3.main.entities.EntityBeamArea;
import com.williameze.minegicka3.main.spells.Spell.CastType;

public class SpellExecuteBeam extends SpellExecute
{
    @Override
    public void startSpell(Spell s)
    {
	if (s.castType == CastType.Single)
	{
	    Entity caster = s.getCaster();
	    if (caster == null) return;
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
	    Entity caster = s.getCaster();
	    if (caster == null) return;
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
