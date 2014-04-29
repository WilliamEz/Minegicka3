package com.williameze.minegicka3.main.spells;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import scala.util.Random;

import com.williameze.api.math.Plane;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.entities.EntitySpray;
import com.williameze.minegicka3.main.entities.EntitySprayCold;
import com.williameze.minegicka3.main.entities.EntitySprayFire;
import com.williameze.minegicka3.main.entities.EntitySpraySteam;
import com.williameze.minegicka3.main.entities.EntitySprayWater;
import com.williameze.minegicka3.main.spells.Spell.CastType;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class SpellExecuteSpray extends SpellExecute
{
    /**
     * Duration: 80 + ele*20
     **/
    @Override
    public void updateSpell(Spell s)
    {
	if (s.spellTicks > 75 + s.countElements() * 25 || s.getCaster().getLookVec() == null)
	{
	    s.toBeInvalidated = true;
	    return;
	}
	else
	{
	    Entity caster = s.getCaster();
	    boolean client = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
	    double power = Math.pow(s.countElements(), 0.3) * s.getPower() * 1.25D;
	    double consume = Math.pow(s.countElements(), 1.2) * s.getManaConsumeRate() * 1.5D;
	    int loop = client ? 2 : 1;

	    Vector dir = new Vector(caster.getLookVec());
	    if (s.castType == CastType.Area)
	    {
		if (dir.y == 1D || dir.y == -1D)
		{
		    dir.x = 1;
		    dir.z = 1;
		}
		dir.y = dir.y / 100;
		dir = dir.normalize();
		dir.rotateAroundY(Math.PI / 16 * (s.spellTicks) * s.getPower());
		loop *= 2;
		consume *= 1.5;
	    }

	    double consumeRate = consumeMana(s, consume, true, false, 0);
	    power *= consumeRate;
	    if (consumeRate < 1) s.toBeInvalidated = true;

	    Vector pos = (new Vector(caster.posX, caster.posY + caster.getEyeHeight(), caster.posZ)).add(dir.multiply(0.2));
	    if (s.castType == CastType.Area)
	    {
		pos.y += caster.getLookVec().yCoord;
	    }

	    for (int a = 0; a < loop; a++)
	    {
		for (Element e : s.elements)
		{
		    if (client && rnd.nextInt(10) <= 6 || !client && rnd.nextInt(10) == 0)
		    {
			shootSpray(s, e, pos, dir, power, 0.4, client);
		    }
		}
	    }
	}
    }

    public void shootSpray(Spell s, Element e, Vector pos, Vector dir, double power, double scatter, boolean client)
    {
	Entity caster = s.getCaster();
	EntitySpray sp;
	if (e == Element.Fire) sp = new EntitySprayFire(s.caster.worldObj);
	else if (e == Element.Water) sp = new EntitySprayWater(s.caster.worldObj);
	else if (e == Element.Cold) sp = new EntitySprayCold(s.caster.worldObj);
	else sp = new EntitySpraySteam(s.caster.worldObj);
	sp.setSpell(s);
	sp.spiralCore = dir;
	sp.setPosition(pos.x, pos.y, pos.z);
	sp.server = !client;

	Plane p = new Plane(dir, dir);
	Vector apoint = p.getAssurancePoint();
	Vector topoint = apoint.subtract(dir).normalize().multiply(rnd.nextDouble() * scatter);
	topoint = topoint.rotateAround(dir, rnd.nextDouble() * Math.PI * 2);
	Vector motion = dir.add(topoint).multiply(power);
	sp.motionX = motion.x + caster.motionX;
	sp.motionY = motion.y + caster.motionY;
	sp.motionZ = motion.z + caster.motionZ;

	s.getCaster().worldObj.spawnEntityInWorld(sp);
    }
}
