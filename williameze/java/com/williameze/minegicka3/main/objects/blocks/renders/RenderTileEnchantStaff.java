package com.williameze.minegicka3.main.objects.blocks.renders;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.minegicka3.main.objects.blocks.TileEntityEnchantStaff;
import com.williameze.minegicka3.main.objects.blocks.models.ModelEnchantStaff;

public class RenderTileEnchantStaff extends TileEntitySpecialRenderer
{
    public static ModelEnchantStaff model = new ModelEnchantStaff();

    @Override
    public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float partialTick)
    {
	render((TileEntityEnchantStaff) var1, x, y, z, partialTick);
    }

    public void render(TileEntityEnchantStaff tile, double x, double y, double z, float partialTick)
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
