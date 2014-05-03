package com.williameze.minegicka3.main.renders;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.math.Vector;
import com.williameze.api.models.Ring;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.entities.EntityMine;

public class RenderEntityMine extends Render
{
    public static Sphere sphere = new Sphere(0, 0, 0, 0.75, 2, 4);
    public static Ring ring = new Ring(Vector.root.copy(), Vector.unitY.copy(), 0.85, 1, 6);

    protected void bindEntityTexture(Entity par1Entity)
    {
    }

    protected void bindTexture(ResourceLocation par1ResourceLocation)
    {
    }

    @Override
    public void doRender(Entity var1, double x, double y, double z, float yaw, float partialTick)
    {
	sphere = new Sphere(0, 0, 0, 0.75, 2, 4);
	EntityMine mine = (EntityMine) var1;
	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glEnable(GL11.GL_LIGHTING);
	GL11.glPushMatrix();
	GL11.glTranslated(x, y + var1.height / 2, z);
	GL11.glScaled(0.25, 0.25, 0.25);

	if (mine.spell.hasElement(Element.Arcane))
	{
	    sphere.setColor(Element.Arcane.getColor());
	}
	else
	{
	    sphere.setColor(Element.Life.getColor());
	}

	sphere.render();
	for (int a = 1; a <= mine.spell.countElements(); a++)
	{
	    double yRing = (-1D + (2D * (a / (double) mine.spell.countElements()) + 0.01 * (mine.ticksExisted + partialTick)) % 2) * 0.6;
	    ring.setColor(mine.spell.elements.get(a - 1).getColor().getRGB(), (int) (255D * (1 - Math.abs(yRing / 0.6))));
	    GL11.glPushMatrix();
	    GL11.glTranslated(0, yRing, 0);
	    GL11.glRotated((mine.ticksExisted + partialTick + a) * 8, 0, 1, 0);
	    ring.render();
	    GL11.glPopMatrix();
	}

	GL11.glPopMatrix();
	GL11.glDisable(GL11.GL_LIGHTING);
	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
