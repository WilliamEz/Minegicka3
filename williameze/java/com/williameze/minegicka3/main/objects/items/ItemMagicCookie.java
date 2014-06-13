package com.williameze.minegicka3.main.objects.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.PlayerData;
import com.williameze.minegicka3.core.PlayersData;

public class ItemMagicCookie extends ItemFood
{
    public double manaRecover;

    public ItemMagicCookie(double d)
    {
	super(4, (float) Math.pow(d / 16D, 0.2), false);
	setAlwaysEdible();
	manaRecover = d;
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass)
    {
	return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack is)
    {
	if (is.getItem() == ModBase.magicCookie) return EnumRarity.common;
	if (is.getItem() == ModBase.magicGoodCookie) return EnumRarity.uncommon;
	if (is.getItem() == ModBase.magicSuperCookie) return EnumRarity.rare;
	return EnumRarity.common;
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean par4)
    {
	super.addInformation(is, p, l, par4);
	l.add("Recover " + manaRecover + " mana");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer p)
    {
	is.stackSize--;
	PlayerData pd = PlayersData.getPlayerData_static(p);
	pd.shiftMana(manaRecover);
	if (!w.isRemote) PlayersData.sendPlayerDataToClient(p, p);
	p.inventory.markDirty();
	return is;
    }
}
