package com.williameze.minegicka3.main.objects.items;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.functional.PlayersData;
import com.williameze.minegicka3.main.packets.PacketActiveStaff;
import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.SpellDamageModifier;
import com.williameze.minegicka3.mechanics.spells.Spell;

public class Staff extends Item
{
    static String[] fullNames = new String[] { "Arcane", "Cold", "Fire", "Earth", "Ice", "Life", "Lightning", "Shield", "Steam", "Water" };
    static String[] shortNames = new String[] { "Ar", "Co", "Fr", "Ea", "Ie", "Lf", "Lg", "Sh", "St", "Wa" };

    private ItemStack currentUsing;
    public static int useCount;
    public double basePower;
    public double baseATKSpeed;
    public double baseConsume;
    public double baseRecover;
    public SpellDamageModifier baseModifier;

    public Staff()
    {
	super();
	ModBase.proxy.registerItemRenderer(this);
	setMaxStackSize(1);
	setCreativeTab(ModBase.modCreativeTab);
	setBaseStats(1, 1, 1, 1);
	setBaseModifier(new SpellDamageModifier());
	setTextureName("apple");
    }

    public Staff setBaseStats(double power, double atkspd, double consume, double recover)
    {
	basePower = power;
	baseATKSpeed = atkspd;
	baseConsume = consume;
	baseRecover = recover;
	return this;
    }

    public Staff setBaseModifier(SpellDamageModifier sp)
    {
	baseModifier = sp;
	return this;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
	return Integer.MAX_VALUE;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
	return EnumAction.block;
    }

    public boolean isCurrentItem(ItemStack is, EntityLivingBase p)
    {
	return p.getHeldItem() != null && p.getHeldItem().isItemEqual(is) && p.getHeldItem().hashCode() == is.hashCode();
    }

    public boolean hasActiveAbility(World world, EntityLivingBase user, ItemStack is)
    {
	return false;
    }

    public void activeAbility(World world, EntityLivingBase user, ItemStack is)
    {
    }

    public void passiveAbility(World world, EntityLivingBase user, ItemStack is)
    {
    }

    public double getActiveAbilityCost(World world, EntityLivingBase user, ItemStack is)
    {
	return 0;
    }

    public double consumeMana(World world, EntityLivingBase user, ItemStack is)
    {
	double manaCost = getActiveAbilityCost(world, user, is);
	return Spell.consumeMana(user, manaCost, true, true, 2);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack is, World w, EntityPlayer p, int par4)
    {
	super.onPlayerStoppedUsing(is, w, p, par4);
	stopUse(w, p, is);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer p)
    {
	p.setItemInUse(is, this.getMaxItemUseDuration(is));
	startUse(w, p, is);
	return super.onItemRightClick(is, w, p);
    }

    @Override
    public boolean onItemUse(ItemStack is, EntityPlayer p, World w, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
	p.setItemInUse(is, this.getMaxItemUseDuration(is));
	startUse(w, p, is);
	return true;
    }

    @Override
    public void onUpdate(ItemStack is, World w, Entity e, int par4, boolean par5)
    {
	super.onUpdate(is, w, e, par4, par5);
	if (w.isRemote && currentUsing != null && is.isItemEqual(currentUsing) && is.hashCode() == currentUsing.hashCode())
	{
	    if (e instanceof EntityPlayer && !isCurrentItem(is, (EntityPlayer) e))
	    {
		unequipStaff(w, (EntityPlayer) e, is);
	    }
	    useCount++;
	}
	if (e instanceof EntityLivingBase && isCurrentItem(is, (EntityLivingBase) e))
	{
	    passiveAbility(w, (EntityLivingBase) e, is);
	}
    }

    public void unequipStaff(World world, EntityPlayer e, ItemStack is)
    {
	stopUse(world, e, is);
    }

    public void startUse(World world, EntityPlayer player, ItemStack is)
    {
	if (world.isRemote)
	{
	    if (currentUsing == null)
	    {
		useCount = 0;
		currentUsing = is;
		if (ModBase.proxy.getCoreClient().queuedElements.isEmpty())
		{
		    if (hasActiveAbility(world, player, is) && PlayersData.clientPlayerData.mana >= getActiveAbilityCost(world, player, is))
		    {
			ModBase.packetPipeline.sendToServer(new PacketActiveStaff(world, player, is));
			activeAbility(world, player, is);
		    }
		}
		else
		{
		    ModBase.proxy.getCoreClient().clientStartUsingStaff(world, player, currentUsing);
		}
	    }
	    else if (!ItemStack.areItemStacksEqual(currentUsing, is))
	    {
		stopUse(world, player, currentUsing);
		startUse(world, player, is);
	    }
	}
    }

