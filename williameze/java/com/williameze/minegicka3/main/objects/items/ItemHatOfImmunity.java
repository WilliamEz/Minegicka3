package com.williameze.minegicka3.main.objects.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ItemHatOfImmunity extends ItemHat
{
    public ItemHatOfImmunity()
    {
	super();
    }

    @Override
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
	return EnumRarity.epic;
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean par4)
    {
	super.addInformation(is, p, l, par4);
	l.add("Grants immunity to spells' damage/healing.");
	l.add(EnumChatFormatting.RED + "" + EnumChatFormatting.ITALIC + "I'm unobtainable!");
    }
}
