package com.williameze.minegicka3.main.renders;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.math.Vector;
import com.williameze.api.models.Box;
import com.williameze.api.models.ModelObject;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.main.entities.FXEProjectileCharge;

public class FXERenderProjectileCharge extends Render
{
    public ModelObject model = new Sphere(0, 0, 0, 0.5, 2, 4);

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
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glTranslated(x, y, z);

	DrawHelper.enableLighting(0.3F);
	if (var1 instanceof FXEProjectileCharge)
	{
	    GL11.glScaled(var1.width, var1.height, var1.width);
	    model.setColor(((FXEProjectileCharge) var1).color.getRGB(),
		    (int) (((FXEProjectileCharge) var1).alpha * 255));
	    model.render();
	}
	DrawHelper.disableLighting();

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
