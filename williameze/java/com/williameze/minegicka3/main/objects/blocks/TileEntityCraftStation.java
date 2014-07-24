package com.williameze.minegicka3.main.objects.blocks;

import net.minecraft.tileentity.TileEntity;

public class TileEntityCraftStation extends TileEntity
{
    public TileEntityCraftStation()
    {
    }

    @Override
    public boolean canUpdate()
    {
	return false;
    }
}
