package com.williameze.minegicka3.main;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.magicks.Magick;
import com.williameze.minegicka3.main.objects.ItemMagickTablet;
import com.williameze.minegicka3.main.packets.PacketPlayerClickCraft;

public class ClickCraft
{
    public static final String catGeneral = "General", catStaff = "Staff", catMagick = "Magick";

    public static Map<ItemStack, List<Entry<ItemStack, Integer>>> clickCraftRecipes = new LinkedHashMap();
    public static Map<String, Map<ItemStack, List<Entry<ItemStack, Integer>>>> categorizedClickCraftRecipes = new LinkedHashMap();

    public static void load()
    {
	clickCraftRecipes.clear();
	categorizedClickCraftRecipes.clear();
	categorizedClickCraftRecipes.put(catGeneral, new LinkedHashMap());
	categorizedClickCraftRecipes.put(catStaff, new LinkedHashMap());
	categorizedClickCraftRecipes.put(catMagick, new LinkedHashMap());
	generalRecipes();
	stavesRecipe();
	magicksRecipe();
    }

    public static void generalRecipes()
    {
	registerClickCraftObject(new ItemStack(ModBase.thingy), new Object[] { Items.emerald, Items.gold_ingot });
	registerClickCraftObject(new ItemStack(ModBase.stick), new Object[] { ModBase.thingy, 2, Items.stick });
	registerClickCraftObject(new ItemStack(ModBase.stickArcane), new Object[] { ModBase.stick, Items.blaze_rod, 4,
		Items.fermented_spider_eye, 16, Items.nether_wart, 4 });
	registerClickCraftObject(new ItemStack(ModBase.stickCold), new Object[] { ModBase.stick, Items.snowball, 64 });
	registerClickCraftObject(new ItemStack(ModBase.stickEarth), new Object[] { ModBase.stick, Blocks.dirt, 64, Blocks.cobblestone, 16,
		Blocks.obsidian, 4 });
	registerClickCraftObject(new ItemStack(ModBase.stickFire), new Object[] { ModBase.stick, Items.flint_and_steel, Items.blaze_rod, 4,
		Items.magma_cream, 4 });
	registerClickCraftObject(new ItemStack(ModBase.stickIce), new Object[] { ModBase.stick, Blocks.ice, 8, Items.arrow, 8 });
	registerClickCraftObject(new ItemStack(ModBase.stickLife), new Object[] { ModBase.stick, Items.bone, 8, Items.wheat_seeds, 8,
		Items.cake, new ItemStack(Items.dye, 4, 10) });
	registerClickCraftObject(new ItemStack(ModBase.stickLightning), new Object[] { ModBase.stick, Items.iron_ingot, 8,
		new ItemStack(Items.dye, 4, 13) });
	registerClickCraftObject(new ItemStack(ModBase.stickShield), new Object[] { ModBase.stick, Items.glowstone_dust, 16,
		Items.golden_apple, 4, Items.iron_door, new ItemStack(Items.dye, 8, 11) });
	registerClickCraftObject(new ItemStack(ModBase.stickSteam), new Object[] { ModBase.stick, Items.flint_and_steel, 2, Items.snowball,
		16 });
	registerClickCraftObject(new ItemStack(ModBase.stickWater), new Object[] { ModBase.stick, Items.glass_bottle, 3,
		new ItemStack(Items.dye, 4, 4) });
    }

    public static void stavesRecipe()
    {
	registerClickCraftObject(catStaff, new ItemStack(ModBase.staff), new Object[] { ModBase.stick, 3, ModBase.thingy,
		new ItemStack(Items.dye, 1, 13) });
    }

    public static void magicksRecipe()
    {
	List<ItemStack> l = ((ItemMagickTablet) ModBase.magickTablet).allMagickPages();
	for (int a = 0; a < l.size(); a++)
	{
	    ItemStack is = l.get(a);
	    Magick m = ((ItemMagickTablet) ModBase.magickTablet).getUnlocking(is);
	    if (m != null && m.craftable) registerClickCraftObject(catMagick, is, m.getCraftClickTabletRecipe());
	}

    }

    public static List<Entry<ItemStack, Integer>> getRecipe(ItemStack is)
    {
	return getRecipeFrom(is, clickCraftRecipes);
    }

    public static List<Entry<ItemStack, Integer>> getRecipe(String category, ItemStack is)
    {
	if (categorizedClickCraftRecipes.containsKey(category))
	{
	    return getRecipeFrom(is, categorizedClickCraftRecipes.get(category));
	}
	else return null;
    }

    public static List<Entry<ItemStack, Integer>> getRecipeFrom(ItemStack is, Map<ItemStack, List<Entry<ItemStack, Integer>>> recipes)
    {
	Iterator<Entry<ItemStack, List<Entry<ItemStack, Integer>>>> ite = recipes.entrySet().iterator();
	while (ite.hasNext())
	{
	    Entry<ItemStack, List<Entry<ItemStack, Integer>>> entry = ite.next();
	    ItemStack entryIS = entry.getKey();
	    if (entryIS != null && ItemStack.areItemStacksEqual(entryIS, is))
	    {
		return entry.getValue();
	    }
	}
	return null;
    }

    public static void registerClickCraftObject(ItemStack output, Object... objects)
    {
	registerClickCraftObject(catGeneral, output, objects);
    }

