package com.williameze.minegicka3.main.objects.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.functional.PlayerData;
import com.williameze.minegicka3.functional.PlayersData;

public class ItemMagicApple extends ItemFood
{
    public double manaIncrease;

    public ItemMagicApple(double d)
    {
	super(4, (float) Math.pow(d / 16D, 0.2), false);
	setAlwaysEdible();
	manaIncrease = d;
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass)
    {
	return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack is)
    {
	if (is.getItem() == ModBase.magicApple) return EnumRarity.uncommon;
	if (is.getItem() == ModBase.magicGoodApple) return EnumRarity.rare;
	if (is.getItem() == ModBase.magicSuperApple) return EnumRarity.epic;
	return EnumRarity.common;
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean par4)
    {
	super.addInformation(is, p, l, par4);
	l.add("Increase mana cap by " + manaIncrease);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer p)
    {
	is.stackSize--;
	PlayerData pd = PlayersData.getPlayerData_static(p);
	pd.maxMana += manaIncrease;
	pd.mana += manaIncrease;
	if (!w.isRemote) PlayersData.sendPlayerDataToClient(p, p);
	p.inventory.markDirty();
	return is;
    }
}
