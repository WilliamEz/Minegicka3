package com.williameze.minegicka3.main.renders.object;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Box;
import com.williameze.minegicka3.main.objects.blocks.TileEntityShield;
import com.williameze.minegicka3.main.objects.blocks.TileEntityShield.ShieldParticleData;

public class RenderTileEntityShield extends TileEntitySpecialRenderer
{
    public static Box box = Box.create(Vector.root, 1, 1, 0.2);

    @Override
    public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float partialTick)
    {
	render((TileEntityShield) var1, x, y, z, partialTick);
    }

    public void render(TileEntityShield tile, double x, double y, double z, float partialTick)
    {
	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_CULL_FACE);
	// DrawHelper.enableLighting(1F);
	GL11.glTranslated(x, y, z);

	for (int a = 0; a < 16; a++)
	{
	    if (a >= tile.particles.size())
	    {
		tile.particles.add(new ShieldParticleData(tile));
	    }
	    ShieldParticleData spd = tile.particles.get(a);
	    double scale = 0.1 * (spd.hashCode() % 2 + 1);
	    GL11.glPushMatrix();
	    GL11.glTranslated(spd.pos.x, spd.pos.y, spd.pos.z);
	    GL11.glScaled(scale, scale, scale);
	    if (spd.xoryorz == 1) GL11.glRotated(90, 1, 0, 0);
	    else if (spd.xoryorz == 2) GL11.glRotated(90, 0, 1, 0);
	    int alpha = (int) (spd.alphaNoise.noiseAt((tile.life + partialTick) % spd.alphaNoise.range));
	    box.setColor(spd.color.getRGB(), alpha);
	    box.render();
	    GL11.glPopMatrix();
	}

	// DrawHelper.disableLighting();
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glDisable(GL11.GL_BLEND);
	GL11.glPopMatrix();
    }
}
