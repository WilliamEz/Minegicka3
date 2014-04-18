package com.williameze.minegicka3.main.spells;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;

import com.williameze.minegicka3.main.entities.EntityBeam;
import com.williameze.minegicka3.main.spells.Spell.CastType;

public class SpellExecuteBeam extends SpellExecute
{
    @Override
    public void startSpell(Spell s)
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

    @Override
    public void updateSpell(Spell s)
    {
	if (consumeMana(s, s.countElements() * 2.2, true, false, 0) == 0)
	{
	    s.toBeStopped = true;
	}
	if (s.spellTicks > 75 + s.countElements() * 25 || s.castType == CastType.Single && s.getCaster().getLookVec() == null)
	{
	    s.toBeStopped = true;
	}
    }

    @Override
    public void stopSpell(Spell s)
    {
	s.toBeStopped = true;
    }
}
