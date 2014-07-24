package com.williameze.minegicka3.mechanics.magicks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.objects.items.Staff;
import com.williameze.minegicka3.main.packets.PacketStartMagick;
import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.spells.Spell;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public abstract class Magick
{
    public static Random rnd = new Random();
    private static int currentMagickID = 0;
    private static boolean useLangName = false;
    public static Map<Integer, Magick> magicks = new HashMap();

    public static Magick getMagickFromID(int id)
    {
	return magicks.get((Integer) id);
    }

    public static Magick getMatchingMagick(List<Element> l)
    {
	for (Magick m : magicks.values())
	{
	    if (m.isCombinationCorrect(l)) return m;
	}
	return null;
    }

    public static Element[] stringToElements(String s)
    {
	String s1 = "";
	for (char c : s.toCharArray())
	{
	    if (Character.isLetter(c)) s1 += Character.toString(Character.toLowerCase(c));
	}
	Element[] es = new Element[s1.length()];
	for (int a = 0; a < s1.length(); a++)
	{
	    char c = s1.charAt(a);
	    if (c == 'a') es[a] = Element.Arcane;
	    else if (c == 'c') es[a] = Element.Cold;
	    else if (c == 'd') es[a] = Element.Shield;
	    else if (c == 'e') es[a] = Element.Earth;
	    else if (c == 'f') es[a] = Element.Fire;
	    else if (c == 'h') es[a] = Element.Lightning;
	    else if (c == 'i') es[a] = Element.Ice;
	    else if (c == 'l') es[a] = Element.Life;
	    else if (c == 's') es[a] = Element.Steam;
	    else if (c == 'w') es[a] = Element.Water;
	}
	return es;
    }

    public final int id;
    public String name;
    public String unlocalizedName;
    public Element[] combination;
    public boolean craftable;

    /** D indicates Shield, H indicates Lightning **/
    public Magick(String display, String s)
    {
	this(display, stringToElements(s));
    }

    public Magick(String display, Element... elements)
    {
	id = currentMagickID++;
	name = display;
	unlocalizedName = "minegicka3.magicks." + name + ".name";
	combination = elements;
	magicks.put(new Integer(id), this);
	craftable = true;
    }

    public int getID()
    {
	return id;
    }

    public String getUnlocalizedName()
    {
	return unlocalizedName;
    }

    public String getDisplayName()
    {
	if (useLangName) return StatCollector.translateToLocal(getUnlocalizedName());

	return name;
    }

    public Element[] getCombination()
    {
	return combination;
    }

    public abstract List<String> getDescription();

    public boolean isCombinationCorrect(List<Element> l)
    {
	return isCombinationCorrect(l.toArray(new Element[0]));
    }

    public boolean isCombinationCorrect(Element[] es)
    {
	return Arrays.deepEquals(combination, es);
    }

    public boolean clientSendMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	if (additionalData == null) additionalData = new NBTTagCompound();
	addDataToMagick(world, x, y, z, caster, additionalData);

	Spell.none.setCaster(caster);
	double[] props = getStaffMainProperties(additionalData);
	double manaCost = getBaseManaCost() * props[2];
	boolean haveEnoughMana = Spell.none.consumeMana(manaCost, false, true, 3) >= 1;
	Spell.none.setCaster(null);
	if (haveEnoughMana)
	{
	    ModBase.packetPipeline.sendToServer(new PacketStartMagick(this, world, x, y, z, caster.getPersistentID(),
		    caster instanceof EntityPlayer ? ((EntityPlayer) caster).getGameProfile().getName() : null, additionalData));
	    return true;
	}
	return false;
    }

    public void clientReceivedMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	activateMagick(world, x, y, z, caster, additionalData);
    }

    public void serverSendMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	TargetPoint point = new TargetPoint(world.provider.dimensionId, x, y, z, Values.magickUpdateRange);
	ModBase.packetPipeline.sendToAllAround(new PacketStartMagick(this, world, x, y, z, caster.getPersistentID(),
		caster instanceof EntityPlayer ? ((EntityPlayer) caster).getGameProfile().getName() : null, additionalData), point);
    }

    public void serverReceivedMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	serverSendMagick(world, x, y, z, caster, additionalData);
	activateMagick(world, x, y, z, caster, additionalData);
    }

    public void addDataToMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	Staff istaff = (Staff) ModBase.staff;
	ItemStack isstaff = new ItemStack(istaff);
	if (caster instanceof EntityLivingBase)
	{
	    ItemStack is = ((EntityLivingBase) caster).getHeldItem();
	    if (is != null && is.getItem() instanceof Staff)
	    {
		istaff = (Staff) is.getItem();
		isstaff = is;
	    }
	}
	NBTTagCompound staffTag = istaff.getStaffTag(isstaff);
	additionalData.setTag("Staff", staffTag);
    }

    /**
     * 1st is power, 2nd is atkspeed, 3rd is consume rate, 4th is mana recovering rate
     **/
    public double[] getStaffMainProperties(NBTTagCompound additionalData)
    {
	double[] d = new double[4];
	NBTTagCompound staffTag = additionalData.getCompoundTag("Staff");
	d[0] = staffTag.getDouble("Power");
	d[1] = staffTag.getDouble("ATKSpeed");
	d[2] = staffTag.getDouble("Consume");
	d[3] = staffTag.getDouble("Recover");
	return d;
    }

    public void activateMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	caster = getPossibleCaster(world, x, y, z, caster);

	//Spell.none.setCaster(caster);
	double[] props = getStaffMainProperties(additionalData);
	double manaCost = getBaseManaCost() * props[2];
	Spell.consumeMana(caster, manaCost, true, true, 0);
	//Spell.none.setCaster(null);
	doTheMagick(world, x, y, z, caster, additionalData);
    }

    public abstract void doTheMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData);

    public abstract double getBaseManaCost();

    public Entity getPossibleCaster(World world, double x, double y, double z, Entity caster)
    {
	if (caster != null) return caster;
	else
	{
	    List<Entity> l = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(4, 4, 4));
	    return FuncHelper.getEntityClosestTo(x, y, z, l);
	}
    }

    public Object[] getUnlockMaterials()
    {
	List l = new ArrayList();
	if (getBasicUnlockMaterials() != null) l.addAll(Arrays.asList(getBasicUnlockMaterials()));
	if (getElementUnlockMaterials() != null) l.addAll(Arrays.asList(getElementUnlockMaterials()));
	if (getAdditionalUnlockMaterials() != null) l.addAll(Arrays.asList(getAdditionalUnlockMaterials()));
	return l.toArray();
    }

    public Object[] getBasicUnlockMaterials()
    {
	return null;
	//return new Object[] { ModBase.stick, 4, Blocks.stone, 4 };
    }

    public Object[] getElementUnlockMaterials()
    {
	List l = new ArrayList();
	for (Element e : combination)
	{
	    Item i = null;
	    switch (e)
	    {
		case Arcane:
		    i = ModBase.essenceArcane;
		    break;
		case Cold:
		    i = ModBase.essenceCold;
		    break;
		case Earth:
		    i = ModBase.essenceEarth;
		    break;
		case Fire:
		    i = ModBase.essenceFire;
		    break;
		case Ice:
		    i = ModBase.essenceIce;
		    break;
		case Life:
		    i = ModBase.essenceLife;
		    break;
		case Lightning:
		    i = ModBase.essenceLightning;
		    break;
		case Shield:
		    i = ModBase.essenceShield;
		    break;
		case Steam:
		    i = ModBase.essenceSteam;
		    break;
		case Water:
		    i = ModBase.essenceWater;
		    break;
	    }
	    if (i != null) l.add(i);
	}
	return l.toArray();
    }

    public abstract Object[] getAdditionalUnlockMaterials();
}
