package com.williameze.minegicka3.main.renders;

import org.lwjgl.opengl.GL11;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.models.ModelCraftStation;
import com.williameze.minegicka3.main.objects.TileEntityCraftStation;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockCustomRenderer implements ISimpleBlockRenderingHandler
{
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
	if (modelId == getRenderId())
	{
	    renderInventoryBlock(block, metadata, renderer);
	}
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
	if (modelId == getRenderId())
	{
	    renderWorldBlock(world, x, y, z, block, renderer);
	    return true;
	}
	return false;
    }

    public void renderInventoryBlock(Block block, int metadata, RenderBlocks renderer)
    {
	if (block == ModBase.craftStation)
	{
	    TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEntityCraftStation(), -0.5, -0.5, -0.5, 0);
	    RenderHelper.enableStandardItemLighting();
	}
    }

    public void renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer)
    {
    }

    public void renderCraftStation()
    {
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
	return modelId == getRenderId();
    }

    @Override
    public int getRenderId()
    {
	return Values.customRenderId;
    }

}
