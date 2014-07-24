package com.williameze.minegicka3.main.objects.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.mechanics.magicks.Magick;

public class ItemMagickPedia extends Item
{
    public ItemMagickPedia()
    {
	super();
	setMaxStackSize(1);
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass)
    {
	return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer p)
    {
	if (w.isRemote)
	{
	    p.openGui(ModBase.instance, 1, w, (int) p.posX, (int) p.posY, (int) p.posZ);
	}
	return super.onItemRightClick(is, w, p);
    }

}
