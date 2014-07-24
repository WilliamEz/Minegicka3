package com.williameze.minegicka3.mechanics;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.williameze.minegicka3.main.objects.items.ItemMagickTablet;
import com.williameze.minegicka3.main.objects.items.Staff;
import com.williameze.minegicka3.main.packets.PacketPlayerClickCraft;
import com.williameze.minegicka3.mechanics.magicks.Magick;

public class ClickCraft
{
    public static final String catGeneral = "General", catAdvanced = "Advanced", catStaff = "Staff", catMagick = "Magick", catHat = "Hat";

    public static Map<Integer, CraftEntry> recipes = new LinkedHashMap();
    public static Map<String, Map<Integer, CraftEntry>> categorizedRecipes = new LinkedHashMap();

    public static void load()
    {
	recipes.clear();
	categorizedRecipes.clear();
	generalRecipes();
	advancedRecipes();
	stavesRecipe();
	//magicksRecipe();
	hatsRecipe();
    }

    public static void generalRecipes()
    {
	registerClickCraftObject(new ItemStack(ModBase.thingy), new Object[] { Items.iron_ingot, Items.gold_ingot });
	registerClickCraftObject(new ItemStack(ModBase.thingyGood), new Object[] { ModBase.thingy, 16 });
	registerClickCraftObject(new ItemStack(ModBase.thingySuper), new Object[] { ModBase.thingyGood, 16 });
	registerClickCraftObject(new ItemStack(ModBase.stick), new Object[] { ModBase.thingy, 2, Items.stick });
	registerClickCraftObject(new ItemStack(ModBase.stickGood), new Object[] { ModBase.thingyGood, 2, ModBase.stick });
	registerClickCraftObject(new ItemStack(ModBase.stickSuper), new Object[] { ModBase.thingySuper, 2, ModBase.stickGood });
	registerClickCraftObject(new ItemStack(ModBase.magicApple), new Object[] { Items.apple, ModBase.thingy });
	registerClickCraftObject(new ItemStack(ModBase.magicGoodApple), new Object[] { Items.golden_apple, ModBase.thingyGood });
	registerClickCraftObject(new ItemStack(ModBase.magicSuperApple),
		new Object[] { new ItemStack(Items.golden_apple, 1, 1), ModBase.thingySuper });
	registerClickCraftObject(new ItemStack(ModBase.magicCookie, 4), new Object[] { Items.cookie, 4, ModBase.thingy });
	registerClickCraftObject(new ItemStack(ModBase.magicGoodCookie, 4), new Object[] { Items.cookie, 4, ModBase.thingyGood });
	registerClickCraftObject(new ItemStack(ModBase.magicSuperCookie, 4), new Object[] { Items.cookie, 4, ModBase.thingySuper });
	registerClickCraftObject(new ItemStack(ModBase.essenceArcane), new Object[] { ModBase.thingy, Items.blaze_rod, 4, Items.fermented_spider_eye,
		16, Items.nether_wart, 4 });
	registerClickCraftObject(new ItemStack(ModBase.essenceCold), new Object[] { ModBase.thingy, Items.snowball, 64 });
	registerClickCraftObject(new ItemStack(ModBase.essenceEarth), new Object[] { ModBase.thingy, Blocks.dirt, 64, Blocks.cobblestone, 16,
		Blocks.obsidian, 4 });
	registerClickCraftObject(new ItemStack(ModBase.essenceFire), new Object[] { ModBase.thingy, Items.flint_and_steel, Items.blaze_rod, 4,
		Items.magma_cream, 4 });
	registerClickCraftObject(new ItemStack(ModBase.essenceIce), new Object[] { ModBase.thingy, Blocks.ice, 8, Items.arrow, 8 });
	registerClickCraftObject(new ItemStack(ModBase.essenceLife), new Object[] { ModBase.thingy, Items.bone, 8, Items.wheat_seeds, 8, Items.cake });
	registerClickCraftObject(new ItemStack(ModBase.essenceLightning), new Object[] { ModBase.thingy, Items.iron_ingot, 8, Items.flint, 4 });
	registerClickCraftObject(new ItemStack(ModBase.essenceShield), new Object[] { ModBase.thingy, Items.glowstone_dust, 8, Items.golden_apple, 1,
		Items.iron_door });
	registerClickCraftObject(new ItemStack(ModBase.essenceSteam), new Object[] { ModBase.thingy, Items.flint_and_steel, 2, Items.snowball, 16 });
	registerClickCraftObject(new ItemStack(ModBase.essenceWater), new Object[] { ModBase.thingy, Items.glass_bottle, 3, Items.water_bucket });
    }

    public static void advancedRecipes()
    {
	registerClickCraftObject(catAdvanced, new ItemStack(ModBase.craftStation), new Object[] { Blocks.obsidian, 3, Items.gold_ingot, 2,
		Items.diamond, 2, Items.emerald });
	registerClickCraftObject(catAdvanced, new ItemStack(ModBase.enchantStaff), new Object[] { Blocks.obsidian, 3, ModBase.thingy, 3,
		new ItemStack(Items.dye, 1, 13), Blocks.glass, 2 });
	registerClickCraftObject(catAdvanced, new ItemStack(ModBase.magickPedia), new Object[] { Items.book, ModBase.thingy });
    }

