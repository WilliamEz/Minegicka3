package com.williameze.minegicka3.main.renders;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.lib.NoiseGen2D;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.entities.EntityBeamArea;

public class RenderEntityBeamArea extends Render
{
    public static List<NoiseGen2D> pregeneratedNoises = new ArrayList();

    public static void load()
    {
	pregeneratedNoises.clear();
	for (int a = 0; a < 16; a++)
	{
	    NoiseGen2D noise = new NoiseGen2D(pregeneratedNoises.hashCode(), 385, 385);
	    noise.setCap(-2, 2);
	    noise.setNoisetainProps((noise.rangeX - 1) / 16, (noise.rangeX - 1) / 6, 2);
	    noise.reset(0);
	    noise.setFixed((noise.rangeX - 1) / 2, (noise.rangeY - 1) / 2, true);
	    noise.generate(32, false, 0);
	    noise.smooth(1, 8);
	    pregeneratedNoises.add(noise);
	}
    }

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
	DrawHelper.enableLighting(1.6f);

	renderBeamArea((EntityBeamArea) var1, partialTick);

	DrawHelper.disableLighting();
	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glPopMatrix();
    }

    public void renderBeamArea(EntityBeamArea ba, float partialTick)
    {
	NoiseGen2D noise = pregeneratedNoises.get(ba.hashCode() % pregeneratedNoises.size());

	double maxRadiusRate = (noise.rangeX - 1) / 2D / ba.maxRange();
	for (int a = 0; a < ba.spell.elements.size(); a++)
	{
	    Element e1 = ba.spell.elements.get(a);
	    Element e2 = a >= ba.spell.elements.size() - 1 ? e1 : ba.spell.elements.get(a + 1);
	    double outRadius = ba.maxRange() * (ba.ticksExisted + partialTick + 1D) / (double) ba.maxTick() - 2D * a;
	    if (outRadius > 0)
	    {
		double inRadius = Math.max(outRadius - 2, 0);
		double radiusDif = outRadius - inRadius;
		Color colorOut = e1.getColor();
		Color colorIn = e2.getColor();
		int loop = 4;
		for (int b = 0; b < loop; b++)
		{
		    double rOut = outRadius - radiusDif / loop * b;
		    double rIn = rOut - radiusDif / loop;
		    double redOut = ((rOut - inRadius) / radiusDif * (colorOut.getRed() - colorIn.getRed()) + colorIn.getRed()) / 255D;
		    double greenOut = ((rOut - inRadius) / radiusDif * (colorOut.getGreen() - colorIn.getGreen()) + colorIn.getGreen()) / 255D;
		    double blueOut = ((rOut - inRadius) / radiusDif * (colorOut.getBlue() - colorIn.getBlue()) + colorIn.getBlue()) / 255D;
		    double redIn = ((rIn - inRadius) / radiusDif * (colorOut.getRed() - colorIn.getRed()) + colorIn.getRed()) / 255D;
		    double greenIn = ((rIn - inRadius) / radiusDif * (colorOut.getGreen() - colorIn.getGreen()) + colorIn.getGreen()) / 255D;
		    double blueIn = ((rIn - inRadius) / radiusDif * (colorOut.getBlue() - colorIn.getBlue()) + colorIn.getBlue()) / 255D;

		    int circleLoop = (int) ((ba.ticksExisted + 1D + partialTick) / ba.maxTick() * ba.maxRange() * 4);
		    Vector base = Vector.unitX.copy();
		    GL11.glPushMatrix();
		    GL11.glBegin(GL11.GL_QUAD_STRIP);
		    for (int c = 0; c < circleLoop + 1; c++)
		    {
			Vector baseVec = base.rotateAround(Vector.unitY, Math.PI * 2 / circleLoop * c);
			Vector v1 = baseVec.copy().multiply(rOut);
			v1.y = noise.getValueAt((noise.rangeX - 1) / 2D - v1.x * maxRadiusRate, (noise.rangeY - 1) / 2D - v1.z * maxRadiusRate);
			Vector v2 = baseVec.copy().multiply(rIn);
			v2.y = noise.getValueAt((noise.rangeX - 1) / 2D - v2.x * maxRadiusRate, (noise.rangeY - 1) / 2D - v2.z * maxRadiusRate);
			baseVec = baseVec.rotateAround(Vector.unitY, Math.PI * 2 / circleLoop);
			Vector v3 = baseVec.copy().multiply(rOut);
			v3.y = noise.getValueAt((noise.rangeX - 1) / 2D - v1.x * maxRadiusRate, (noise.rangeY - 1) / 2D - v1.z * maxRadiusRate);

			Vector normal = v2.subtract(v1).crossProduct(v3.subtract(v1));
			GL11.glColor4d(redOut, greenOut, blueOut, 0.95);
			GL11.glNormal3d(normal.x, normal.y, normal.z);
			GL11.glVertex3d(v1.x, v1.y, v1.z);

			GL11.glColor4d(redIn, greenIn, blueIn, 0.95);
			GL11.glNormal3d(normal.x, normal.y, normal.z);
			GL11.glVertex3d(v2.x, v2.y, v2.z);
		    }
		    GL11.glEnd();
		    GL11.glPopMatrix();
		}
	    }
	    else break;
	}
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
