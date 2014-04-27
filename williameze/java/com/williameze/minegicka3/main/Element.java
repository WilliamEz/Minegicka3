package com.williameze.minegicka3.main;

import java.awt.Color;

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

    public Color getColor()
    {
	if (this == Arcane) return new Color(255, 0, 0);
	else if (this == Cold) return new Color(255, 255, 255);
	else if (this == Earth) return new Color(80, 60, 27);
	else if (this == Fire) return new Color(255, 100, 0);
	else if (this == Ice) return new Color(144, 255, 255);
	else if (this == Life) return new Color(0, 255, 0);
	else if (this == Lightning) return new Color(255, 84, 253);
	else if (this == Shield) return new Color(255, 246, 56);
	else if (this == Steam) return new Color(171, 171, 171);
	else return new Color(37, 41, 255);
    }
}
