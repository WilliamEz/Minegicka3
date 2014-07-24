package com.williameze.minegicka3.main.worldgen;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import com.williameze.minegicka3.ModBase;

public class ChestGenHook
{
    public static void load()
    {
	ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(ModBase.thingy, 0, 1, 3, 5));
	ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(ModBase.thingyGood, 0, 1, 2, 2));
	ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(ModBase.stick, 0, 1, 3, 3));
	ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(ModBase.stickGood, 0, 1, 2, 1));
	ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(ModBase.magicCookie, 0, 1, 3, 10));
	ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(ModBase.staff, 0, 1, 1, 1));
	ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(ModBase.hat, 0, 1, 1, 1));
    }
}
