package com.williameze.minegicka3.bridges;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.minegicka3.core.Element;

public class DrawHelper
{
    public static void drawRect(double x1, double y1, double x2, double y2, double r, double g, double b, double a)
    {
	double bridge;

        if (x1 < x2)
        {
            bridge = x1;
            x1 = x2;
            x2 = bridge;
        }

        if (y1 < y2)
        {
            bridge = y1;
            y1 = y2;
            y2 = bridge;
        }

        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4d(r, g, b, a);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double)x1, (double)y2, 0.0D);
        tessellator.addVertex((double)x2, (double)y2, 0.0D);
        tessellator.addVertex((double)x2, (double)y1, 0.0D);
        tessellator.addVertex((double)x1, (double)y1, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
    
    public static void tessAddQuad(Tessellator tess, double x1, double y1, double x2, double y2, double u1, double v1, double u2, double v2)
    {
	if (x1 > x2)
	{
	    double x3 = x2;
	    x2 = x1;
	    x1 = x3;
	}
	if (y1 > y2)
	{
	    double y3 = y2;
	    y2 = y1;
	    y1 = y3;
	}
	if (u1 > u2)
	{
	    double u3 = u2;
	    u2 = u1;
	    u1 = u3;
	}
	if (v1 > v2)
	{
	    double v3 = v2;
	    v2 = v1;
	    v1 = v3;
	}
	tess.addVertexWithUV(x1, y2, 0, u1, v2);
	tess.addVertexWithUV(x2, y2, 0, u2, v2);
	tess.addVertexWithUV(x2, y1, 0, u2, v1);
	tess.addVertexWithUV(x1, y1, 0, u1, v1);
    }

    public static void drawElementIcon(TextureManager tex, Element el, boolean disabled, double x, double y, double w, double h, double actualSizeOccupyRatio)
    {
	if (tex != null) tex.bindTexture(Values.elementsTexture);
	int index = el.getTextureIndex(disabled);
	double tu1 = (index % 4) / 4D;
	double tu2 = tu1 + 0.25D;
	double tv1 = (index - index % 4) / 4D / 5D;
	double tv2 = tv1 + 0.2D;

	double actualW = w * actualSizeOccupyRatio;
	double actualH = h * actualSizeOccupyRatio;

	Tessellator tess = Tessellator.instance;
	tess.startDrawingQuads();
	tessAddQuad(tess, x + w / 2 - actualW / 2, y + h / 2 - actualH / 2, x + w / 2 + actualW / 2, y + h / 2 + actualH / 2, tu1, tv1, tu2, tv2);
	tess.draw();
    }

    public static void blurTexture(boolean b)
    {
	boolean a = false;
	if (b)
	{
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, a ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	}
	else
	{
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, a ? GL11.GL_NEAREST_MIPMAP_LINEAR : GL11.GL_NEAREST);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	}
    }
}