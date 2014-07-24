package com.williameze.minegicka3.mechanics.spells;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.magicks.Magick;
import com.williameze.minegicka3.mechanics.spells.Spell.CastType;

public class TemplateSpell
{
    public boolean isMagick;

    public List<Element> combination;
    public CastType castType;

    public Magick magick;

    public TemplateSpell(Magick m)
    {
	isMagick = true;
	magick = m;
    }

    public TemplateSpell(CastType type, Object... os)
    {
	isMagick = false;
	castType = type;
	combination = new ArrayList();
	setCombination(os);
    }

    public TemplateSpell setCombination(Object... os)
    {
	Element e = null;
	int count = 1;
	for (int a = 0; a < os.length; a++)
	{
	    if (os[a] instanceof Element)
	    {
		if (e != null)
		{
		    for (int b = 0; b < count; b++)
		    {
			combination.add(e);
		    }
		}
		e = (Element) os[a];
		count = 1;
	    }
	    else if (os[a] instanceof Integer)
	    {
		count = (Integer) os[a];
	    }
	    if (a == os.length - 1)
	    {
		if (e != null)
		{
		    for (int b = 0; b < count; b++)
		    {
			combination.add(e);
		    }
		}
	    }
	}
	return this;
    }

    public Spell toSpell(Entity e, NBTTagCompound addi)
    {
	UUID uuid = e.getUniqueID();
	String name = e.getCommandSenderName();
	if (e instanceof EntityPlayer)
	{
	    name = ((EntityPlayer) e).getGameProfile().getName();
	}
	int dim = e.worldObj.provider.dimensionId;

	Spell spell = new Spell(combination, dim, uuid, name, castType, addi);
	return spell;
    }

    public void castMagick()
    {

    }

    @Override
    public int hashCode()
    {
	String hash = "";
	if (isMagick)
	{
	    hash = "1" + magick.getID();
	}
	else
	{
	    hash = "0" + castType.ordinal() + "";
	    for (int a = 0; a < combination.size(); a++)
	    {
		hash = combination.get(a).ordinal() + hash;
	    }
	}
	return Integer.parseInt(hash);
    }

    @Override
    public boolean equals(Object obj)
    {
	if (obj instanceof TemplateSpell)
	{
	    if (((TemplateSpell) obj).isMagick == isMagick)
	    {
		if (isMagick)
		{
		    if (((TemplateSpell) obj).magick == magick)
		    {
			return true;
		    }
		}
		else if (((TemplateSpell) obj).castType == castType)
		{
		    if (((TemplateSpell) obj).combination != null && combination == null)
		    {
			return false;
		    }
		    else if (combination.equals(((TemplateSpell) obj).combination))
		    {
			return true;
		    }
		}
	    }
	}
	return false;
    }
}
