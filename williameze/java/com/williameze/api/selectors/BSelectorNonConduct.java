package com.williameze.api.selectors;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BSelectorNonConduct extends BSelector
{
    @Override
    public boolean acceptBlock(IBlockAccess world, int x, int y, int z)
    {
	Material mat = world.getBlock(x, y, z).getMaterial();
	return world.getBlock(x, y, z).getMaterial().isSolid() && mat != Material.anvil && mat != Material.circuits && mat != Material.iron
		&& mat != Material.redstoneLight;
    }
}
