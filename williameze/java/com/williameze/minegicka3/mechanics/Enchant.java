package com.williameze.minegicka3.mechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.entities.monsters.Entity888;
import com.williameze.minegicka3.main.objects.items.Staff;
import com.williameze.minegicka3.mechanics.EnchantEntry.StatBasic;

public class Enchant
{
    private static Map<Object, EnchantEntry> enchantList = new HashMap();

    public static void load()
    {
	enchantList.clear();
	addEnchantObject(ModBase.thingy, 0.001, 0.001, 0.001, 0.001);
	addEnchantObject(ModBase.thingyGood, 0.016, 0.016, 0.016, 0.016);
	addEnchantObject(ModBase.thingySuper, 0.256, 0.256, 0.256, 0.256);
	addEnchantObject(ModBase.essenceArcane, "1.2a0.8l");
	addEnchantObject(ModBase.essenceCold, "1.2c0.8f");
	addEnchantObject(ModBase.essenceEarth, "1.2e0.8h");
	addEnchantObject(ModBase.essenceFire, "1.2f0.8c");
	addEnchantObject(ModBase.essenceIce, "1.2i1.1c0.8s0.9f");
	addEnchantObject(ModBase.essenceLife, "1.2l0.8a");
	addEnchantObject(ModBase.essenceLightning, "1.2h0.9e0.9w");
	addEnchantObject(ModBase.essenceShield, "1.2d");
	addEnchantObject(ModBase.essenceSteam, "1.2s1.1f0.8i0.9c");
	addEnchantObject(ModBase.essenceWater, "1.2w0.8h");
	addEnchantObject(ModBase.matResistance, Entity888.spellResistance.toString());

	addEnchantObject(Items.iron_ingot, 0.001, 0.001, 0, 0, "1.01h0.99e");
	addEnchantObject(Items.gold_ingot, 0, 0, 0.001, 0.001);
	addEnchantObject(Blocks.iron_block, 0.01, 0.01, 0, 0, "1.1h0.9e");
	addEnchantObject(Blocks.gold_block, 0, 0, 0.01, 0.01);
	addEnchantObject(Items.diamond, 0.02, -0.02, 0, 0);
	addEnchantObject(Items.emerald, -0.02, 0.02, 0, 0);
	addEnchantObject(Items.sugar, 0, 0.01, -0.01, 0);
	addEnchantObject(Items.nether_star, 1, 1, 1, 1);
	addEnchantObject(Items.redstone, 0, -0.005, 0.01, 0.01, "1.005h0.995e");
	addEnchantObject(Items.gunpowder, 0.0005, -0.0005, -0.0005, -0.0005, "1.005f0.995w");
	addEnchantObject(Items.fermented_spider_eye, "1.002a0.998l");
	addEnchantObject(Items.nether_wart, "1.01a0.99l");
	addEnchantObject(Items.snowball, "1.00125c1.001s0.99875f");
	addEnchantObject(Blocks.dirt, "1.0001e0.9999h");
	addEnchantObject(Blocks.cobblestone, "1.002e0.998h");
	addEnchantObject(Blocks.obsidian, "1.04e0.96h");
	addEnchantObject(Items.flint_and_steel, "1.005f0.995c");
	addEnchantObject(Items.blaze_rod, "1.01f1.005a0.99c0.995l");
	addEnchantObject(Items.magma_cream, "1.01f0.99c");
	addEnchantObject(Blocks.ice, "1.005i0.995f");
	addEnchantObject(Items.arrow, "1.005i");
	addEnchantObject(Items.bone, "1.005l0.995a");
	addEnchantObject(Items.wheat_seeds, "1.002l0.998a");
	addEnchantObject(Items.cake, "1.015l0.985a");
	addEnchantObject(Items.flint, "1.005h0.995e");
	addEnchantObject(Items.glowstone_dust, "1.005d");
	addEnchantObject(Items.golden_apple, "1.05d");
	addEnchantObject(Items.iron_door, "1.01d1.005h0.995e");
	addEnchantObject(Items.flint_and_steel, "1.005s0.995c");
	addEnchantObject(Items.glass_bottle, "1.002w0.998h");
	addEnchantObject(Items.water_bucket, "1.01w0.99h");
	addEnchantObject(Items.reeds, "1.015w0.99h0.99f");
    }

