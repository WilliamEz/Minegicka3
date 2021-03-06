package com.williameze.minegicka3.main.entities.magic.render;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.entities.magic.model.ModelEntitySpray;

public class RenderEntitySpray extends Render
{
    public ModelEntitySpray model = new ModelEntitySpray();

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
	GL11.glTranslated(x, y + var1.width / 2, z);
	GL11.glRotated(Values.clientTicked + var1.hashCode(), var1.motionX, var1.motionY, var1.motionZ);

	DrawHelper.enableLighting(1);
	model.render(var1, partialTick);
	DrawHelper.disableLighting();

	GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
