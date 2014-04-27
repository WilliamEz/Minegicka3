package com.williameze.minegicka3.main.renders;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.models.ModelEntityBoulder;
import com.williameze.minegicka3.main.models.ModelEntitySpray;

public class RenderEntityBoulder extends Render
{
    public ModelEntityBoulder model = new ModelEntityBoulder();

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
	GL11.glTranslated(x, y + var1.height / 2, z);
	DrawHelper.enableLighting(1);

	GL11.glRotated(90, 1, 0, 0.3);
	model.render(var1, partialTick);

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
