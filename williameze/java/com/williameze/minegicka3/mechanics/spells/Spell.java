package com.williameze.minegicka3.mechanics.spells;

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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.functional.CoreBridge;
import com.williameze.minegicka3.functional.PlayerData;
import com.williameze.minegicka3.functional.PlayersData;
import com.williameze.minegicka3.main.entities.IEntityMagicResistance;
import com.williameze.minegicka3.main.objects.items.Hat;
import com.williameze.minegicka3.main.objects.items.Staff;
import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.SpellDamageModifier;

public class Spell
{
    public static Random rnd = new Random();
    public static Spell none = new Spell(new ArrayList(), 0, null, "", CastType.Single, null);

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
    public String casterName = null;
    public CastType castType;
    public SpellType spellType;
    public NBTTagCompound additionalData;
    public boolean toBeInvalidated;
    public boolean alreadyStopped;

    private Entity caster;
    public Map<Entity, Integer> recentlyAffected = new HashMap();

    public Spell(List<Element> l, int dimID, UUID entityID, String entityName, CastType type, NBTTagCompound addi)
    {
	toBeInvalidated = false;
	spellTicks = 0;
	setElements(l);
	dimensionID = dimID;
	casterUUID = entityID;
	casterName = entityName;
	castType = type;
	additionalData = addi;
	if (additionalData == null) additionalData = new NBTTagCompound();
	toBeInvalidated = alreadyStopped = false;
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

    public void setCaster(Entity e)
    {
	if (e == null)
	{
	    caster = null;
	    casterUUID = new UUID(0, 0);
	    casterName = null;
	}
	else
	{
	    caster = e;
	    casterUUID = e.getPersistentID();
	    if (e instanceof EntityPlayer) casterName = ((EntityPlayer) e).getGameProfile().getName();
	    else casterName = null;
	}
    }

    public Entity getCaster()
    {
	if (caster == null)
	{
	    caster = CoreBridge.instance().getEntityFromArgs(casterUUID, dimensionID, casterName, true, false, true);
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
	    if (spellTicks >= 2000 || getCaster() != null && getCaster().isDead) stopSpell();
	}
    }

    public void stopSpell()
    {
	toBeInvalidated = true;
	if (!alreadyStopped) getExecute().stopSpell(this);
	alreadyStopped = true;
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

    public SpellDamageModifier getEntityResistance(Entity e)
    {
	SpellDamageModifier mod = new SpellDamageModifier();
	if (e instanceof EntityPlayer)
	{
	    EntityPlayer p = (EntityPlayer) e;
	    ItemStack hatIS = p.inventory.armorItemInSlot(0);
	    if (hatIS != null && hatIS.getItem() != null)
	    {
		Item hat = hatIS.getItem();
		if (hat instanceof Hat && ((Hat) hat).wearerIncomeDamageModifier != null)
		{
		    mod.multiply(((Hat) hat).wearerIncomeDamageModifier);
		}
	    }
	}
	if (e instanceof EntityLivingBase)
	{
	    EntityLivingBase living = (EntityLivingBase) e;
	    SpellDamageModifier potionModifier = new SpellDamageModifier(1);
	    PotionEffect arcane = living.getActivePotionEffect(ModBase.arcaneResistance);
	    PotionEffect cold = living.getActivePotionEffect(ModBase.coldResistance);
	    PotionEffect fire = living.getActivePotionEffect(Potion.fireResistance);
	    PotionEffect life = living.getActivePotionEffect(ModBase.lifeBoost);
	    PotionEffect lightning = living.getActivePotionEffect(ModBase.lightningResistance);
	    PotionEffect water = living.getActivePotionEffect(Potion.waterBreathing);
	    PotionEffect physic = living.getActivePotionEffect(Potion.resistance);

	    if (arcane != null) potionModifier.arcaneMod -= 0.15 * (arcane.getAmplifier() + 1);
	    if (cold != null)
	    {
		potionModifier.coldMod -= 0.15 * (cold.getAmplifier() + 1);
		potionModifier.iceMod -= 0.15 * (cold.getAmplifier() + 1);
	    }
	    if (fire != null)
	    {
		potionModifier.fireMod -= 0.15 * (fire.getAmplifier() + 1);
		potionModifier.steamMod -= 0.075 * (fire.getAmplifier() + 1);
	    }
	    if (life != null) potionModifier.lifeMod += 0.15 * (life.getAmplifier() + 1);
	    if (lightning != null) potionModifier.lightningMod -= 0.15 * (lightning.getAmplifier() + 1);
	    if (water != null)
	    {
		potionModifier.waterMod -= 0.15 * (water.getAmplifier() + 1);
		potionModifier.steamMod -= 0.075 * (water.getAmplifier() + 1);
	    }
	    if (physic != null) potionModifier.earthMod -= 0.15 * (physic.getAmplifier() + 1);

	    potionModifier.clampUnderZero();
	    mod = mod.multiply(potionModifier);
	}
	if (e instanceof IEntityMagicResistance)
	{
	    SpellDamageModifier resistance = ((IEntityMagicResistance) e).getSpellEffectiveness();
	    if (resistance != null) mod.multiply(resistance);
	}
	if (e.isImmuneToFire())
	{
	    mod.fireMod = 0;
	}
	return mod;
    }

    public void damageEntity(Entity e, int cooldownTime)
    {
	damageEntity(e, cooldownTime, SpellDamageModifier.defau);
    }

    public void damageEntity(Entity e, int cooldownTime, SpellDamageModifier mod)
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

	mod = mod.multiply(getEntityResistance(e)).multiply(getDamageModifier());

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

	if (!e.worldObj.isRemote)
	{
	    if (countFire > 0) e.setFire(countFire * 3);
	    if (isEntityWet) e.extinguish();
	    if (e instanceof EntityLivingBase)
	    {
		EntityLivingBase be = (EntityLivingBase) e;
		if (countCold > 0) be.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 20 * countCold, countCold - 1));
		if (countCold + countWater + countSteam > 0) be.addPotionEffect(new PotionEffect(Potion.fireResistance.getId(), (countCold
			+ countWater + countSteam) * 20, countCold + countWater + countSteam));
		if (countLife > 0) ((EntityLivingBase) e).removePotionEffect(Potion.poison.getId());
	    }

	    if (!recentlyAffected.containsKey(e))
	    {
		float totalDamage = (float) (waterDamage + fireDamage + arcaneDamage + lightningDamage + earthDamage + iceDamage + coldDamage + steamDamage);
		totalDamage *= getPower();
		if (totalDamage > 0)
		{
		    DamageSource source = getCaster() instanceof EntityLivingBase ? DamageSource.causeMobDamage((EntityLivingBase) getCaster())
			    .setMagicDamage() : DamageSource.magic;
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
	else
	{
	    if (!recentlyAffected.containsKey(e))
	    {
		if (lifeHeal > 0 && e instanceof EntityLivingBase)
		{
		    e.worldObj.spawnParticle("heart", e.posX, e.posY + e.getEyeHeight(), e.posZ, 0, 1, 0);
		}
		if (cooldownTime > 0) recentlyAffected.put(e, (int) (cooldownTime / getAtkSpeed()));
	    }
	}
    }

    public static double consumeMana(Entity caster, double m, boolean reallyConsume, boolean mustHaveMoreMana, int showChatMessage)
    {
	Spell.none.setCaster(caster);
	double d = Spell.none.consumeMana(m, reallyConsume, mustHaveMoreMana, showChatMessage);
	Spell.none.setCaster(null);
	return d;
    }

    public double consumeMana(double m, boolean reallyConsume, boolean mustHaveMoreMana, int showChatMessage)
    {
	Entity e = getCaster();
	if (e instanceof EntityPlayer)
	{
	    EntityPlayer p = (EntityPlayer) e;
	    if (p.capabilities.isCreativeMode) return 1;
	    if (p.worldObj.isRemote && ModBase.proxy.getClientPlayer() != p) return 1;
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
			p.addChatMessage(new ChatComponentText("That requires " + (int) (Math.round(m * 10) / 10) + " mana.")
				.setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.RED)));
		    }
		    else if (showChatMessage == 3)
		    {
			p.addChatMessage(new ChatComponentText("That requires " + (int) (Math.round(m * 10) / 10)
				+ " mana. It's a shame you only have " + (int) (Math.round(pd.mana * 10) / 10) + " mana.")
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
	NBTTagCompound tag = additionalData == null ? null : (NBTTagCompound) additionalData.getTag("Staff");
	if (tag == null) tag = ((Staff) ModBase.staff).getDefaultStaffTag();
	return tag;
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

    public SpellDamageModifier getDamageModifier()
    {
	return new SpellDamageModifier(getStaffTag().getString("Modifier"));
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
	String data = elementsString + ";";
	data += dimensionID + ";";
	data += (casterUUID == null ? "NAN" : casterUUID.toString()) + ";";
	data += (casterName == null || casterName.equals("") ? "@NAN#" : casterName) + ";";
	data += castType.toString();
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
	String uuidString = datas[2];
	UUID eUUID = uuidString.equals("NAN") ? null : UUID.fromString(datas[2]);
	String nameString = datas[3];
	String eName = nameString.equals("@NAN#") ? null : nameString;
	CastType cast = CastType.valueOf(datas[4]);

	NBTTagCompound addi = tag.getCompoundTag("Addition");

	return new Spell(l, dID, eUUID, eName, cast, addi);
    }

    public static NBTTagCompound createAdditionalInfo(EntityPlayer p)
    {
	NBTTagCompound tag = new NBTTagCompound();
	NBTTagCompound staff = new NBTTagCompound();
	ItemStack staffIS = p.getCurrentEquippedItem();
	if (staffIS != null && staffIS.getItem() instanceof Staff)
	{
	    staff = ((Staff) staffIS.getItem()).getStaffTag(staffIS);
	}
	tag.setTag("Staff", staff);
	return tag;
    }

    public boolean sameCaster(Spell s)
    {
	return s.getCaster() == getCaster() || s.casterUUID.equals(casterUUID);
    }

    @Override
    public boolean equals(Object obj)
    {
	if (obj instanceof Spell)
	{
	    Spell o = (Spell) obj;
	    return o.dimensionID == dimensionID && sameCaster(o) && castType == o.castType && elements.equals(o.elements);
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
	return (int) (dimensionID + (casterUUID != null ? casterUUID.getLeastSignificantBits() * casterUUID.getMostSignificantBits() : 0)
		+ (casterName != null ? casterName.hashCode() : 0) + i);
    }
}