    public static void addEnchantObject(Object o, double power, double spd, double consume, double recharge)
    {
	addEnchantObject(o, power, spd, consume, recharge, "");
    }

    public static void addEnchantObject(Object o, String s)
    {
	addEnchantObject(o, 0, 0, 0, 0, s);
    }

    public static void addEnchantObject(Object o, double power, double spd, double consume, double recharge, String s)
    {
	enchantList.put(o, new EnchantEntry(o, power, spd, consume, recharge, s));
    }

    public static void addEnchantObject(Object o, Object... data)
    {
	enchantList.put(o, new EnchantEntry(o, data));
    }

    public static Object getAddedEnchantObject(Object o)
    {
	ItemStack is = null;
	if (o instanceof ItemStack) is = (ItemStack) o;
	else if (o instanceof Block) is = new ItemStack((Block) o);
	else if (o instanceof Item) is = new ItemStack((Item) o);
	else return null;
	for (Entry<Object, EnchantEntry> entry : enchantList.entrySet())
	{
	    Object o1 = entry.getKey();
	    if (o1 == o) return o1;

	    ItemStack is1 = null;
	    if (o1 instanceof ItemStack) is1 = (ItemStack) o1;
	    else if (o1 instanceof Block) is1 = new ItemStack((Block) o1);
	    else if (o1 instanceof Item) is1 = new ItemStack((Item) o1);
	    else continue;

	    if (is.isItemEqual(is1)) return entry.getKey();
	}
	return null;
    }

    public static EnchantEntry getEnchantEntry(Object o)
    {
	ItemStack is = null;
	if (o instanceof ItemStack) is = (ItemStack) o;
	else if (o instanceof Block) is = new ItemStack((Block) o);
	else if (o instanceof Item) is = new ItemStack((Item) o);
	else return null;
	for (Entry<Object, EnchantEntry> entry : enchantList.entrySet())
	{
	    Object o1 = entry.getKey();
	    if (o1 == o) return entry.getValue();

	    ItemStack is1 = null;
	    if (o1 instanceof ItemStack) is1 = (ItemStack) o1;
	    else if (o1 instanceof Block) is1 = new ItemStack((Block) o1);
	    else if (o1 instanceof Item) is1 = new ItemStack((Item) o1);
	    else continue;

	    if (is.isItemEqual(is1)) return entry.getValue();
	}
	return null;
    }

    public static EnchantEntry finalizeEntryChain(List<EnchantEntry> entries)
    {
	List<EnchantEntry> affectedEntries = new ArrayList();
	for (int a = 0; a < entries.size(); a++)
	{
	    if (entries.get(a) != null)
	    {
		EnchantEntry thisEntry = entries.get(a).copy();
		if (a > 0)
		{
		    EnchantEntry prevEntry = entries.get(a - 1);
		    if (prevEntry != null)
		    {
			thisEntry = thisEntry.affectBy(prevEntry);
		    }
		}
		if (a < entries.size() - 1)
		{
		    EnchantEntry nextEntry = entries.get(a + 1);
		    if (nextEntry != null)
		    {
			thisEntry = thisEntry.affectBy(nextEntry);
		    }
		}
		affectedEntries.add(thisEntry);
	    }
	}
	return combineEntries(affectedEntries);
    }

