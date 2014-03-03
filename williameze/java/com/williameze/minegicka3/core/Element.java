package com.williameze.minegicka3.core;

import net.minecraft.util.ResourceLocation;

import com.williameze.minegicka3.bridges.Values;

public enum Element
{
    Arcane, Cold, Earth, Fire, Ice, Life, Lightning, Shield, Steam, Water;

    public int getTextureIndex(boolean disabled)
    {
	return ordinal() * 2 + (disabled ? 1 : 0);
    }
    
    public static boolean areOpposite(Element el1, Element el2)
    {
	return el1.isOpposite(el2);
    }

    public static Element combineTogether(Element el1, Element el2)
    {
	return el1.combineWith(el2);
    }
    
    public static Element breakDown(Element el1, Element el2)
    {
	return el1.breakDown(el2);
    }

    public boolean isOpposite(Element el)
    {
	switch (this)
	{
	    case Arcane:
		return el == Life;
	    case Cold:
		return el == Fire;
	    case Earth:
		return el == Lightning;
	    case Fire:
		return el == Cold;
	    case Life:
		return el == Arcane;
	    case Lightning:
		return el == Earth || el == Water;
	    case Shield:
		return el == Shield;
	    case Water:
		return el == Lightning;
	    default:
		return false;
	}
    }

    public Element combineWith(Element el)
    {
	switch (this)
	{
	    case Water:
		if (el == Cold) return Ice;
		if (el == Fire) return Steam;
		return null;
	    case Fire:
		if (el == Water) return Steam;
		return null;
	    case Cold:
		if (el == Water) return Ice;
		return null;
	    default:
		return null;
	}
    }
    
    public Element breakDown(Element el)
    {
	switch (this)
	{
	    case Fire:
		if (el == Ice) return Water;
		return null;
	    case Cold:
		if (el == Steam) return Water;
		return null;
	    default:
		return null;
	}
    }
}
