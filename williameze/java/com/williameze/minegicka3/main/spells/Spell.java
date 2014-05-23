package com.williameze.minegicka3.main.spells;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;

import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.core.PlayerData;
import com.williameze.minegicka3.core.PlayersData;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.SpellDamageModifier;
import com.williameze.minegicka3.main.objects.ItemStaff;

public class Spell
{
    public static Random rnd = new Random();
    public static Spell none = new Spell(new ArrayList(), 0, null, CastType.Single, null);

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
    public boolean toBeInvalidated;

    public Entity caster;
    public Map<Entity, Integer> recentlyAffected = new HashMap();

    public Spell(List<Element> l, int dimID, UUID entityID, CastType type, NBTTagCompound addi)
    {
	toBeInvalidated = false;
	spellTicks = 0;
	setElements(l);
	dimensionID = dimID;
	casterUUID = entityID;
	castType = type;
	additionalData = addi;
    }

    public void setElements(List<Element> l)
    {
	elements.clear();
	elements.addAll(l);
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
    }

    public SpellExecute getExecute()
    {
	return SpellExecute.getSpellExecute(this);
    }

    public Entity getCaster()
    {
	if (caster == null)
	{
	    caster = CoreBridge.instance().getEntityByUUID(dimensionID, casterUUID);
	}
	return caster;
    }

    public boolean hasAllElements(Element... es)
    {
	List<Element> list = Arrays.asList(es);
	for (Element e1 : elements)
	{
	    if (list.contains(e1)) list.remove(e1);
	}
	return list.isEmpty();
    }

    public boolean hasEitherElement(Element... es)
    {
	List<Element> list = Arrays.asList(es);
	for (Element e1 : elements)
	{
	    if (list.contains(e1)) return true;
	}
	return false;
    }

    public boolean hasElement(Element e)
    {
	for (Element e1 : elements)
	{
	    if (e1 == e) return true;
	}
	return false;
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
	if (!toBeInvalidated)
	{
	    spellTicks++;
	    getExecute().updateSpell(this);

	    updateRecentAffected();
	}
    }

    public void stopSpell()
    {
	toBeInvalidated = true;
	getExecute().stopSpell(this);
    }

    public void updateRecentAffected()
    {
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

    public void damageEntity(Entity e, int cooldownTime)
    {
	damageEntity(e, cooldownTime, SpellDamageModifier.defau);
    }

    public void damageEntity(Entity e, int cooldownTime, SpellDamageModifier mod)
    {
	if (e.worldObj.isRemote) return;
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
	    if (countCold + countWater + countSteam > 0) be.addPotionEffect(new PotionEffect(Potion.fireResistance.getId(), (countCold
		    + countWater + countSteam) * 20, countCold + countWater + countSteam));
	    if (countLife > 0) ((EntityLivingBase) e).removePotionEffect(Potion.poison.getId());
	}

	double waterDamage = 0.0 * countWater;
	double fireDamage = 0.6 * countFire;
	double arcaneDamage = 1 * countArcane;
	double lightningDamage = 0.8 * countLightning;
	double earthDamage = 1 * countEarth;
	double iceDamage = 0.5 * countIce;
	double coldDamage = 0.4 * countCold;
	double steamDamage = 0.3 * countSteam;
	double lifeHeal = 1 * countLife;

	if (e instanceof EntityBlaze || e instanceof EntityMagmaCube || e instanceof EntityPigZombie)
	{
	    waterDamage = 1 * countWater;
	    fireDamage = 0;
	    arcaneDamage = 0.5 * countArcane;
	    iceDamage = 0.2 * countIce;
	    coldDamage = 1.2 * countCold;
	    steamDamage = 0.5 * countSteam;
	}
	if (e instanceof EntitySnowman)
	{
	    lifeHeal += coldDamage;
	    coldDamage = 0;
	    fireDamage *= 2;
	}
	if (e instanceof EntityEnderman)
	{
	    waterDamage = 2 * countWater;
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
	if (e.isImmuneToFire())
	{
	    fireDamage = 0;
	}

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
	    float totalDamage = (float) (waterDamage + fireDamage + arcaneDamage + lightningDamage + earthDamage + iceDamage + coldDamage + steamDamage);
	    totalDamage*=getPower();
	    if (totalDamage > 0)
	    {
		DamageSource source = getCaster() instanceof EntityLivingBase ? DamageSource.causeMobDamage((EntityLivingBase) getCaster())
			: DamageSource.magic;
		if (e instanceof EntityDragon)
		{
		    EntityDragon dr = (EntityDragon) e;
		    dr.attackEntityFromPart(dr.dragonPartHead, source, totalDamage);
		}
		else e.attackEntityFrom(source, totalDamage);
	    }
	    if (lifeHeal > 0 && e instanceof EntityLivingBase)
	    {
		((EntityLivingBase) e).heal((float) lifeHeal);
	    }
	    if (e instanceof EntityCreeper && lightningDamage >= 3.2 && rnd.nextInt(4) == 0)
	    {
		e.getDataWatcher().updateObject(17, Byte.valueOf((byte) 1));
	    }
	    if (e instanceof EntityPig && lightningDamage >= 3.8 && rnd.nextInt(4) == 0)
	    {
		e.onStruckByLightning(new EntityLightningBolt(e.worldObj, e.posX, e.posY, e.posZ));
	    }
	    if (cooldownTime > 0) recentlyAffected.put(e, (int) (cooldownTime / getAtkSpeed()));
	}
    }

