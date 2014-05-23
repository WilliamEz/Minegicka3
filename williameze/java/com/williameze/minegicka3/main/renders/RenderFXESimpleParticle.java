package com.williameze.minegicka3.main.renders;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.math.Vector;
import com.williameze.api.models.Box;
import com.williameze.api.models.ModelObject;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.main.entities.FXESimpleParticle;

public class RenderFXESimpleParticle extends Render
{
    public ModelObject box = Box.create(Vector.root.copy(), 0.5);
    public ModelObject octa = new Sphere(0, 0, 0, 0.5, 2, 4);
    public ModelObject sphere = new Sphere(0, 0, 0, 0.5, 16, 32);

    protected void bindEntityTexture(Entity par1Entity)
    {
    }

    protected void bindTexture(ResourceLocation par1ResourceLocation)
    {
    }

    @Override
    public void doRender(Entity var1, double x, double y, double z, float yaw, float partialTick)
    {
	GL11.glPushMatrix();
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glTranslated(x, y, z);

	RenderHelper.enableStandardItemLighting();
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	// DrawHelper.enableLighting(0.65F);
	if (var1 instanceof FXESimpleParticle)
	{
	    FXESimpleParticle fx = (FXESimpleParticle) var1;
	    ModelObject model;
	    if (((FXESimpleParticle) var1).renderType == 2) model = sphere;
	    else if (((FXESimpleParticle) var1).renderType == 1) model = octa;
	    else model = box;

	    GL11.glScaled(var1.width, var1.height, var1.width);
	    double alphaRate = 1;
	    if (fx.alphaDrops > 0)
	    {
		alphaRate = (double) fx.life / (fx.maxLife + 1D);
		alphaRate = Math.pow(alphaRate, fx.alphaDrops);
	    }
	    model.setColor(fx.color.getRGB(), (int) (fx.alpha * alphaRate * 255D));
	    model.render();
	}
	// DrawHelper.disableLighting();
	RenderHelper.disableStandardItemLighting();

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
