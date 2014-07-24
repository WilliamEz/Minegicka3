package com.williameze.minegicka3.main.objects.items;

import java.util.List;

import com.williameze.minegicka3.main.entities.monsters.Entity888;
import com.williameze.minegicka3.mechanics.SpellDamageModifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class HatOfResistance extends Hat
{
    public HatOfResistance()
    {
	super();
	wearerIncomeDamageModifier = Entity888.spellResistance.copy();
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean par4)
    {
	super.addInformation(is, p, l, par4);
	l.add("x0.5 damage and x4 healing of all incoming spells.");
    }
}
