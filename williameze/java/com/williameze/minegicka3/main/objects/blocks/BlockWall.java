package com.williameze.minegicka3.main.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.williameze.api.math.IntVector;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;

public class BlockWall extends Block implements ITileEntityProvider
{

    public BlockWall()
    {
	super(ModBase.magical);
	lightOpacity = 0;
	blockParticleGravity = 1.5F;
	setLightLevel(0.2F);
	setBlockUnbreakable();
	setResistance(5);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
	return null;
    }

    @Override
    public boolean onBlockEventReceived(World w, int x, int y, int z, int i, int j)
    {
	return ((TileEntityWall) w.getTileEntity(x, y, z)).receiveClientEvent(i, j);
    }

    @Override
    public void onBlockClicked(World w, int x, int y, int z, EntityPlayer p)
    {
	super.onBlockClicked(w, x, y, z, p);
	TileEntity t = w.getTileEntity(x, y, z);
	if (t instanceof TileEntityWall) ((TileEntityWall) t).blockClicked(p);
    }

    @Override
    public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity e)
    {
	super.onEntityCollidedWithBlock(w, x, y, z, e);
	((TileEntityWall) w.getTileEntity(x, y, z)).entityCollide(e);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
	return new TileEntityWall();
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
