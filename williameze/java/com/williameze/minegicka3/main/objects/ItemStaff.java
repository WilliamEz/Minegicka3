package com.williameze.minegicka3.main.objects;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.williameze.minegicka3.ClientProxy;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.core.CoreClient;
import com.williameze.minegicka3.main.models.ModelStaff;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStaff extends Item
{
    private ItemStack currentUsing;
    private Object model;
    public static int useCount;
    public double basePower;
    public double baseATKSpeed;
    public double baseConsume;
    public double baseRecover;

    public ItemStaff()
    {
	super();
	ModBase.proxy.registerItemRenderer(this);
	setMaxStackSize(1);
	setCreativeTab(ModBase.modCreativeTab);
	setBaseStats(1, 1, 1, 1);
    }

    @SideOnly(Side.CLIENT)
    public ItemStaff setModel(ModelStaff m)
    {
	model = m;
	return this;
    }

    @SideOnly(Side.CLIENT)
    public ModelStaff getModel()
    {
	return (ModelStaff) (model == null ? (model = ModelStaff.defaultStaffModel) : model);
    }

    public ItemStaff setBaseStats(double power, double atkspd, double consume, double recover)
    {
	basePower = power;
	baseATKSpeed = atkspd;
	baseConsume = consume;
	baseRecover = recover;
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

    @Override
    public void onPlayerStoppedUsing(ItemStack is, World w, EntityPlayer p, int par4)
    {
	super.onPlayerStoppedUsing(is, w, p, par4);
	clientStopUse(w, p, is);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer p)
    {
	p.setItemInUse(is, this.getMaxItemUseDuration(is));
	clientStartUse(w, p, is);
	return super.onItemRightClick(is, w, p);
    }

    @Override
    public boolean onItemUse(ItemStack is, EntityPlayer p, World w, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
	p.setItemInUse(is, this.getMaxItemUseDuration(is));
	clientStartUse(w, p, is);
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
    }

    public void unequipStaff(World world, EntityPlayer e, ItemStack is)
    {
	clientStopUse(world, e, is);
    }

    public void clientStartUse(World world, EntityPlayer player, ItemStack is)
    {
	if (world.isRemote)
	{
	    if (currentUsing == null)
	    {
		useCount = 0;
		currentUsing = is;
		ModBase.proxy.getCoreClient().clientStartUsingStaff(world, player, currentUsing);
	    }
	    else if (!ItemStack.areItemStacksEqual(currentUsing, is))
	    {
		clientStopUse(world, player, currentUsing);
		clientStartUse(world, player, is);
	    }
	}
    }

    public void clientStopUse(World world, EntityPlayer player, ItemStack is)
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

    @SideOnly(Side.CLIENT)
    public ModelStaff getModel(ItemStack is)
    {
	return getModel();
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

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean advancedItemTooltip)
    {
	super.addInformation(is, p, l, advancedItemTooltip);

	if (this == ModBase.staff)
	{
	    String s = (String) l.get(0);
	    s += EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.ITALIC + (GuiScreen.isShiftKeyDown() ? "" : " (press Shift for more details)");
	    l.set(0, s);
	}

	NBTTagCompound tag = getStaffTag(is);
	if (!GuiScreen.isShiftKeyDown())
	{
	    String power = EnumChatFormatting.RED + "" + Math.round(tag.getDouble("Power") * 100) + "%" + EnumChatFormatting.GRAY;
	    String atkSpeed = EnumChatFormatting.GREEN + "" + Math.round(tag.getDouble("ATKSpeed") * 100) + "%" + EnumChatFormatting.GRAY;
	    String consume = EnumChatFormatting.BLUE + "" + Math.round(tag.getDouble("Consume") * 100) + "%" + EnumChatFormatting.GRAY;
	    String recover = EnumChatFormatting.LIGHT_PURPLE + "" + Math.round(tag.getDouble("Recover") * 100) + "%" + EnumChatFormatting.GRAY;
	    l.add("[ " + power + " ; " + atkSpeed + " ; " + consume + " ; " + recover + " ]");
	}
	else
	{

	    l.add("[ " + EnumChatFormatting.RED + "Power: " + Math.round(tag.getDouble("Power") * 100) + "%" + EnumChatFormatting.GRAY + " ]");
	    l.add("[ " + EnumChatFormatting.GREEN + "ATK Rate: " + Math.round(tag.getDouble("ATKSpeed") * 100) + "%" + EnumChatFormatting.GRAY + " ]");
	    l.add("[ " + EnumChatFormatting.BLUE + "Mana Consume Rate: " + Math.round(tag.getDouble("Consume") * 100) + "%" + EnumChatFormatting.GRAY + " ]");
	    l.add("[ " + EnumChatFormatting.LIGHT_PURPLE + "Mana Recovery Rate: " + Math.round(tag.getDouble("Recover") * 100) + "%" + EnumChatFormatting.GRAY + " ]");
	}
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
	staffTag.setDouble("Power", basePower);
	staffTag.setDouble("ATKSpeed", baseATKSpeed);
	staffTag.setDouble("Consume", baseConsume);
	staffTag.setDouble("Recover", baseRecover);
	return staffTag;
    }

    public void checkStaffTag(NBTTagCompound tag)
    {
	if (!tag.hasKey("Power")) tag.setDouble("Power", basePower);
	if (!tag.hasKey("ATKSpeed")) tag.setDouble("ATKSpeed", baseATKSpeed);
	if (!tag.hasKey("Consume")) tag.setDouble("Consume", baseConsume);
	if (!tag.hasKey("Recover")) tag.setDouble("Recover", baseRecover);
    }
}
