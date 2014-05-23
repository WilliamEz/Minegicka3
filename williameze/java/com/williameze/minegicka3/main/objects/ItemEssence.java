package com.williameze.minegicka3.main.objects;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.PlayersData;
import com.williameze.minegicka3.main.Element;

public class ItemEssence extends Item
{
    public Element unlocking;

    public ItemEssence(Element e)
    {
	super();
	unlocking = e;
	setTextureName("apple");
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
	if (unlocking != null)
	{
	    String unlockingColor = getUnlockingColor();
	    l.set(0, unlockingColor + l.get(0));
	    String finalOutput = "";
	    if (!PlayersData.getPlayerData_static(p).isUnlocked(unlocking))
	    {
		finalOutput = "Unlockable ";
		String element = unlockingColor + "[" + unlocking.toString() + "]";
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
