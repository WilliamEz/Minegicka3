package com.williameze.minegicka3.main.objects;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.PlayersData;
import com.williameze.minegicka3.main.Element;

public class ItemElementStick extends Item
{
    public Element unlocking;

    public ItemElementStick(Element e)
    {
	super();
	unlocking = e;
	ModBase.proxy.registerItemRenderer(this);
    }

    public String getUnlockingColor()
    {
	if (unlocking == Element.Arcane) return EnumChatFormatting.DARK_RED + "";
	else if (unlocking == Element.Cold) return EnumChatFormatting.WHITE + "";
	else if (unlocking == Element.Earth) return EnumChatFormatting.GOLD + "";
	else if (unlocking == Element.Fire) return EnumChatFormatting.RED + "";
	else if (unlocking == Element.Ice) return EnumChatFormatting.AQUA + "";
	else if (unlocking == Element.Life) return EnumChatFormatting.GREEN + "";
	else if (unlocking == Element.Lightning) return EnumChatFormatting.LIGHT_PURPLE + "";
	else if (unlocking == Element.Shield) return EnumChatFormatting.YELLOW + "";
	else if (unlocking == Element.Steam) return EnumChatFormatting.DARK_GRAY + "";
	else if (unlocking == Element.Water) return EnumChatFormatting.BLUE + "";
	else return "";
    }

    @Override
    public void addInformation(ItemStack is, EntityPlayer p, List l, boolean par4)
    {
	super.addInformation(is, p, l, par4);
	if (unlocking == null)
	{
	    l.add("-You can learn spells from eating a stick!?");
	    l.add(EnumChatFormatting.ITALIC + "-Apparently yes!");
	}
	else
	{
	    String unlockingColor = getUnlockingColor();
	    l.set(0, unlockingColor + l.get(0));
	    String finalOutput = "";
	    if (is.hashCode() % 101 == 0) finalOutput = "You have me already!";

	    if (!PlayersData.getPlayerData_static(p).isUnlocked(unlocking))
	    {
		finalOutput = "Eat me and you shall obtain";
		if (is.hashCode() % 100 > 50) finalOutput = "I wield within myself the power of ";
		else if (is.hashCode() % 100 == 0) finalOutput = "Stick me into your butt and you shall have ";
		String element = unlockingColor + unlocking.toString();
		finalOutput += element;
	    }
	    if (finalOutput.length() > 0) l.add(finalOutput);
	}
    }

    @Override
    public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer p)
    {
	if (unlocking != null)
	{
	    if (!PlayersData.getPlayerData_static(p).isUnlocked(unlocking))
	    {
		if (w.isRemote)
		{
		    String text = "You have acquired ";
		    if (is.hashCode() % 100 > 50) text = "You now wield the power of ";
		    else if (is.hashCode() % 100 == 0) text = "Sticking a stick into your butt gave you ";
		    String element = getUnlockingColor() + "[" + unlocking.toString() + "]";
		    text += element;
		    p.addChatMessage(new ChatComponentText(text));
		}
		PlayersData.getPlayerData_static(p).unlock(unlocking);
		is.stackSize--;
		return is;
	    }
	}
	return super.onItemRightClick(is, w, p);
    }
}
