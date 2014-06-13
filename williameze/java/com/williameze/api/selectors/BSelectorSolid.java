package com.williameze.api.selectors;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BSelectorSolid extends BSelector
{
    @Override
    public boolean acceptBlock(IBlockAccess world, int x, int y, int z)
    {
        return world.getBlock(x, y, z).getMaterial().isSolid();
    }
}
