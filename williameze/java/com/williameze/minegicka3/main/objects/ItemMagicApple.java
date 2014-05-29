package com.williameze.minegicka3.main.objects;

import java.util.List;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.PlayerData;
import com.williameze.minegicka3.core.PlayersData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMagicApple extends Item
{
    public double manaIncrease;

    public ItemMagicApple(double d)
    {
	super();
	manaIncrease = d;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack is)
    {
	return is.getItem() == ModBase.magicGoldenApple;
    }

    @Override
    public EnumRarity getRarity(ItemStack is)
    {
	return is.getItem() == ModBase.magicGoldenApple ? EnumRarity.epic : EnumRarity.uncommon;
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean par4)
    {
	super.addInformation(is, p, l, par4);
	l.add("Increase " + manaIncrease + " mana.");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer p)
    {
	if (!w.isRemote)
	{
	    is.stackSize--;
	    PlayerData pd = PlayersData.getPlayerData_static(p);
	    pd.maxMana += manaIncrease;
	    pd.mana += manaIncrease;
	    PlayersData.sendPlayerDataToClient(p, p);
	    p.inventory.markDirty();
	}
	return is;
    }
}