    public void stopUse(World world, EntityPlayer player, ItemStack is)
    {
	if (world.isRemote)
	{
	    if (is != null)
	    {
		ModBase.proxy.getCoreClient().clientStopUsingStaff(world, player, is, useCount);
	    }
	    currentUsing = null;
	    useCount = 0;
	}
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
    {
	super.onUsingTick(stack, player, count);
	if (player.worldObj.isRemote) useCount++;
    }

    public double getPower(ItemStack is)
    {
	return getStaffTag(is).getDouble("Power");
    }

    public double getATKSpeed(ItemStack is)
    {
	return getStaffTag(is).getDouble("ATKSpeed");
    }

    public double getConsume(ItemStack is)
    {
	return getStaffTag(is).getDouble("Consume");
    }

    public double getRecover(ItemStack is)
    {
	return getStaffTag(is).getDouble("Recover");
    }

    public boolean getEnchanted(ItemStack is)
    {
	return getStaffTag(is).getBoolean("Enchanted");
    }

    public SpellDamageModifier getModifier(ItemStack is)
    {
	return new SpellDamageModifier(getStaffTag(is).getString("Modifier"));
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean advancedItemTooltip)
    {
	super.addInformation(is, p, l, advancedItemTooltip);

	if (this == ModBase.staff)
	{
	    String s = (String) l.get(0);
	    s += EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.ITALIC
		    + (GuiScreen.isShiftKeyDown() || getEnchanted(is) ? "" : " (press Shift for more details)");
	    l.set(0, s);
	}

	if (getEnchanted(is))
	{
	    String s = EnumChatFormatting.YELLOW + "" + EnumChatFormatting.ITALIC + "Enchanted";
	    if (GuiScreen.isShiftKeyDown())
	    {
		String ect = getStaffTag(is).getString("Enchanters");
		if (ect.length() > 0) s += " by " + ect;
	    }
	    l.add(s);
	}

	NBTTagCompound tag = getStaffTag(is);
	if (!GuiScreen.isShiftKeyDown())
	{
	    String power = EnumChatFormatting.RED + "x" + FuncHelper.formatToDecimal(tag.getDouble("Power")) + EnumChatFormatting.GRAY;
	    String atkSpeed = EnumChatFormatting.GREEN + "x" + FuncHelper.formatToDecimal(tag.getDouble("ATKSpeed")) + EnumChatFormatting.GRAY;
	    String consume = EnumChatFormatting.BLUE + "x" + FuncHelper.formatToDecimal(tag.getDouble("Consume")) + EnumChatFormatting.GRAY;
	    String recover = EnumChatFormatting.LIGHT_PURPLE + "x" + FuncHelper.formatToDecimal(tag.getDouble("Recover")) + EnumChatFormatting.GRAY;
	    l.add("[ " + power + " ; " + atkSpeed + " ; " + consume + " ; " + recover + " ]");
	}
	else
	{
	    l.add("[ " + EnumChatFormatting.RED + "Power: x" + FuncHelper.formatToDecimal(tag.getDouble("Power")) + EnumChatFormatting.GRAY + " ]");
	    l.add("[ " + EnumChatFormatting.GREEN + "Atk Rate: x" + FuncHelper.formatToDecimal(tag.getDouble("ATKSpeed")) + EnumChatFormatting.GRAY
		    + " ]");
	    l.add("[ " + EnumChatFormatting.BLUE + "Mana Consume: x" + FuncHelper.formatToDecimal(tag.getDouble("Consume")) + EnumChatFormatting.GRAY
		    + " ]");
	    l.add("[ " + EnumChatFormatting.LIGHT_PURPLE + "Mana Recover: x" + FuncHelper.formatToDecimal(tag.getDouble("Recover"))
		    + EnumChatFormatting.GRAY + " ]");
	}

	l.addAll(getModifierStrings(is));

	String activeDesc = getActiveDesc(is, p);
	String passiveDesc = getPassiveDesc(is, p);
	if (activeDesc != null)
	{
	    if (!GuiScreen.isShiftKeyDown())
	    {
		l.add("[ " + EnumChatFormatting.WHITE + "A" + EnumChatFormatting.GRAY + " ] " + activeDesc);
	    }
	    else
	    {
		l.add("[ " + EnumChatFormatting.WHITE + "Active Ability" + EnumChatFormatting.GRAY + " ] " + activeDesc);
		String detailedActive = getDetailedActiveDesc(is, p);
		if (detailedActive != null)
		{
		    l.add(EnumChatFormatting.ITALIC + detailedActive);
		}
	    }
	}
	if (passiveDesc != null)
	{
	    if (!GuiScreen.isShiftKeyDown())
	    {
		l.add("[ " + EnumChatFormatting.WHITE + "P" + EnumChatFormatting.GRAY + " ] " + passiveDesc);
	    }
	    else
	    {
		l.add("[ " + EnumChatFormatting.WHITE + "Passive Ability" + EnumChatFormatting.GRAY + " ] " + passiveDesc);
		String detailedPassive = getDetailedPassiveDesc(is, p);
		if (detailedPassive != null)
		{
		    l.add(EnumChatFormatting.ITALIC + detailedPassive);
		}
	    }
	}

	if (tag.getInteger("Add queueable") != 0)
	{
	    int queueable = (5 + tag.getInteger("Add queueable"));
	    l.add(queueable + "-element combo" + (queueable > 5 ? "!!!" : ""));
	}
    }

    public List<String> getModifierStrings(ItemStack is)
    {
	List<String> l = new ArrayList();
	SpellDamageModifier mod = getModifier(is);
	if (!mod.isDefaultModifier())
	{
	    double[] values = new double[] { mod.arcaneMod, mod.coldMod, mod.earthMod, mod.fireMod, mod.iceMod, mod.lifeMod, mod.lightningMod,
		    mod.shieldMod, mod.steamMod, mod.waterMod };
	    DecimalFormat df = FuncHelper.decimalFormat;
	    int counted = 0;
	    String display = "";
	    for (int a = 0; a < 10; a++)
	    {
		if (values[a] != 1)
		{
		    if (counted % 3 == 0)
		    {
			if (counted > 0)
			{
			    l.add("[ " + display + " ]");
			    display = "";
			}
		    }
		    else
		    {
			display += " ; ";
		    }
		    display += Element.values()[a].getTextColor() + fullNames[a] + " x" + df.format(values[a]) + EnumChatFormatting.GRAY;
		    counted++;
		}
		if (a == 9 && display.length() > 0)
		{
		    l.add("[ " + display + " ]");
		}
	    }
	}
	return l;
    }

    public String getActiveDesc(ItemStack is, EntityPlayer p)
    {
	return null;
    }

    public String getPassiveDesc(ItemStack is, EntityPlayer p)
    {
	return null;
    }

    public String getDetailedActiveDesc(ItemStack is, EntityPlayer p)
    {
	return null;
    }

    public String getDetailedPassiveDesc(ItemStack is, EntityPlayer p)
    {
	return null;
    }

    public static NBTTagCompound getStaffTag_static(ItemStack is)
    {
	return ((Staff) is.getItem()).getStaffTag(is);
    }

    public NBTTagCompound getStaffTag(ItemStack is)
    {
	String tagName = "com.williameze.minegicka3.StaffTag";
	NBTTagCompound isTag = is.getTagCompound();
	if (isTag == null)
	{
	    isTag = new NBTTagCompound();
	    is.setTagCompound(isTag);
	}

	if (!isTag.hasKey(tagName))
	{
	    NBTTagCompound staffTag = getDefaultStaffTag();
	    isTag.setTag(tagName, staffTag);
	}

	NBTTagCompound staffTag = isTag.getCompoundTag(tagName);
	checkStaffTag(staffTag);
	return staffTag;
    }

    public NBTTagCompound getDefaultStaffTag()
    {
	NBTTagCompound staffTag = new NBTTagCompound();
	checkStaffTag(staffTag);
	writeToDefaultNBT(staffTag);
	return staffTag;
    }

    public void writeToDefaultNBT(NBTTagCompound tag)
    {
	if (this == ModBase.hemmyStaff) tag.setInteger("Add queueable", 8);
	if (this == ModBase.staff) tag.setInteger("Add queueable", -1);
	if (this == ModBase.staffSuper) tag.setInteger("Add queueable", 1);
    }

    public void checkStaffTag(NBTTagCompound tag)
    {
	if (!tag.hasKey("Enchanted")) tag.setBoolean("Enchanted", false);
	if (!tag.hasKey("Power")) tag.setDouble("Power", basePower);
	if (!tag.hasKey("ATKSpeed")) tag.setDouble("ATKSpeed", baseATKSpeed);
	if (!tag.hasKey("Consume")) tag.setDouble("Consume", baseConsume);
	if (!tag.hasKey("Recover")) tag.setDouble("Recover", baseRecover);
	if (!tag.hasKey("Modifier")) tag.setString("Modifier", baseModifier.toString());
    }

    public static Staff getStaffItem(ItemStack is)
    {
	if (is != null && is.getItem() instanceof Staff) return (Staff) is.getItem();
	return null;
    }
}
