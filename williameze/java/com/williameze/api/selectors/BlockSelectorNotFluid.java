package com.williameze.api.selectors;

import net.minecraft.world.IBlockAccess;

public class BlockSelectorNotFluid extends BlockSelector
{
    @Override
    public boolean acceptBlock(IBlockAccess world, int x, int y, int z)
    {
        return !world.getBlock(x, y, z).getMaterial().isLiquid();
    }
}
