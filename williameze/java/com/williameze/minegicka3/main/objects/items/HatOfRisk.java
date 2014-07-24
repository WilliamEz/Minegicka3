package com.williameze.minegicka3.main.objects.items;

import java.util.List;

import com.williameze.minegicka3.mechanics.SpellDamageModifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class HatOfRisk extends Hat
{
    public HatOfRisk()
    {
	super();
	wearerIncomeDamageModifier = new SpellDamageModifier(2).setModifiers("3l");
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean par4)
    {
	super.addInformation(is, p, l, par4);
	l.add("x2 damage and x3 healing of all incoming spells.");
    }
}