    public double consumeMana(double m, boolean reallyConsume, boolean mustHaveMoreMana, int showChatMessage)
    {
	Entity e = getCaster();
	if (e instanceof EntityPlayer)
	{
	    EntityPlayer p = (EntityPlayer) e;
	    if (p.capabilities.isCreativeMode) return 1;
	    PlayersData psd = PlayersData.getWorldPlayersData(p.worldObj);
	    PlayerData pd = psd.getPlayerData(p);

	    double canConsume = Math.min(m, pd.mana);
	    if (mustHaveMoreMana && pd.mana < m)
	    {
		canConsume = 0;
	    }
	    boolean nope = pd.mana < m;
	    if (canConsume > 0)
	    {
		if (reallyConsume)
		{
		    pd.mana -= canConsume;
		    if (!p.worldObj.isRemote) psd.sendPlayerManaToClient(p, p);
		}
	    }
	    if (nope)
	    {
		if (!p.worldObj.isRemote)
		{
		    if (showChatMessage == 1)
		    {
			p.addChatMessage(new ChatComponentText("Mana too low.").setChatStyle(new ChatStyle().setItalic(true).setColor(
				EnumChatFormatting.RED)));
		    }
		    else if (showChatMessage == 2)
		    {
			p.addChatMessage(new ChatComponentText("Requires " + (int) (Math.round(m * 10) / 10) + " mana.")
				.setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.RED)));
		    }
		    else if (showChatMessage == 3)
		    {
			p.addChatMessage(new ChatComponentText("Requires " + (int) (Math.round(m * 10) / 10)
				+ " mana. Shame you only have " + (int) (Math.round(pd.mana * 10) / 10) + " mana.")
				.setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.RED)));
		    }
		}
	    }

	    return canConsume / m;
	}
	return 1;
    }

    public NBTTagCompound getStaffTag()
    {
	return (NBTTagCompound) additionalData.getTag("Staff");
    }

    public double getManaConsumeRate()
    {
	return getStaffTag().getDouble("Consume");
    }

    public double getPower()
    {
	return getStaffTag().getDouble("Power");
    }

    public double getManaRechargeRate()
    {
	return getStaffTag().getDouble("Recharge");
    }

    public double getAtkSpeed()
    {
	return getStaffTag().getDouble("ATKSpeed");
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

    public static NBTTagCompound createAdditionalInfo(EntityPlayer p)
    {
	NBTTagCompound tag = new NBTTagCompound();
	NBTTagCompound staff = new NBTTagCompound();
	ItemStack staffIS = p.getCurrentEquippedItem();
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
	    return o.dimensionID == dimensionID && o.casterUUID.equals(casterUUID) && castType == o.castType && elements.equals(o.elements);
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
