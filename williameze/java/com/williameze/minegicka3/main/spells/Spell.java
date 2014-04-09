package com.williameze.minegicka3.main.spells;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.williameze.minegicka3.bridges.Values;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.SpellDamageModifier;
import com.williameze.minegicka3.main.objects.ItemStaff;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class Spell
{
    public static enum CastType
    {
	Single, Area, Self, Weapon;
    }

    public static enum SpellType
    {
	Grounded, Projectile, Beam, Lightning, Spray;
    }

    public int spellTicks;

    public List<Element> elements = new ArrayList();
    public int dimensionID;
    public UUID casterUUID;
    public CastType castType;
    public SpellType spellType;
    public NBTTagCompound additionalData;
    public boolean toBeStopped;

    public Entity caster;
    public Map<Entity, Integer> recentlyAffected = new HashMap();

    public Spell(List<Element> l, int dimID, UUID entityID, CastType type, NBTTagCompound addi)
    {
	toBeStopped = false;
	spellTicks = 0;
	elements.clear();
	elements.addAll(l);
	dimensionID = dimID;
	casterUUID = entityID;
	castType = type;
	if (elements.contains(Element.Shield))
	{
	    spellType = SpellType.Grounded;
	}
	else if (elements.contains(Element.Earth) || elements.contains(Element.Ice))
	{
	    spellType = SpellType.Projectile;
	}
	else if (elements.contains(Element.Arcane) || elements.contains(Element.Life))
	{
	    spellType = SpellType.Beam;
	}
	else if (elements.contains(Element.Lightning))
	{
	    spellType = SpellType.Lightning;
	}
	else spellType = SpellType.Spray;
	additionalData = addi;
    }

    public SpellExecute getExecute()
    {
	return SpellExecute.getSpellExecute(this);
    }

    public Entity getCaster()
    {
	if (caster == null)
	{
	    if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
	    {
		if (Minecraft.getMinecraft().theWorld.provider.dimensionId == dimensionID)
		{
		    caster = Values.worldEntitiesUUIDMap.get(Minecraft.getMinecraft().theWorld).get(casterUUID);
		}
	    }
	    else if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
	    {
		World w = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimensionID);
		if (w != null)
		{
		    caster = Values.worldEntitiesUUIDMap.get(w).get(casterUUID);
		}
	    }
	}
	return caster;
    }

    public int countElements()
    {
	return elements.size();
    }

    public int countElement(Element e)
    {
	int c = 0;
	for (int a = 0; a < elements.size(); a++)
	{
	    if (elements.get(a) == e) c++;
	}
	return c;
    }

    public int countElements(Element... es)
    {
	int c = 0;
	for (int a = 0; a < es.length; a++)
	{
	    c += countElement(es[a]);
	}
	return c;
    }

    public void startSpell()
    {
	getExecute().startSpell(this);
    }

    public void updateSpell()
    {
	if (!toBeStopped)
	{
	    spellTicks++;
	    getExecute().updateSpell(this);

	    Iterator<Entry<Entity, Integer>> ite = recentlyAffected.entrySet().iterator();
	    List<Entity> toRemove = new ArrayList();
	    while (ite.hasNext())
	    {
		Entry<Entity, Integer> en = ite.next();
		en.setValue(en.getValue() - 1);
		if (en.getValue() == 0) toRemove.add(en.getKey());
	    }
	    for (Entity e : toRemove)
	    {
		recentlyAffected.remove(e);
	    }
	}
    }

    public void stopSpell()
    {
	getExecute().stopSpell(this);
    }

    public void damageEntity(Entity e)
    {
	damageEntity(e, SpellDamageModifier.defau);
    }

    public void damageEntity(Entity e, SpellDamageModifier mod)
    {
	int countWater = countElement(Element.Water);
	int countLife = countElement(Element.Life);
	int countCold = countElement(Element.Cold);
	int countLightning = countElement(Element.Lightning);
	int countShield = countElement(Element.Shield);
	int countArcane = countElement(Element.Arcane);
	int countSteam = countElement(Element.Steam);
	int countFire = countElement(Element.Fire);
	int countIce = countElement(Element.Ice);
	int countEarth = countElement(Element.Earth);

	boolean isEntityWet = e.isWet() || countWater + countSteam > 0;
	if (isEntityWet) e.extinguish();
	if (!isEntityWet && countFire > 0) e.setFire(countFire * 3);
	if (e instanceof EntityLivingBase)
	{
	    EntityLivingBase be = (EntityLivingBase) e;
	    if (countCold > 0) be.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 20 * countCold, countCold));
	    if (countCold + countWater + countSteam > 0) be.addPotionEffect(new PotionEffect(Potion.fireResistance.getId(),
		    (countCold + countWater + countSteam) * 20, countCold + countWater + countSteam));
	    if (countLife > 0) ((EntityLivingBase) e).removePotionEffect(Potion.poison.getId());
	}

	double waterDamage = 0.1 * countWater;
	double fireDamage = 0.6 * countFire;
	double arcaneDamage = 1 * countArcane;
	double lightningDamage = 0.8 * countLightning;
	double earthDamage = 1 * countEarth;
	double iceDamage = 0.5 * countIce;
	double coldDamage = 0.4 * countCold;
	double steamDamage = 0.3 * countSteam;
	double lifeHeal = 1 * countLife;

	if (e instanceof EntityBlaze || e instanceof EntityMagmaCube)
	{
	    waterDamage = 1 * countWater;
	    fireDamage = 0;
	    arcaneDamage = 0.5 * countArcane;
	    iceDamage = 0.2 * countIce;
	    coldDamage = 1.4 * countCold;
	    steamDamage = 0.6 * countSteam;
	}
	if (e instanceof EntitySnowman)
	{
	    lifeHeal += coldDamage;
	    coldDamage = 0;
	    fireDamage *= 2;
	}
	if (e instanceof EntityEnderman)
	{
	    waterDamage *= 2;
	    fireDamage /= 2;
	    arcaneDamage /= 2;
	    earthDamage /= 2;
	    iceDamage /= 2;
	    coldDamage *= 2;
	    lightningDamage /= 2;
	    steamDamage *= 2;
	    lifeHeal /= 2;
	}
	if (isEntityWet) lightningDamage *= 2;

	waterDamage *= mod.waterMod;
	fireDamage *= mod.fireMod;
	arcaneDamage *= mod.arcaneMod;
	lightningDamage *= mod.lightningMod;
	earthDamage *= mod.earthMod;
	iceDamage *= mod.iceMod;
	coldDamage *= mod.coldMod;
	steamDamage *= mod.steamMod;
	lifeHeal *= mod.lifeMod;

	if (e instanceof EntityLivingBase && ((EntityLivingBase) e).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
	{
	    double f = lifeHeal;
	    lifeHeal = arcaneDamage;
	    arcaneDamage = f;
	}

	if (!recentlyAffected.containsKey(e))
	{
	    e.attackEntityFrom(
		    getCaster() instanceof EntityLivingBase ? DamageSource.causeMobDamage((EntityLivingBase) getCaster())
			    : DamageSource.magic, (float) (waterDamage + fireDamage + arcaneDamage + lightningDamage
			    + earthDamage + iceDamage + coldDamage + steamDamage));
	    if (lifeHeal > 0 && e instanceof EntityLivingBase)
	    {
		((EntityLivingBase) e).heal((float) lifeHeal);
	    }
	    recentlyAffected.put(e, 20);
	}
    }

    public NBTTagCompound getStaffTag()
    {
	return (NBTTagCompound) additionalData.getTag("Staff");
    }

    public NBTTagCompound writeToNBT()
    {
	NBTTagCompound tag = new NBTTagCompound();
	tag.setTag("Addition", additionalData);

	String elementsString = "";
	for (Element e : elements)
	{
	    elementsString = elementsString.concat(String.valueOf(e.ordinal()));
	}
	String data = elementsString + ";" + dimensionID + ";" + casterUUID.toString() + ";" + castType.toString();
	tag.setString("Data", data);

	return tag;
    }

    public static Spell createFromNBT(NBTTagCompound tag)
    {
	String[] datas = tag.getString("Data").split(";");

	List<Element> l = new ArrayList();
	for (int a = 0; a < datas[0].length(); a++)
	{
	    int ordinal = Integer.parseInt(String.valueOf(datas[0].charAt(a)));
	    l.add(Element.values()[ordinal]);
	}

	int dID = Integer.parseInt(datas[1]);
	UUID eUUID = UUID.fromString(datas[2]);
	CastType cast = CastType.valueOf(datas[3]);

	NBTTagCompound addi = tag.getCompoundTag("Addition");

	return new Spell(l, dID, eUUID, cast, addi);
    }

    public static NBTTagCompound createAdditionalInfo(ItemStack staffIS)
    {
	NBTTagCompound tag = new NBTTagCompound();
	NBTTagCompound staff = new NBTTagCompound();
	if (staffIS != null && staffIS.getItem() instanceof ItemStaff)
	{
	    staff = ((ItemStaff) staffIS.getItem()).getStaffTag(staffIS);
	}
	tag.setTag("Staff", staff);
	return tag;
    }

    @Override
    public boolean equals(Object obj)
    {
	if (obj instanceof Spell)
	{
	    Spell o = (Spell) obj;
	    return o.dimensionID == dimensionID && o.casterUUID.equals(casterUUID) && castType == o.castType
		    && elements.equals(o.elements);
	}
	return false;
    }

    @Override
    public int hashCode()
    {
	int i = 0;
	for (Element e : elements)
	{
	    i += e.ordinal();
	}
	return (int) (dimensionID + casterUUID.getLeastSignificantBits() * casterUUID.getMostSignificantBits() + i);
    }
}
