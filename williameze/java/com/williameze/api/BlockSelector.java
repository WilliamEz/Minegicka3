package com.williameze.api;

import net.minecraft.world.IBlockAccess;

public class BlockSelector
{
    public boolean acceptBlock(IBlockAccess world, int x, int y, int z)
    {
	return true;
    }
}