    public static void registerClickCraftObject(String category, ItemStack output, Object... objects)
    {
	Entry<ItemStack, List<Entry<ItemStack, Integer>>> entry = getClickCraftObjectRecipe(output, objects);
	if (!categorizedClickCraftRecipes.containsKey(category))
	{
	    categorizedClickCraftRecipes.put(category, new LinkedHashMap());
	}
	Map<ItemStack, List<Entry<ItemStack, Integer>>> map = categorizedClickCraftRecipes.get(category);
	map.put(entry.getKey(), entry.getValue());
	clickCraftRecipes.put(entry.getKey(), entry.getValue());
    }

    private static Entry<ItemStack, List<Entry<ItemStack, Integer>>> getClickCraftObjectRecipe(ItemStack output, Object... objects)
    {
	if (output == null || objects == null || objects.length <= 0) return null;

	List<Entry<ItemStack, Integer>> recipe = new ArrayList();
	for (int a = 0; a < objects.length; a++)
	{
	    Object o1 = objects[a];
	    if (o1 == null) continue;
	    ItemStack toAdd = null;
	    if (o1 instanceof Block || o1 instanceof Item)
	    {
		int value = 1;
		if (a < objects.length - 1)
		{
		    Object o2 = objects[a + 1];
		    if (o2 instanceof Integer)
		    {
			value = Integer.parseInt(o2.toString());
			a++;
		    }
		}
		if (o1 instanceof Block) toAdd = new ItemStack((Block) o1, value);
		else toAdd = new ItemStack((Item) o1, value);
	    }
	    else if (o1 instanceof ItemStack) toAdd = (ItemStack) o1;

	    boolean hasThisAlready = false;
	    for (int b = 0; b < recipe.size(); b++)
	    {
		Entry<ItemStack, Integer> entry = recipe.get(b);
		ItemStack prevStack = entry.getKey();
		int value = entry.getValue();
		if (ItemStack.areItemStackTagsEqual(toAdd, prevStack) && toAdd.isItemEqual(prevStack))
		{
		    hasThisAlready = true;
		    value += toAdd.stackSize;
		    entry.setValue(value);
		    recipe.set(b, entry);
		    break;
		}
	    }

	    if (!hasThisAlready)
	    {
		Entry<ItemStack, Integer> entry = new SimpleEntry<ItemStack, Integer>(toAdd, toAdd.stackSize);
		recipe.add(entry);
	    }
	}

	if (recipe.isEmpty()) return null;
	else return new SimpleEntry<ItemStack, List<Entry<ItemStack, Integer>>>(output, recipe);
    }

    public static void clientPlayerQueueCraft(EntityPlayer p, ItemStack is, int repeat)
    {
	ModBase.packetPipeline.sendToServer(new PacketPlayerClickCraft(p, is, repeat));
    }

    public static void playerCraft(EntityPlayer p, ItemStack is, int repeat)
    {
	List<Entry<ItemStack, Integer>> recipe = getRecipe(is);
	for (int a = 0; a < repeat; a++)
	{
	    ItemStack toAdd = is.copy();
	    int addable = 0;
	    for (int b = 0; b < p.inventory.mainInventory.length; b++)
	    {
		ItemStack slot = p.inventory.getStackInSlot(b);
		if (slot == null)
		{
		    addable = toAdd.stackSize;
		}
		else if (slot.isItemEqual(toAdd) && ItemStack.areItemStackTagsEqual(slot, toAdd))
		{
		    addable += slot.getMaxStackSize() - slot.stackSize;
		}
		if (addable >= toAdd.stackSize) break;
	    }
	    if (addable >= toAdd.stackSize)
	    {
		for (int b = 0; b < p.inventory.mainInventory.length; b++)
		{
		    ItemStack slot = p.inventory.getStackInSlot(b);
		    if (slot == null)
		    {
			p.inventory.setInventorySlotContents(b, toAdd.copy());
			toAdd.stackSize = 0;
		    }
		    else if (slot.isItemEqual(toAdd) && ItemStack.areItemStackTagsEqual(slot, toAdd))
		    {
			int addAmount = Math.min(slot.getMaxStackSize() - slot.stackSize, toAdd.stackSize);
			slot.stackSize += addAmount;
			toAdd.stackSize -= addAmount;
		    }
		    if (toAdd.stackSize <= 0) break;
		}
		for (Entry<ItemStack, Integer> entry : recipe)
		{
		    ItemStack recipeIS = entry.getKey();
		    int stackSize = entry.getValue();

		    for (int b = 0; b < p.inventory.mainInventory.length; b++)
		    {
			ItemStack slot = p.inventory.getStackInSlot(b);
			if (slot != null && slot.isItemEqual(recipeIS) && ItemStack.areItemStackTagsEqual(slot, recipeIS))
			{
			    int reducing = Math.min(stackSize, slot.stackSize);
			    slot.stackSize -= reducing;
			    if (slot.stackSize <= 0)
			    {
				slot = null;
				p.inventory.setInventorySlotContents(b, slot);
			    }
			    stackSize -= reducing;
			}
			if (stackSize <= 0) break;
		    }
		}
	    }
	}
	p.inventory.markDirty();
    }
}
