package com.williameze.minegicka3.main.renders;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.api.models.Ring;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.objects.TileEntityWall;

public class RenderTileEntityWall extends TileEntitySpecialRenderer
{
    public Cylinder cyl = Cylinder.create(new Vector(0, 0, 0), new Vector(0, 1, 0), 0.25, 6);
    public Ring ring = new Ring(new Vector(0, 0, 0), new Vector(0, 1, 0), 0.3, 0.6, 6);

    @Override
    public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float partialTick)
    {
	render((TileEntityWall) var1, x, y, z, partialTick);
    }

    public void render(TileEntityWall tile, double x, double y, double z, float partialTick)
    {
	cyl = Cylinder.create(new Vector(0, 0, 0), new Vector(0, 1, 0), 0.25, 6);
	ring = new Ring(new Vector(0, 0, 0), new Vector(0, 1, 0), 0.4, 0.6, 6);
	if (tile.getSpell() == null) return;
	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glTranslated(x, y, z);
	GL11.glTranslated(0.5, 0, 0.5);

	GL11.glPushMatrix();
	cyl.setColor(tile.getMainColor());
	cyl.render();
	GL11.glPopMatrix();

	int count = tile.getSpell().countElements();
	for (int a = 0; a < count; a++)
	{
	    Element e = tile.getSpell().elements.get(a);
	    ring.setColor(e.getColor().getRGB(), 200);
	    double yRing = (a / (double) count + (tile.life - partialTick) * 0.02) % 1;
	    double rot = (tile.life - partialTick) * 4 + a * 4;
	    GL11.glPushMatrix();
	    GL11.glTranslated(0, yRing, 0);
	    GL11.glRotated(rot, 0, 1, 0);
	    ring.render();
	    GL11.glPopMatrix();
	    GL11.glPushMatrix();
	    GL11.glTranslated(0, 1 - yRing, 0);
	    GL11.glRotated(-rot, 0, 1, 0);
	    ring.render();
	    GL11.glPopMatrix();
	}

	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glDisable(GL11.GL_BLEND);
	GL11.glPopMatrix();
    }
}
