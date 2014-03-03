package com.williameze.minegicka3;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabCustom extends CreativeTabs
{
    public Item theItem;
    
    public CreativeTabCustom(String lable)
    {
	super(lable);
    }
    
    public CreativeTabCustom setTabIconItem(Item i)
    {
	theItem = i;
	return this;
    }

    @Override
    public Item getTabIconItem()
    {
	return theItem;
    }

}