    public static void stavesRecipe()
    {
	registerClickCraftObject(catStaff, new ItemStack(ModBase.staff), new Object[] { ModBase.stick, 3, ModBase.thingy,
		new ItemStack(Items.dye, 1, 13) });
	registerClickCraftObject(catStaff, new ItemStack(ModBase.staffGrand), new Object[] { ModBase.stick, 2, ModBase.stickGood, 2,
		ModBase.thingyGood });
	registerClickCraftObject(catStaff, new ItemStack(ModBase.staffSuper), new Object[] { ModBase.stick, 2, ModBase.stickSuper, 3,
		ModBase.thingySuper, new ItemStack(Items.dye, 3, 1) });
	registerClickCraftObject(catStaff, new ItemStack(ModBase.staffBlessing), new Object[] { ModBase.staff, ModBase.essenceLife, 2,
		Items.glass_bottle, Items.fermented_spider_eye, 2, Items.magma_cream, Items.blaze_powder, Items.ghast_tear, Items.redstone, 8,
		Items.glowstone_dust, 8, Items.speckled_melon, Items.golden_carrot, Items.sugar, 4, new ItemStack(Items.fish, 1, 3) });
	registerClickCraftObject(catStaff, new ItemStack(ModBase.staffDestruction), new Object[] { ModBase.staff, ModBase.essenceArcane,
		ModBase.essenceFire, ModBase.essenceEarth, Items.gunpowder, 8, Items.flint_and_steel, Items.blaze_powder, 2 });
	registerClickCraftObject(catStaff, new ItemStack(ModBase.staffTelekinesis), new Object[] { ModBase.staff, ModBase.essenceSteam, Items.arrow,
		16, Items.feather, 16, Blocks.sticky_piston });
	registerClickCraftObject(catStaff, new ItemStack(ModBase.staffManipulation), new Object[] { ModBase.staff, ModBase.essenceArcane,
		ModBase.essenceLife, Items.golden_carrot, Items.fermented_spider_eye, 2, Items.redstone, 2, Items.rotten_flesh, 4 });
    }

    @Deprecated
    public static void magicksRecipe()
    {
	List<ItemStack> l = ((ItemMagickTablet) ModBase.magickTablet).allMagickPages();
	for (int a = 0; a < l.size(); a++)
	{
	    ItemStack is = l.get(a);
	    Magick m = ((ItemMagickTablet) ModBase.magickTablet).getUnlocking(is);
	    if (m != null && m.craftable) registerClickCraftObject(catMagick, is, m.getUnlockMaterials());
	}

    }

    public static void hatsRecipe()
    {
	registerClickCraftObject(catHat, new ItemStack(ModBase.hat), new Object[] { Items.leather, 8, ModBase.thingy });
	registerClickCraftObject(catHat, new ItemStack(ModBase.hatRisk), new Object[] { ModBase.hat, ModBase.essenceLife,
		new ItemStack(Items.dye, 3, 1) });
	registerClickCraftObject(catHat, new ItemStack(ModBase.hatResistance), new Object[] { ModBase.hat, ModBase.matResistance });
    }

    public static CraftEntry getRecipe(int recipeID)
    {
	return recipes.get((Integer) recipeID);
    }

    public static List<CraftEntry> getRecipesFor(ItemStack is)
    {
	return getRecipesFor(is, recipes);
    }

    public static List<CraftEntry> getRecipesFor(ItemStack is, String category)
    {
	if (categorizedRecipes.containsKey(category))
	{
	    return getRecipesFor(is, categorizedRecipes.get(category));
	}
	else return new ArrayList();
    }

    public static List<CraftEntry> getRecipesFor(ItemStack is, Map<Integer, CraftEntry> recipes)
    {
	Iterator<Entry<Integer, CraftEntry>> ite = recipes.entrySet().iterator();
	List<CraftEntry> l = new ArrayList();
	while (ite.hasNext())
	{
	    Entry<Integer, CraftEntry> mapEntry = ite.next();
	    CraftEntry entry = mapEntry.getValue();
	    ItemStack entryIS = entry.output;
	    if (entryIS != null && is != null && entryIS.getItem() == is.getItem() && entryIS.getItemDamage() == is.getItemDamage()
		    && entryIS.stackSize == is.stackSize)
	    {
		if (entryIS.stackTagCompound == null && is.stackTagCompound == null)
		{
		    l.add(entry);
		}
		else if (entryIS.stackTagCompound != null && is.stackTagCompound != null && entryIS.stackTagCompound.equals(is.stackTagCompound))
		{
		    l.add(entry);
		}
	    }
	}
	return l;
    }

    public static void registerClickCraftObject(ItemStack output, Object... objects)
    {
	registerClickCraftObject(catGeneral, output, objects);
    }

    public static void registerClickCraftObject(String category, ItemStack output, Object... objects)
    {
	CraftEntry entry = getRegisteringRecipe(output, objects);
	if (!categorizedRecipes.containsKey(category))
	{
	    categorizedRecipes.put(category, new LinkedHashMap());
	}
	Map<Integer, CraftEntry> map = categorizedRecipes.get(category);
	map.put((Integer) entry.id, entry);
	recipes.put((Integer) entry.id, entry);
    }

