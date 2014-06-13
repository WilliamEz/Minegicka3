package com.williameze.minegicka3.main.spells;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import com.williameze.minegicka3.main.entities.magic.EntityMine;

public class ESelectorBeamArea extends ESelectorDefault
{
    public ESelectorBeamArea(Spell s)
    {
	super(s);
    }

    @Override
    public boolean isEntityApplicable(Entity var1)
    {
	if (var1 == spell.getCaster()) return false;
	return var1 instanceof EntityLivingBase || var1 instanceof EntityMine;
    }

}