    public static EnchantEntry combineEntries(List<EnchantEntry> e)
    {
	EnchantEntry combinedEntry = new EnchantEntry();
	for (int a = 0; a < e.size(); a++)
	{
	    EnchantEntry entry = e.get(a);
	    double power = combinedEntry.statEnchanting.get(StatBasic.Power) + entry.statEnchanting.get(StatBasic.Power);
	    double speed = combinedEntry.statEnchanting.get(StatBasic.AtkSpeed) + entry.statEnchanting.get(StatBasic.AtkSpeed);
	    double consume = combinedEntry.statEnchanting.get(StatBasic.ConsumeRate) + entry.statEnchanting.get(StatBasic.ConsumeRate);
	    double recover = combinedEntry.statEnchanting.get(StatBasic.RecoverRate) + entry.statEnchanting.get(StatBasic.RecoverRate);
	    if (power < 0) power = 0;
	    if (speed < 0) speed = 0;
	    if (consume < 0) consume = 0;
	    if (recover < 0) recover = 0;
	    combinedEntry.statEnchanting.put(StatBasic.Power, power);
	    combinedEntry.statEnchanting.put(StatBasic.AtkSpeed, speed);
	    combinedEntry.statEnchanting.put(StatBasic.ConsumeRate, consume);
	    combinedEntry.statEnchanting.put(StatBasic.RecoverRate, recover);
	    combinedEntry.elementEnchanting = combinedEntry.elementEnchanting.add(entry.elementEnchanting);
	}
	return combinedEntry;
    }

    public static void enchantAndConsume(EntityPlayer p, List<ItemStack> items, int staffIndex)
    {
	if (p != null)
	{
	    ItemStack staff = p.inventory.getStackInSlot(staffIndex);
	    if (staff != null && staff.getItem() instanceof Staff)
	    {
		List<EnchantEntry> entries = new ArrayList();
		for (ItemStack is : items)
		{
		    EnchantEntry entry = getEnchantEntry(is);
		    if (entry != null)
		    {
			entries.add(entry);
		    }
		}
		EnchantEntry enchant = finalizeEntryChain(entries);
		applyStaffEnchant(staff, enchant, p.getGameProfile().getName());
		playerConsume(p, items);
	    }
	    p.inventory.markDirty();
	}
    }

    public static void playerConsume(EntityPlayer p, List<ItemStack> items)
    {
	for (int b = 0; b < items.size(); b++)
	{
	    ItemStack is = items.get(b);
	    for (int a = 0; a < p.inventory.getSizeInventory(); a++)
	    {
		ItemStack invIs = p.inventory.getStackInSlot(a);
		if (invIs != null && invIs.isItemEqual(is))
		{
		    invIs.stackSize--;
		    if (invIs.stackSize > 0) p.inventory.setInventorySlotContents(a, invIs);
		    else p.inventory.setInventorySlotContents(a, null);
		    break;
		}
	    }
	}
    }

    public static void applyStaffEnchant(ItemStack is, EnchantEntry enchant, String enchanter)
    {
	if (is.getItem() instanceof Staff)
	{
	    NBTTagCompound tag = Staff.getStaffTag_static(is);
	    tag.setBoolean("Enchanted", true);
	    tag.setDouble("Power", tag.getDouble("Power") + enchant.statEnchanting.get(StatBasic.Power));
	    tag.setDouble("ATKSpeed", tag.getDouble("ATKSpeed") + enchant.statEnchanting.get(StatBasic.AtkSpeed));
	    tag.setDouble("Consume", Math.max(tag.getDouble("Consume") - enchant.statEnchanting.get(StatBasic.ConsumeRate), 0.01));
	    tag.setDouble("Recover", tag.getDouble("Recover") + enchant.statEnchanting.get(StatBasic.RecoverRate));
	    SpellDamageModifier newModifier = new SpellDamageModifier(tag.getString("Modifier")).add(enchant.elementEnchanting);
	    newModifier.clampUnderZero();
	    tag.setString("Modifier", newModifier.toString());

	    String prevEnchanters = tag.getString("Enchanters");
	    String[] prevEcts = prevEnchanters.split(",");
	    boolean hasThisEnchanter = false;
	    for (String s : prevEcts)
	    {
		if (s.trim().compareTo(enchanter) == 0)
		{
		    hasThisEnchanter = true;
		}
	    }
	    if (!hasThisEnchanter)
	    {
		if (prevEnchanters.length() > 0) prevEnchanters += ", ";
		prevEnchanters += enchanter;
		tag.setString("Enchanters", prevEnchanters);
	    }
	}
    }
}
