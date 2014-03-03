package com.williameze.minegicka3.objects;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.rendering.models.ModelStaff;

public class ItemStaff extends Item
{
    ModelStaff model;
    double basePower;
    double baseConsume;
    double baseRecharge;

    public ItemStaff()
    {
	super();
	ModBase.proxy.registerItemRenderer(this);
	setMaxStackSize(1);
	setCreativeTab(ModBase.modCreativeTab);
	setModel(ModelStaff.defaultModel);
	setBaseStats(1, 1, 1);
    }
    
    public ItemStaff setModel(ModelStaff m)
    {
	model = m;
	return this;
    }
    
    public ItemStaff setBaseStats(double i, double j, double k)
    {
	basePower = i;
	baseConsume = j;
	baseRecharge = k;
	return this;
    }
    
    public ModelStaff getModel(ItemStack is)
    {
	return model;
    }
    
    public double getPower(ItemStack is)
    {
	return getStaffTag(is).getDouble("Power");
    }

    public double getConsume(ItemStack is)
    {
	return getStaffTag(is).getDouble("Consume");
    }

    public double getRecharge(ItemStack is)
    {
	return getStaffTag(is).getDouble("Recharge");
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean advancedItemTooltip)
    {
	super.addInformation(is, p, l, advancedItemTooltip);
	NBTTagCompound tag = getStaffTag(is);
	String power = EnumChatFormatting.RED+""+Math.round(tag.getDouble("Power")*100)+"%";
	String consume = EnumChatFormatting.GREEN+""+Math.round(tag.getDouble("Consume")*100)+"%";
	String recharge = EnumChatFormatting.BLUE+""+Math.round(tag.getDouble("Recharge")*100)+"%";
	l.add("[ "+power+EnumChatFormatting.GRAY+" ; "+consume+EnumChatFormatting.GRAY+" ; "+recharge+EnumChatFormatting.GRAY+" ]");
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
	    return staffTag;
	}
	else return isTag.getCompoundTag(tagName);
    }

    public NBTTagCompound getDefaultStaffTag()
    {
	NBTTagCompound staffTag = new NBTTagCompound();
	staffTag.setDouble("Power", basePower);
	staffTag.setDouble("Consume", baseConsume);
	staffTag.setDouble("Recharge", baseRecharge);
	return staffTag;
    }
}
