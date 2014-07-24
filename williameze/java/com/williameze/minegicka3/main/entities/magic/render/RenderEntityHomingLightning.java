package com.williameze.minegicka3.main.entities.magic.render;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.models.DPStar;
import com.williameze.api.models.ModelBase;
import com.williameze.api.models.Sphere;

public class RenderEntityHomingLightning extends Render
{
    public ModelBase model = new ModelBase(new Sphere(0, 0, 0, 0.16, 2, 16).setColor(0xffff77, 150));

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
	GL11.glTranslated(x, y, z);

	model.render(var1, partialTick);

	GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
