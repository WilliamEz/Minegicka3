package com.williameze.minegicka3.main.objects.blocks.renders;

import java.awt.Color;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.math.Vector;
import com.williameze.api.models.Ring;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.objects.blocks.TileEntityCraftStation;
import com.williameze.minegicka3.main.objects.blocks.models.ModelCraftStation;

public class RenderTileCraftStation extends TileEntitySpecialRenderer
{
    public static ModelCraftStation model = new ModelCraftStation();

    @Override
    public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float partialTick)
    {
	render((TileEntityCraftStation) var1, x, y, z, partialTick);
    }

    public void render(TileEntityCraftStation tile, double x, double y, double z, float partialTick)
    {
	GL11.glPushMatrix();
	GL11.glTranslated(x, y, z);
	GL11.glRotated(135, 1, 0, 0);
	DrawHelper.enableLighting(1.5F);
	GL11.glRotated(-135, 1, 0, 0);
	model.render(tile, partialTick);
	DrawHelper.disableLighting();
	GL11.glPopMatrix();
    }
}
