package com.williameze.minegicka3.mechanics.magicks;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;

public class ESelectorItemAndXP implements IEntitySelector
{
    @Override
    public boolean isEntityApplicable(Entity var1)
    {
	return var1 instanceof EntityItem || var1 instanceof EntityXPOrb;
    }
}
