package com.williameze.minegicka3;

import net.minecraft.potion.Potion;

public class PotionCustom extends Potion
{
    public PotionCustom(int id, boolean isBadEffect, int color)
    {
	super(id, isBadEffect, color);
    }

    public Potion setCustomIconIndex(int i, int j)
    {
	return super.setIconIndex(i, j);
    }
}
