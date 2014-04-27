package com.williameze.minegicka3.main.renders;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.entities.EntityIcicle;

public class RenderEntityIcicle extends Render
{
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

	DrawHelper.enableLighting(1.6F);
	doTheRender((EntityIcicle) var1, partialTick);
	DrawHelper.disableLighting();

	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glPopMatrix();
    }

    public void doTheRender(EntityIcicle ice, float partialTick)
    {
	GL11.glTranslated(-ice.headingX / 2, -ice.headingY / 2, -ice.headingZ / 2);
	if (ice.motionX != 0 && ice.motionY != 0 && ice.motionZ != 0) GL11.glRotated(ice.ticksExisted, ice.headingX, ice.headingY, ice.headingZ);
	Vector v1 = new Vector(0, 0, 0);
	Vector v2 = new Vector(ice.headingX, ice.headingY, ice.headingZ).multiply(((ice.hashCode() % 5) + 3) * 0.2);
	Cylinder cyl = Cylinder.create(v1, v2, 0.1, 0.001, 4 + ice.hashCode() % 5, 0);
	cyl.setColor(Element.Ice.getColor());
	cyl.render();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
