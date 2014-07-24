package com.williameze.minegicka3.main.objects.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.functional.PlayersData;
import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.magicks.Magick;

public class ItemMagickTablet extends Item
{
    public ItemMagickTablet()
    {
	super();
	setMaxStackSize(1);
	setTextureName("apple");
	ModBase.proxy.registerItemRenderer(this);
    }

    @Override
    public String getItemStackDisplayName(ItemStack is)
    {
	Magick m = getUnlocking(is);
	if (m != null)
	{
	    String magickname = " of " + EnumChatFormatting.YELLOW + m.getDisplayName();
	    return super.getItemStackDisplayName(is) + magickname;
	}
	return super.getItemStackDisplayName(is);
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean par4)
    {
	super.addInformation(is, p, l, par4);
	Magick m = getUnlocking(is);
	if (m != null)
	{
	    String magickname = " of " + EnumChatFormatting.YELLOW + m.getDisplayName();
	    // l.set(0, (String) l.get(0) + magickname);
	    String comb = EnumChatFormatting.ITALIC + "[ ";
	    for (int a = 0; a < m.getCombination().length; a++)
	    {
		Element e = m.getCombination()[a];
		if (PlayersData.getPlayerData_static(p).isUnlocked(e))
		{
		    comb += e.toString();
		}
		else comb += "Unknown";
		if (a < m.getCombination().length - 1) comb += " ; ";
	    }
	    comb += " ]";
	    l.add(comb);
	}
    }

    @Override
    public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer p)
    {
	Magick m = getUnlocking(is);
	if (m != null)
	{
	    if (!PlayersData.getPlayerData_static(p).isUnlocked(m))
	    {
		PlayersData.getPlayerData_static(p).unlock(m);
		is.stackSize--;
		if (w.isRemote)
		{
		    String mgk = EnumChatFormatting.YELLOW + m.getDisplayName() + EnumChatFormatting.RESET;
		    String s = "Good job. " + mgk + " acquired.";
		    if (is.hashCode() % 100 > 50) s = "You have learnt " + mgk + " succesfully.";
		    else if (is.hashCode() % 100 == 0) s = "You have just finished reading the instruction manual of " + mgk + ", newb!";
		    p.addChatMessage(new ChatComponentText(s));
		}
		return is;
	    }
	}
	return super.onItemRightClick(is, w, p);
    }

    public ItemStack setUnlocking(ItemStack is, Magick m)
    {
	if (is.stackTagCompound == null) is.stackTagCompound = new NBTTagCompound();
	is.stackTagCompound.setInteger("UnlockMagick", m.getID() + 1);
	return is;
    }

    public Magick getUnlocking(ItemStack is)
    {
	if (is == null) return null;
	if (is.stackTagCompound == null) return null;
	int id = is.stackTagCompound.getInteger("UnlockMagick") - 1;
	if (id >= 0) return Magick.getMagickFromID(id);
	else return null;
    }

    @Override
    public void getSubItems(Item i, CreativeTabs tab, List l)
    {
	l.addAll(allMagickPages());
    }

    public List<ItemStack> allMagickPages()
    {
	List<ItemStack> l = new ArrayList();
	for (Magick m : Magick.magicks.values())
	{
	    ItemStack is = new ItemStack(this);
	    setUnlocking(is, m);
	    l.add(is);
	}
	return l;
    }
}
