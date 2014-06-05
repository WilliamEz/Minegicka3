package com.williameze.api.selectors;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockSelectorSolid extends BlockSelector
{
    @Override
    public boolean acceptBlock(IBlockAccess world, int x, int y, int z)
    {
        return world.getBlock(x, y, z).getMaterial().isSolid();
    }
}
