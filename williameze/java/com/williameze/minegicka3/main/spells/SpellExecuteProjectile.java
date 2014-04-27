package com.williameze.minegicka3.main.spells;

import java.util.Random;

import net.minecraft.entity.Entity;

import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.entities.EntityBoulder;
import com.williameze.minegicka3.main.entities.EntityIcicle;
import com.williameze.minegicka3.main.spells.Spell.CastType;

public class SpellExecuteProjectile extends SpellExecute
{
    public static double defaultMaxChargeTick = 100;

    @Override
    public void startSpell(Spell s)
    {
    }

    @Override
    public void updateSpell(Spell s)
    {
	if (s.castType == CastType.Single)
	{
	    double charged = s.additionalData.getDouble("Projectile charged");
	    charged += 1D / defaultMaxChargeTick * s.getAtkSpeed();
	    if (charged > 1) charged = 1;
	    s.additionalData.setDouble("Projectile charged", charged);

	    if (s.hasElement(Element.Earth))
	    {
		int notEarthAndIce = s.countElements() - s.countElements(Element.Earth, Element.Ice);
		double manaCost = s.countElements() + notEarthAndIce * notEarthAndIce * 100D / defaultMaxChargeTick * s.getAtkSpeed();
		if (charged < 1 && consumeMana(s, manaCost * s.getManaConsumeRate(), true, false, 0) < 1) s.toBeInvalidated = true;
	    }
	    else
	    {
		if (charged < 1 && consumeMana(s, s.countElements() * 1.5D, true, false, 0) < 1) s.toBeInvalidated = true;
	    }
	}
    }

    @Override
    public void stopSpell(Spell s)
    {
	if (s.castType == CastType.Single)
	{
	    Entity caster = s.getCaster();
	    if (caster == null || caster.getLookVec() == null) return;

	    if (s.hasElement(Element.Earth))
	    {
		Vector look = new Vector(caster.getLookVec());
		EntityBoulder bld = new EntityBoulder(caster.worldObj);
		bld.setSpell(s);
		bld.setPosition(caster.posX + look.x, caster.posY + caster.getEyeHeight() - 0.2 + look.y, caster.posZ + look.z);
		double charged = s.additionalData.getDouble("Projectile charged") * s.getPower() * Math.pow(s.countElements(), 0.4) + 1;
		bld.motionX = look.x * 2 * charged;
		bld.motionY = look.y * 2 * charged;
		bld.motionZ = look.z * 2 * charged;

		if (!caster.worldObj.isRemote) caster.worldObj.spawnEntityInWorld(bld);
	    }
	    else
	    {
		Vector look = new Vector(caster.getLookVec());
		int count = s.countElement(Element.Ice) * 8 + 4;
		double maxDirectionRandom = 0.8 - 0.55 * s.additionalData.getDouble("Projectile charged");
		double flyPower = s.getPower() * (1 + s.additionalData.getDouble("Projectile charged"));
		for (int a = 0; a < count; a++)
		{
		    Vector motion = look.randomizeDirection(new Random().nextDouble() * maxDirectionRandom).normalize();
		    EntityIcicle ici = new EntityIcicle(caster.worldObj);
		    ici.setSpell(s);
		    ici.posX = caster.posX + look.x * 0.2;
		    ici.posY = caster.posY + look.y * 0.2 + caster.getEyeHeight() - 0.2;
		    ici.posZ = caster.posZ + look.z * 0.2;
		    ici.setPosition(ici.posX, ici.posY, ici.posZ);
		    ici.motionX = motion.x * flyPower;
		    ici.motionY = motion.y * flyPower;
		    ici.motionZ = motion.z * flyPower;
		    if (!caster.worldObj.isRemote) caster.worldObj.spawnEntityInWorld(ici);
		}
	    }
	}
    }
}
