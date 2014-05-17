package com.williameze.minegicka3.main.magicks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.objects.ItemStaff;
import com.williameze.minegicka3.main.packets.PacketStartMagick;
import com.williameze.minegicka3.main.spells.Spell;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public abstract class Magick
{
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
	    if (Character.isAlphabetic(c)) s1 += Character.toString(Character.toLowerCase(c));
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

    public int id;
    public String displayID;
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
	displayID = display;
	unlocalizedName = "minegicka3.magicks." + displayID + ".name";
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

	return displayID;
    }

    public Element[] getCombination()
    {
	return combination;
    }

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

	Spell.none.caster = caster;
	double[] props = getStaffMainProperties(additionalData);
	double manaCost = getBaseManaCost() * props[2];
	boolean haveEnoughMana = Spell.none.consumeMana(manaCost, false, true, 3) >= 1;
	Spell.none.caster = null;
	if (haveEnoughMana)
	{
	    ModBase.packetPipeline.sendToServer(new PacketStartMagick(this, world, x, y, z, caster.getPersistentID(), additionalData));
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
	ModBase.packetPipeline.sendToAllAround(new PacketStartMagick(this, world, x, y, z, caster.getPersistentID(), additionalData), point);
    }

    public void serverReceivedMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	serverSendMagick(world, x, y, z, caster, additionalData);
	activateMagick(world, x, y, z, caster, additionalData);
    }

    public void addDataToMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	ItemStaff istaff = (ItemStaff) ModBase.staff;
	ItemStack isstaff = new ItemStack(istaff);
	if (caster instanceof EntityLivingBase)
	{
	    ItemStack is = ((EntityLivingBase) caster).getHeldItem();
	    if (is != null && is.getItem() instanceof ItemStaff)
	    {
		istaff = (ItemStaff) is.getItem();
		isstaff = is;
	    }
	}
	NBTTagCompound staffTag = istaff.getStaffTag(isstaff);
	additionalData.setTag("Staff", staffTag);
    }

    /**
     * 1st is power, 2nd is atkspeed, 3rd is consume rate, 4th is mana
     * recovering rate
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

	Spell.none.caster = caster;
	double[] props = getStaffMainProperties(additionalData);
	double manaCost = getBaseManaCost() * props[2];
	Spell.none.consumeMana(manaCost, true, true, 0);
	Spell.none.caster = null;
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

    public Object[] getCraftClickTabletRecipe()
    {
	List l = new ArrayList();
	if (getBasicCraftClickTabletMaterials() != null) l.addAll(Arrays.asList(getBasicCraftClickTabletMaterials()));
	if (getElementCraftClickTabletMaterials() != null) l.addAll(Arrays.asList(getElementCraftClickTabletMaterials()));
	if (getAdditionalCraftClickTabletMaterials() != null) l.addAll(Arrays.asList(getAdditionalCraftClickTabletMaterials()));
	return l.toArray();
    }

    public Object[] getBasicCraftClickTabletMaterials()
    {
	return new Object[] { ModBase.stick, 4, Blocks.stone, 4 };
    }

    public Object[] getElementCraftClickTabletMaterials()
    {
	List l = new ArrayList();
	for (Element e : combination)
	{
	    Item i = null;
	    switch (e)
	    {
		case Arcane:
		    i = ModBase.stickArcane;
		    break;
		case Cold:
		    i = ModBase.stickCold;
		    break;
		case Earth:
		    i = ModBase.stickEarth;
		    break;
		case Fire:
		    i = ModBase.stickFire;
		    break;
		case Ice:
		    i = ModBase.stickIce;
		    break;
		case Life:
		    i = ModBase.stickLife;
		    break;
		case Lightning:
		    i = ModBase.stickLightning;
		    break;
		case Shield:
		    i = ModBase.stickShield;
		    break;
		case Steam:
		    i = ModBase.stickSteam;
		    break;
		case Water:
		    i = ModBase.stickWater;
		    break;
	    }
	    if (i != null) l.add(i);
	}
	return l.toArray();
    }

    public abstract Object[] getAdditionalCraftClickTabletMaterials();
}
