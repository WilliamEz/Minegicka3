package com.williameze.minegicka3.main.renders.object;

import java.awt.Color;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.lib.NoiseGen1D;
import com.williameze.api.math.Vector;
import com.williameze.api.models.Box;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.main.objects.blocks.TileEntityWall;

public class RenderTileEntityWall extends TileEntitySpecialRenderer
{
    public static NoiseGen1D heightNoise = new NoiseGen1D(null, 100);
    public static Box body = Box.create(new Vector(0, 0.5, 0), 0.5);
    public static Sphere tip = new Sphere(0, 0, 0, 0.5 * Math.sqrt(2), 2, 4);

    public static void load()
    {
	heightNoise.setCap(-0.5, 0.3);
	heightNoise.setDifs(0.2, 0.05);
	heightNoise.generate();
    }

    @Override
    public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float partialTick)
    {
	render((TileEntityWall) var1, x, y, z, partialTick);
    }

    public void render(TileEntityWall tile, double x, double y, double z, float partialTick)
    {
	if (tile.getSpell() == null) return;
	if (!tile.isRootWall()) return;

	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glTranslated(x, y, z);
	DrawHelper.enableLighting(1);

	int height = tile.instanceWallHeight();
	int maxX = 2;
	int maxZ = maxX;
	double spikeWidthRate = 1.4;
	for (int a = 0; a < maxX; a++)
	{
	    for (int b = 0; b < maxZ; b++)
	    {
		GL11.glPushMatrix();
		GL11.glTranslated(1D / maxX * (a + 0.5), 0, 1D / maxZ * (b + 0.5));
		double noiseAt = heightNoise.noiseAt((tile.hashCode() + a + b) % heightNoise.range);
		double eachHeight = height + noiseAt;
		double tipHeight = eachHeight * (Math.abs(noiseAt) + 0.2) / 1.4;
		Color cBase = tile.getSpell().elements.get((tile.hashCode() + a + b) % tile.getSpell().countElements()).getColor();
		renderASpike(cBase, 1D / maxX * spikeWidthRate, eachHeight - tipHeight, 1D / maxZ * spikeWidthRate, tipHeight);
		GL11.glPopMatrix();
	    }
	}

	DrawHelper.disableLighting();
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glDisable(GL11.GL_BLEND);
	GL11.glPopMatrix();
    }

    public void renderASpike(Color c, double xWidth, double height, double zWidth, double tipHeight)
    {
	GL11.glPushMatrix();
	GL11.glRotated(45, 0, 1, 0);
	GL11.glScaled(xWidth, 1, zWidth);

	GL11.glScaled(1, height, 1);
	body.setColor(c);
	body.render();
	GL11.glScaled(1, 1D / height, 1);

	GL11.glTranslated(0, height, 0);
	GL11.glScaled(1, tipHeight, 1);
	GL11.glRotated(45, 0, 1, 0);
	tip.setColor(c);
	tip.render();

	GL11.glPopMatrix();
    }
}
