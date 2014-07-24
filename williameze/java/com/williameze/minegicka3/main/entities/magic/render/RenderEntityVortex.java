package com.williameze.minegicka3.main.entities.magic.render;

import java.awt.Color;
import java.util.Random;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.models.DPStar;
import com.williameze.api.models.ModelBase;

public class RenderEntityVortex extends Render
{
    public DPStar star = (DPStar) new DPStar(0, 0, 0, 0.7, 0.9, 64).setOuterColor(0xaa0099, 255).setCenterColor(0x400040, 255)
	    .setColor(0x880096, 255);
    public ModelBase model = new ModelBase(star);

    protected void bindEntityTexture(Entity par1Entity)
    {
    }

    protected void bindTexture(ResourceLocation par1ResourceLocation)
    {
    }

    @Override
    public void doRender(Entity var1, double x, double y, double z, float yaw, float partialTick)
    {
	Random rnd = new Random();
	star.initialRotation = (var1.ticksExisted + partialTick);
	star.setOuterColor(new Color(130 + rnd.nextInt(80), 0, 130 + rnd.nextInt(80), 110));
	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glTranslated(x, y, z);

	star.setOpposing(-x, -y, -z);
	model.render(var1, partialTick);

	GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
