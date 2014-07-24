package com.williameze.minegicka3.mechanics;

import java.awt.Color;

import net.minecraft.util.EnumChatFormatting;

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

    public String getTextColor()
    {
	if (this == Element.Arcane) return EnumChatFormatting.DARK_RED + "";
	else if (this == Element.Cold) return EnumChatFormatting.WHITE + "";
	else if (this == Element.Earth) return EnumChatFormatting.GOLD + "";
	else if (this == Element.Fire) return EnumChatFormatting.RED + "";
	else if (this == Element.Ice) return EnumChatFormatting.AQUA + "";
	else if (this == Element.Life) return EnumChatFormatting.GREEN + "";
	else if (this == Element.Lightning) return EnumChatFormatting.LIGHT_PURPLE + "";
	else if (this == Element.Shield) return EnumChatFormatting.YELLOW + "";
	else if (this == Element.Steam) return EnumChatFormatting.DARK_GRAY + "";
	else if (this == Element.Water) return EnumChatFormatting.BLUE + "";
	else return "";
    }

    public static Color[] colors = new Color[] { new Color(255, 0, 0), new Color(255, 255, 255), new Color(56, 39, 19), new Color(255, 75, 0),
	    new Color(144, 255, 255), new Color(0, 255, 0), new Color(255, 84, 253), new Color(255, 246, 56), new Color(171, 171, 171),
	    new Color(37, 41, 255) };

    public Color getColor()
    {
	if (this == Arcane) return colors[0];
	else if (this == Cold) return colors[1];
	else if (this == Earth) return colors[2];
	else if (this == Fire) return colors[3];
	else if (this == Ice) return colors[4];
	else if (this == Life) return colors[5];
	else if (this == Lightning) return colors[6];
	else if (this == Shield) return colors[7];
	else if (this == Steam) return colors[8];
	else return colors[9];
    }
}
