package com.williameze.minegicka3.main.objects.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemHatOfRisk extends ItemHat
{
    public ItemHatOfRisk()
    {
	super();
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean par4)
    {
        super.addInformation(is, p, l, par4);
        l.add("x2 damage and x3 healing of all incoming spells.");
    }
}
