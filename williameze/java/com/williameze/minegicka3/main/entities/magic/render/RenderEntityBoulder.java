package com.williameze.minegicka3.main.entities.magic.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.entities.magic.EntityBoulder;
import com.williameze.minegicka3.main.entities.magic.model.ModelEntityBoulder;
import com.williameze.minegicka3.mechanics.Element;

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
	GL11.glTranslated(x, y, z);
	DrawHelper.enableLighting(1);

	doTheRender_followLine((EntityBoulder) var1, partialTick);

	GL11.glRotated(90, 1, 0, 0.3);
	GL11.glScaled(1.4, 1.4, 1.4);
	model.render(var1, partialTick);

	DrawHelper.disableLighting();
	GL11.glPopMatrix();
    }

    public void doTheRender_followLine(EntityBoulder bou, float partialTick)
    {
	GL11.glLineWidth(2F);
	for (int b = 0; b < bou.prevPos.size(); b++)
	{
	    Color c = bou.getSpell().elements.get(b % bou.getSpell().countElements()).getColor();
	    List<Vector> prevPos = bou.prevPos.get(b);
	    List<Vector> toRemove = new ArrayList();
	    int max = 20;
	    GL11.glBegin(GL11.GL_LINE_STRIP);
	    for (int a = 0; a < prevPos.size() - 1; a++)
	    {
		Vector now = prevPos.get(a);
		if (prevPos.size() - a >= max)
		{
		    toRemove.add(now);
		    continue;
		}
		double opa = (double) (a - prevPos.size() + max) / (double) max;
		GL11.glColor4d(c.getRed() / 255D, c.getGreen() / 255D, c.getBlue() / 255D, opa);
		GL11.glVertex3d(now.x - ((bou.posX - bou.prevPosX) * partialTick + bou.prevPosX), now.y
			- ((bou.posY - bou.prevPosY) * partialTick + bou.prevPosY), now.z - ((bou.posZ - bou.prevPosZ) * partialTick + bou.prevPosZ));
	    }
	    GL11.glEnd();
	    prevPos.removeAll(toRemove);
	}
	GL11.glLineWidth(1);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
