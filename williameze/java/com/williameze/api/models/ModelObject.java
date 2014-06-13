package com.williameze.api.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ModelObject
{
    public static Tessellator tess = Tessellator.instance;
    public List<Quad> childQuads = new ArrayList();
    public Color color = Color.white;
    public Vector pivot = Vector.root;

    public ModelObject setPivot(Vector v)
    {
	if (v != null) pivot = v;
	return this;
    }

    public ModelObject setColor(Color c)
    {
	color = c;
	return this;
    }

    public ModelObject setColor(int c, int op)
    {
	color = new Color(c);
	int red = color.getRed();
	int green = color.getGreen();
	int blue = color.getBlue();
	color = new Color(red, green, blue, op);
	return this;
    }

    public void setRotation(Vector v)
    {

    }

    public void setRotation(double yaw, double pitch)
    {
	if (pivot != null) GL11.glTranslated(pivot.x, pivot.y, pivot.z);
	GL11.glRotated(yaw, 0, 1, 0);
	GL11.glRotated(pitch, 1, 0, 0);
	if (pivot != null) GL11.glTranslated(-pivot.x, -pivot.y, -pivot.z);
    }

    public void glResetColor()
    {
	// GL11.glColor4d(1, 1, 1, 1);
	tess.setColorOpaque(1, 1, 1);
    }

    public void glSetColor()
    {
	// GL11.glColor4ub((byte) color.getRed(), (byte) color.getGreen(),
	// (byte) color.getBlue(), (byte) color.getAlpha());
	tess.setColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void glSetColor(Color c)
    {
	//GL11.glColor4ub((byte) c.getRed(), (byte) c.getGreen(), (byte) c.getBlue(), (byte) c.getAlpha());
	tess.setColorRGBA(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    public abstract void render();

    public void begin(int i)
    {
	// GL11.glBegin(i);
	tess.startDrawing(i);
    }

    public void setNormal(double x, double y, double z)
    {
	// GL11.glNormal3d(x, y, z);
	tess.setNormal((float) x, (float) y, (float) z);
    }

    public void setNormal(Vector v)
    {
	setNormal(v.x, v.y, v.z);
    }

    public void addVertex(double x, double y, double z)
    {
	// GL11.glVertex3d(x, y, z);
	tess.addVertex(x, y, z);
    }

    public void addVertex(Vector v)
    {
	addVertex(v.x, v.y, v.z);
    }

    public void end()
    {
	// GL11.glEnd();
	tess.draw();
    }
}
