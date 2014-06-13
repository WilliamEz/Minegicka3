package com.williameze.minegicka3.main.renders.entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.entities.magic.EntityIcicle;

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

	//DrawHelper.enableLighting(2F);
	doTheRender((EntityIcicle) var1, partialTick);
	doTheRender_followLine((EntityIcicle) var1, partialTick);
	//DrawHelper.enableLighting(1F);
	//DrawHelper.disableLighting();

	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glPopMatrix();
    }

    public void doTheRender(EntityIcicle ice, float partialTick)
    {
	GL11.glTranslated(-ice.headingX / 2, -ice.headingY / 2, -ice.headingZ / 2);
	GL11.glPushMatrix();
	if (ice.motionX != 0 && ice.motionY != 0 && ice.motionZ != 0) GL11.glRotated((ice.ticksExisted + partialTick) * 4D, ice.headingX, ice.headingY, ice.headingZ);
	Vector v1 = new Vector(0, 0, 0);
	Vector v2 = new Vector(ice.headingX, ice.headingY, ice.headingZ).multiply(((ice.hashCode() % 5) + 3) * 0.2);
	Cylinder cyl = Cylinder.create(v1, v2, 0.1, 0.001, 4 + ice.hashCode() % 5, 0);
	cyl.setColor(Element.Ice.getColor());
	cyl.render();
	GL11.glPopMatrix();
    }

    public void doTheRender_followLine(EntityIcicle ice, float partialTick)
    {
	List<Vector> toRemove = new ArrayList();
	int max = 60;
	GL11.glLineWidth(2F);
	GL11.glBegin(GL11.GL_LINE_STRIP);
	for (int a = 0; a < ice.prevPos.size() - 1; a++)
	{
	    Vector now = ice.prevPos.get(a);
	    if (ice.prevPos.size() - a >= max)
	    {
		toRemove.add(now);
		continue;
	    }
	    double opa = (double) (a - ice.prevPos.size() + max) / (double) max;
	    Color c = ice.getSpell().elements.get((ice.hashCode() + a) % ice.getSpell().countElements()).getColor();
	    GL11.glColor4d(c.getRed() / 255D, c.getGreen() / 255D, c.getBlue() / 255D, opa);
	    GL11.glVertex3d(now.x - ((ice.posX - ice.prevPosX) * partialTick + ice.prevPosX), now.y - ((ice.posY - ice.prevPosY) * partialTick + ice.prevPosY), now.z
		    - ((ice.posZ - ice.prevPosZ) * partialTick + ice.prevPosZ));
	}
	ice.prevPos.removeAll(toRemove);
	GL11.glEnd();
	GL11.glLineWidth(1);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