    private static CraftEntry getRegisteringRecipe(ItemStack output, Object... objects)
    {
	if (output == null || objects == null || objects.length <= 0) return null;

	List<Entry<ItemStack, Integer>> recipe = combineDuplicates(objects);

	if (output.getItem() instanceof Staff) ((Staff) output.getItem()).getStaffTag(output);

	if (recipe.isEmpty()) return null;
	else return new CraftEntry(output, recipe);
    }

    public static List<Entry<ItemStack, Integer>> combineDuplicates(Object... objects)
    {
	List<Entry<ItemStack, Integer>> list = new ArrayList();
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
	    for (int b = 0; b < list.size(); b++)
	    {
		Entry<ItemStack, Integer> entry = list.get(b);
		ItemStack prevStack = entry.getKey();
		int value = entry.getValue();
		if (ItemStack.areItemStackTagsEqual(toAdd, prevStack) && toAdd.isItemEqual(prevStack))
		{
		    hasThisAlready = true;
		    value += toAdd.stackSize;
		    entry.setValue(value);
		    list.set(b, entry);
		    break;
		}
	    }

	    if (!hasThisAlready)
	    {
		Entry<ItemStack, Integer> entry = new SimpleEntry<ItemStack, Integer>(toAdd, toAdd.stackSize);
		list.add(entry);
	    }
	}
	return list;
    }

    public static void clientPlayerQueueCraft(EntityPlayer p, CraftEntry entry, int repeat)
    {
	ModBase.packetPipeline.sendToServer(new PacketPlayerClickCraft(p, entry, repeat));
    }

    public static int getAmountUserHas(EntityPlayer p, ItemStack hasIS)
    {
	if (hasIS == null) return 0;
	int count = 0;
	for (int a = 0; a < p.inventory.getSizeInventory(); a++)
	{
	    ItemStack is = p.inventory.getStackInSlot(a);
	    if (is != null && is.isItemEqual(hasIS))
	    {
		if (is.getItem() instanceof Staff && !((Staff) is.getItem()).getEnchanted(is) || is.getItem() instanceof Staff == false
			&& ItemStack.areItemStackTagsEqual(is, hasIS))
		{
		    count += is.stackSize;
		}
	    }
	}
	return count;
    }

    public static void playerCraft(EntityPlayer p, int recipeID, int repeat)
    {
	CraftEntry craftEntry = getRecipe(recipeID);
	if (craftEntry == null)
	{
	    System.err.println("ClickCraft error. No recipe found for recipe id " + recipeID);
	    return;
	}
	playerCraft(p, craftEntry, repeat);
    }

    public static void playerCraft(EntityPlayer p, CraftEntry craftEntry, int repeat)
    {
	List<Entry<ItemStack, Integer>> recipe = craftEntry.input;
	for (int a = 0; a < repeat; a++)
	{
	    ItemStack toAdd = craftEntry.output == null ? null : craftEntry.output.copy();
	    int addable = 0;
	    if (toAdd != null)
	    {
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
	    }
	    if (toAdd == null || addable >= toAdd.stackSize)
	    {
		if (toAdd != null)
		{
		    for (int b = 0; b < p.inventory.mainInventory.length; b++)
		    {
			ItemStack slot = p.inventory.getStackInSlot(b);
			if (slot == null)
			{
			    p.inventory.setInventorySlotContents(b, toAdd.copy());
			    toAdd.stackSize = 0;
			}
			else if (slot.isItemEqual(toAdd))
			{
			    if (slot.getItem() instanceof Staff && !((Staff) slot.getItem()).getEnchanted(slot)
				    || slot.getItem() instanceof Staff == false && ItemStack.areItemStackTagsEqual(slot, toAdd))
			    {
				int addAmount = Math.min(slot.getMaxStackSize() - slot.stackSize, toAdd.stackSize);
				slot.stackSize += addAmount;
				toAdd.stackSize -= addAmount;
			    }
			}
			if (toAdd.stackSize <= 0) break;
		    }
		}
		playerConsume(p, recipe);
	    }
	}
	p.inventory.markDirty();
    }

    public static void playerConsume(EntityPlayer p, List<Entry<ItemStack, Integer>> recipe)
    {
	for (Entry<ItemStack, Integer> entry : recipe)
	{
	    ItemStack recipeIS = entry.getKey();
	    int stackSize = entry.getValue();

	    for (int b = 0; b < p.inventory.mainInventory.length; b++)
	    {
		ItemStack slot = p.inventory.getStackInSlot(b);
		if (slot != null && slot.isItemEqual(recipeIS))
		{
		    if (slot.getItem() instanceof Staff && !((Staff) slot.getItem()).getEnchanted(slot) || slot.getItem() instanceof Staff == false
			    && ItemStack.areItemStackTagsEqual(slot, recipeIS))
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
		}
		if (stackSize <= 0) break;
	    }
	}
	p.inventory.markDirty();
    }
}
