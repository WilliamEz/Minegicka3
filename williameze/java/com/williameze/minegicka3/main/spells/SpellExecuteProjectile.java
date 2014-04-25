package com.williameze.minegicka3.main.spells;

import net.minecraft.entity.Entity;

import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.entities.EntityBoulder;

public class SpellExecuteProjectile extends SpellExecute
{
    @Override
    public void startSpell(Spell s)
    {
	super.startSpell(s);
	Entity caster = s.getCaster();
	
	if (caster.worldObj.isRemote || caster.getLookVec() == null) return;
	Vector look = new Vector(caster.getLookVec());
	EntityBoulder bld = new EntityBoulder(caster.worldObj);
	bld.setSpell(s);
	bld.setPosition(caster.posX + look.x, caster.posY + caster.getEyeHeight() - 0.2 + look.y, caster.posZ + look.z);
	bld.motionX = look.x/100;
	bld.motionY = look.y/100;
	bld.motionZ = look.z/100;

	caster.worldObj.spawnEntityInWorld(bld);
    }

    @Override
    public void updateSpell(Spell s)
    {
    }
}
