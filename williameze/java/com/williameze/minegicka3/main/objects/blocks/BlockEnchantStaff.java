package com.williameze.minegicka3.main.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;

public class BlockEnchantStaff extends Block implements ITileEntityProvider
{

    public BlockEnchantStaff()
    {
	super(ModBase.magical);
	setHardness(2.5F);
    }

    @Override
    public boolean onBlockActivated(World w, int i, int j, int k, EntityPlayer p, int side, float xOffset, float yOffset, float zOffset)
    {
	if (w.isRemote)
	{
	    p.openGui(ModBase.instance, 2, w, i, j, k);
	}
	return true;
    }

    @Override
    public boolean isOpaqueCube()
    {
	return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
	return false;
    }

    @Override
    public int getRenderType()
    {
	return Values.customRenderId;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
	return new TileEntityEnchantStaff();
    }

}
