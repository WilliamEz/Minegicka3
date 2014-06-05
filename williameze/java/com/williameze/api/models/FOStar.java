package com.williameze.api.models;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

public class FOStar extends FlatOpposing
{
    public double innerRadius, outerRadius;
    public int cuts;
    public boolean edgeOnly;
    public double initialRotation;
    public Color outerColor;
    public Color centerColor;

    public FOStar(double x, double y, double z, double r1, double r2, int tip, boolean b)
    {
	orX = x;
	orY = y;
	orZ = z;
	innerRadius = Math.min(r1, r2);
	outerRadius = Math.max(r1, r2);
	cuts = tip * 2;
	edgeOnly = b;
    }

    public FOStar(double x, double y, double z, double r1, double r2, int tip)
    {
	this(x, y, z, r1, r2, tip, false);
    }

    public FOStar setOuterColor(Color c)
    {
	outerColor = c;
	return this;
    }

    public FOStar setOuterColor(int c, int op)
    {
	outerColor = new Color(c);
	int red = outerColor.getRed();
	int green = outerColor.getGreen();
	int blue = outerColor.getBlue();
	outerColor = new Color(red, green, blue, op);
	return this;
    }

    public FOStar setCenterColor(Color c)
    {
	centerColor = c;
	return this;
    }

    public FOStar setCenterColor(int c, int op)
    {
	centerColor = new Color(c);
	int red = centerColor.getRed();
	int green = centerColor.getGreen();
	int blue = centerColor.getBlue();
	centerColor = new Color(red, green, blue, op);
	return this;
    }

    @Override
    public void flatRender()
    {
	if (outerColor == null) outerColor = color;
	if (centerColor == null) centerColor = color;
	GL11.glShadeModel(GL11.GL_SMOOTH);
	Vector pointing = new Vector(0, 1, 0);
	pointing.rotateAroundZ(initialRotation);
	if (edgeOnly)
	{
	    GL11.glBegin(GL11.GL_LINE_LOOP);
	    for (int a = 0; a < cuts; a++)
	    {
		if (a % 2 == 0)
		{
		    GL11.glColor4d(color.getRed() / 255D, color.getGreen() / 255D, color.getBlue() / 255D, color.getAlpha() / 255D);
		    GL11.glVertex3d(pointing.x * innerRadius, pointing.y * innerRadius, 0);
		}
		else
		{
		    GL11.glColor4d(outerColor.getRed() / 255D, outerColor.getGreen() / 255D, outerColor.getBlue() / 255D,
			    outerColor.getAlpha() / 255D);
		    GL11.glVertex3d(pointing.x * outerRadius, pointing.y * outerRadius, 0);
		}
		pointing.rotateAroundZ(Math.PI * 2 / cuts);
	    }
	    GL11.glEnd();
	}
	else
	{
	    GL11.glBegin(GL11.GL_QUAD_STRIP);
	    for (int a = 0; a < cuts+1; a++)
	    {
		if (a % 2 == 0)
		{
		    GL11.glColor4d(color.getRed() / 255D, color.getGreen() / 255D, color.getBlue() / 255D, color.getAlpha() / 255D);
		    GL11.glVertex3d(pointing.x * innerRadius, pointing.y * innerRadius, 0);
		    GL11.glColor4d(centerColor.getRed() / 255D, centerColor.getGreen() / 255D, centerColor.getBlue() / 255D,
			    centerColor.getAlpha() / 255D);
		    GL11.glVertex3d(0, 0, 0);
		}
		else
		{
		    GL11.glColor4d(outerColor.getRed() / 255D, outerColor.getGreen() / 255D, outerColor.getBlue() / 255D,
			    outerColor.getAlpha() / 255D);
		    GL11.glVertex3d(pointing.x * outerRadius, pointing.y * outerRadius, 0);
		    GL11.glColor4d(centerColor.getRed() / 255D, centerColor.getGreen() / 255D, centerColor.getBlue() / 255D,
			    centerColor.getAlpha() / 255D);
		    GL11.glVertex3d(0, 0, 0);
		}
		pointing.rotateAroundZ(Math.PI * 2 / cuts);
	    }
	    GL11.glEnd();
	}
    }

}
