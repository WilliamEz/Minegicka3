package com.williameze.minegicka3.main.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.williameze.api.math.Vector;
import com.williameze.minegicka3.CommonProxy;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.SpellDamageModifier;
import com.williameze.minegicka3.main.spells.Spell;
import com.williameze.minegicka3.main.spells.Spell.CastType;

public class EntityBoulder extends EntityProjectile
{
    public int collideLeft;

    public EntityBoulder(World par1World)
    {
	super(par1World);
    }

    @Override
    public void setSpell(Spell s)
    {
	super.setSpell(s);
	double charged = spell.additionalData.getDouble("Projectile charged");
	if (charged < 0.2) charged = 0.2;
	if (charged > 1) charged = 1;
	int eCount = spell.countElements(Element.Earth, Element.Ice);
	float radius = (float) Math.sqrt(Math.sqrt((double) eCount) * charged * 0.3D);
	setSize(radius * 2, radius * 2);
	collideLeft = eCount + 2;
	friction = 0.99 - eCount * 0.005;
	gravity = 0.01 * Math.sqrt(eCount);
    }

    public boolean isIce()
    {
	return spell.hasElement(Element.Ice);
    }
    
    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if(ticksExisted>=200)
        {
            novaHere();
            setDead();
        }
    }

    @Override
    public void collideWithBlock(int x, int y, int z)
    {
	super.collideWithBlock(x, y, z);
	if (isDead) return;
	if (getSpell().countElements(Element.Earth, Element.Ice) == getSpell().countElements())
	{

	}
	else
	{
	    novaHere();
	    setDead();
	}
    }

    @Override
    public void collideWithEntity(Entity e)
    {
	super.collideWithEntity(e);
	if (isDead || e == null || e.isDead) return;
	if (getSpell().countElements(Element.Earth, Element.Ice) == getSpell().countElements())
	{
	    double mass = width * width * height;
	    double emass = e.width * e.width * e.height;
	    Vector motion = new Vector(motionX, motionY, motionZ);
	    Vector emotion = new Vector(e.motionX, e.motionY, e.motionZ);
	    Vector newMotion = motion.multiply(mass - emass).add(emotion.multiply(2 * emass)).multiply(1D / (mass + emass));
	    Vector neweMotion = emotion.multiply(emass - mass).add(motion.multiply(2 * mass)).multiply(1D / (mass + emass));
	    motionX = motion.x;
	    motionY = motion.y;
	    motionZ = motion.z;
	    e.motionX = emotion.x;
	    e.motionY = emotion.y;
	    e.motionZ = emotion.z;

	    getSpell().damageEntity(e, 2);
	    collideLeft--;
	    if (collideLeft <= 0) setDead();
	}
	else
	{
	    novaHere();
	    setDead();
	}
    }

    public void novaHere()
    {
	if (!worldObj.isRemote && !isDead)
	{
	    List<Element> le = new ArrayList();
	    for (Element e : getSpell().elements)
	    {
		if (e != Element.Earth && e != Element.Ice) le.add(e);
	    }
	    Spell s = new Spell(le, getSpell().dimensionID, getPersistentID(), CastType.Area, getSpell().additionalData);
	    EntityBeamArea beamA = new EntityBeamArea(worldObj);
	    beamA.spell = s;
	    beamA.setPosition(posX, posY + 1, posZ);
	    beamA.damMod = new SpellDamageModifier(0.5 + getSpell().additionalData.getDouble("Projectile charged"));
	    worldObj.spawnEntityInWorld(beamA);
	}
    }
}
