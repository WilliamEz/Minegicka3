package com.williameze.minegicka3.main.spells;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.entities.fx.FXEProjectileCharge;
import com.williameze.minegicka3.main.entities.magic.EntityBoulder;
import com.williameze.minegicka3.main.entities.magic.EntityEarthRumble;
import com.williameze.minegicka3.main.entities.magic.EntityIceShard;
import com.williameze.minegicka3.main.entities.magic.EntityIcicle;
import com.williameze.minegicka3.main.spells.Spell.CastType;

public class SpellExecuteProjectile extends SpellExecute
{
    public static double defaultMaxChargeTick = 100;

    @Override
    public void startSpell(Spell s)
    {
	if (s.castType == CastType.Area)
	{
	    Random rnd = new Random();
	    Entity caster = s.getCaster();
	    if (caster == null) return;
	    double manaToConsume = s.countElements() * s.countElements() * 100 * s.getManaConsumeRate();
	    if (consumeMana(s, manaToConsume, true, true, 3) > 0)
	    {
		if (s.hasElement(Element.Ice) && !caster.worldObj.isRemote)
		{
		    int max = s.countElements() * s.countElements() * 2 + 4;
		    double range = s.countElements() * 4 + 4;
		    double hrange = Math.sqrt(s.countElements());
		    List<EntityLivingBase> list = caster.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(
			    caster.posX - range, caster.posY - hrange, caster.posZ - range, caster.posX + range, caster.posY + hrange, caster.posZ
				    + range), new ESelectorDefault(s));
		    for (int a = 0; a < max; a++)
		    {
			double posX = caster.posX + (rnd.nextDouble() - 0.5) * 2 * range;
			double posY = caster.posY + caster.height / 2 + (rnd.nextDouble() - 0.5) * 2 * hrange;
			double posZ = caster.posZ + (rnd.nextDouble() - 0.5) * 2 * range;
			if (!list.isEmpty())
			{
			    Entity e = list.get(rnd.nextInt(list.size()));
			    s.damageEntity(e, 1);
			    posX = e.posX;
			    posY = e.posY + e.height / 2;
			    posZ = e.posZ;
			}
			EntityIceShard ics = new EntityIceShard(caster.worldObj);
			ics.setPosition(posX, posY, posZ);
			ics.spell = s;
			ics.maxTick = 12;
			caster.worldObj.spawnEntityInWorld(ics);
		    }
		    if (s.hasElement(Element.Earth))
		    {
			List<Element> earths = new ArrayList();
			for (Element e : s.elements)
			{
			    if (e == Element.Earth) earths.add(e);
			}
			Spell s1 = new Spell(earths, s.dimensionID, s.casterUUID, s.casterName, CastType.Area, s.additionalData);
			EntityEarthRumble rumb = new EntityEarthRumble(caster.worldObj);
			rumb.setPosition(caster.posX, caster.posY, caster.posZ);
			rumb.setSpell(s1);
			if (!caster.worldObj.isRemote) caster.worldObj.spawnEntityInWorld(rumb);
		    }
		}
		else
		{
		    EntityEarthRumble rumb = new EntityEarthRumble(caster.worldObj);
		    rumb.setPosition(caster.posX, caster.posY, caster.posZ);
		    rumb.setSpell(s);
		    if (!caster.worldObj.isRemote) caster.worldObj.spawnEntityInWorld(rumb);
		}
	    }
	}
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
		double manaCost = s.countElements() * s.countElements() * 100D / defaultMaxChargeTick * s.getAtkSpeed();
		if (charged < 1 && consumeMana(s, manaCost * s.getManaConsumeRate(), true, false, 0) < 1) s.toBeInvalidated = true;
	    }
	    else
	    {
		if (charged < 1 && consumeMana(s, s.countElements() * 150 / defaultMaxChargeTick, true, false, 0) < 1) s.toBeInvalidated = true;
	    }

	    Entity e = s.getCaster();
	    Random rnd = new Random();
	    if (e != null && e.worldObj.isRemote)
	    {
		Vector look = new Vector(e.getLookVec()).multiply(0.6);
		double dif = 0.6 - 0.4 * charged;
		double midX = e.posX + look.x;
		double midY = e.posY + look.y + e.getEyeHeight() - 0.2;
		double midZ = e.posZ + look.z;
		for (int a = 0; a < Math.ceil(2 * charged); a++)
		{
		    FXEProjectileCharge pc = new FXEProjectileCharge(e.worldObj);
		    if (s.hasElement(Element.Ice))
		    {
			if (s.hasElement(Element.Earth) && a % 2 == 0) pc.color = Element.Earth.getColor();
			else pc.color = Element.Ice.getColor();
		    }
		    else pc.color = Element.Earth.getColor();
		    pc.setPosition(midX + (rnd.nextDouble() - 0.5) * 2 * dif, midY + (rnd.nextDouble() - 0.5) * 2 * dif, midZ
			    + (rnd.nextDouble() - 0.5) * 2 * dif);
		    pc.destination = new Vector(midX, midY, midZ);
		    pc.pullToDest = 0.01;
		    pc.alpha = charged;
		    pc.noClip = true;
		    e.worldObj.spawnEntityInWorld(pc);
		}
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
		double further = Math.sqrt(bld.width * bld.width / 4 + bld.height * bld.height / 4);
		bld.setPosition(caster.posX + look.x * further, caster.posY + caster.getEyeHeight() - 0.2 + look.y * further, caster.posZ + look.z
			* further);
		double charged = s.additionalData.getDouble("Projectile charged") * s.getPower() * Math.pow(s.countElements(), 0.4) + 1;
		bld.motionX = look.x * 1.5 * charged;
		bld.motionY = look.y * 1.5 * charged;
		bld.motionZ = look.z * 1.5 * charged;

		if (!caster.worldObj.isRemote) caster.worldObj.spawnEntityInWorld(bld);
	    }
	    else
	    {
		Vector look = new Vector(caster.getLookVec());
		int count = s.countElement(Element.Ice) * 4 + 4;
		double maxDirectionRandom = 0.65 - 0.5 * s.additionalData.getDouble("Projectile charged");
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
