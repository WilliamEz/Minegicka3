package com.williameze.minegicka3.main.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.williameze.api.math.IntVector;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;

public class BlockShield extends Block implements ITileEntityProvider
{

    public BlockShield()
    {
	super(ModBase.magical);
	lightOpacity = 0;
	useNeighborBrightness = false;
	slipperiness = 1;
	blockParticleGravity = 0;
	setLightLevel(0.35F);
	setBlockUnbreakable();
	setResistance(5);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
	return null;
    }

    @Override
    public void onBlockClicked(World w, int x, int y, int z, EntityPlayer p)
    {
	super.onBlockClicked(w, x, y, z, p);
	TileEntity t = w.getTileEntity(x, y, z);
	if (t instanceof TileEntityShield) ((TileEntityShield) t).blockClicked(p);
    }

    @Override
    public int onBlockPlaced(World w, int x, int y, int z, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_,
	    int p_149660_9_)
    {
	return super.onBlockPlaced(w, x, y, z, p_149660_5_, p_149660_6_, p_149660_7_, p_149660_8_, p_149660_9_);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
	return new TileEntityShield();
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
	return -1;
    }

    @Override
    public void dropBlockAsItemWithChance(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_,
	    float p_149690_6_, int p_149690_7_)
    {
    }

    @Override
    protected void dropBlockAsItem(World p_149642_1_, int p_149642_2_, int p_149642_3_, int p_149642_4_, ItemStack p_149642_5_)
    {
    }

    @Override
    public void dropXpOnBlockBreak(World p_149657_1_, int p_149657_2_, int p_149657_3_, int p_149657_4_, int p_149657_5_)
    {
    }
}
