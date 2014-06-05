package com.williameze.minegicka3.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import org.apache.commons.lang3.math.NumberUtils;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.magicks.Magick;
import com.williameze.minegicka3.main.objects.ItemStaff;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class PlayerData
{
    public EntityPlayer ref;

    public String playerName;
    public int dimensionID;
    public double maxMana;
    public double mana;
    public Set<Element> unlocked = new HashSet<Element>();
    public Set<Magick> unlockedMagicks = new HashSet<Magick>();

    public PlayerData()
    {
	mana = maxMana = 1000;
	unlocked.add(Element.Ice);
	unlocked.add(Element.Steam);
    }

    public PlayerData(EntityPlayer p)
    {
	this();
	ref = p;
	playerName = p.getGameProfile().getName();
	dimensionID = p.worldObj.provider.dimensionId;
    }

    public void recoverMana()
    {
	if (mana < maxMana)
	{
	    double recover = 1 / 6D;
	    ItemStack is = ref == null ? null : ref.getCurrentEquippedItem();
	    if (is != null && is.getItem() instanceof ItemStaff)
	    {
		recover *= ((ItemStaff) is.getItem()).getRecover(is);
	    }
	    shiftMana(recover);
	}
    }

    public void shiftMana(double d)
    {
	mana += d;
	mana = Math.max(Math.min(maxMana, mana), 0);
    }

    public void unlockEverything()
    {
	unlocked.addAll(Arrays.asList(Element.values()));
	unlockedMagicks.addAll(Magick.magicks.values());
    }

    public void unlock(Object o)
    {
	if (o instanceof Element)
	{
	    unlocked.add((Element) o);
	}
	if (o instanceof Magick)
	{
	    unlockedMagicks.add((Magick) o);
	}
    }

    public boolean isUnlocked(Object o)
    {
	if (ref != null && ref.capabilities.isCreativeMode)
	{
	    return true;
	}
	if (o instanceof Element)
	{
	    return unlocked.contains((Element) o);
	}
	if (o instanceof Magick)
	{
	    return unlockedMagicks.contains((Magick) o);
	}
	return false;
    }

    public void loadPlayerRef()
    {
	if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
	{
	    loadPlayerRefFromID(ModBase.proxy.getClientWorld());
	}
	else if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
	{
	    loadPlayerRefFromServer(FMLCommonHandler.instance().getMinecraftServerInstance());
	}
    }

    public void loadPlayerRefFromServer(MinecraftServer server)
    {
	World w = server.worldServerForDimension(dimensionID);
	loadPlayerRefFromID(w);
    }

    public void loadPlayerRefFromID(World w)
    {
	for (int a = 0; a < w.playerEntities.size(); a++)
	{
	    EntityPlayer e = (EntityPlayer) w.playerEntities.get(a);
	    if (e.getGameProfile().getName().equals(playerName))
	    {
		ref = e;
		return;
	    }
	}
    }

    public String dataToString()
    {
	String s = "";
	s += dimensionID + ";" + playerName + ";" + maxMana + ";" + mana + ";";
	for (Element e : unlocked)
	{
	    s += String.valueOf(e.ordinal());
	}
	s += ";!";
	for (Magick m : unlockedMagicks)
	{
	    s += String.valueOf(m.getID()) + "!";
	}
	s += ";";
	return s;
    }

    public void dataFromString(String s)
    {
	String[] ss = s.split(";");
	dimensionID = Integer.parseInt(ss[0]);
	playerName = ss[1];
	maxMana = Double.parseDouble(ss[2]);
	mana = Double.parseDouble(ss[3]);
	for (char c : ss[4].toCharArray())
	{
	    if (Character.isDigit(c))
	    {
		unlock(Element.values()[Integer.parseInt(String.valueOf(c))]);
	    }
	}
	String unlockedMagicksString = ss[5];
	String[] unlockedMagicksID = unlockedMagicksString.split("!");
	for (String sm : unlockedMagicksID)
	{
	    if (sm == null || sm == "" || sm.length() < 1) continue;
	    try
	    {
		unlock(Magick.getMagickFromID(Integer.parseInt(sm)));
	    }
	    catch (NumberFormatException e)
	    {
		throw e;
	    }
	}
	loadPlayerRef();
    }

    public static PlayerData stringToData(String s)
    {
	PlayerData pd = new PlayerData();
	pd.dataFromString(s);
	return pd;
    }

    @Override
    public int hashCode()
    {
	return playerName.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
	return obj instanceof PlayerData && ((PlayerData) obj).playerName.equals(playerName);
    }
}
