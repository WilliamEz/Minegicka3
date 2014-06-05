package com.williameze.api.models;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

public class FOCircle extends FlatOpposing
{
    public double radius;
    public int cuts;
    public boolean edgeOnly;
    public double initialRotation;

    public FOCircle(double x, double y, double z, double r, int c, boolean b)
    {
	orX = x;
	orY = y;
	orZ = z;
	radius = r;
	cuts = c;
	edgeOnly = b;
    }

    public FOCircle(double x, double y, double z, double r, int c)
    {
	this(x, y, z, r, c, false);
    }

    @Override
    public void flatRender()
    {
	Vector pointing = new Vector(0, radius, 0);
	pointing.rotateAroundZ(initialRotation);
	if (edgeOnly) GL11.glBegin(GL11.GL_LINE_LOOP);
	else GL11.glBegin(GL11.GL_POLYGON);
	glSetColor();
	for (int a = 0; a < cuts; a++)
	{
	    GL11.glVertex3d(pointing.x, pointing.y, 0);
	    pointing.rotateAroundZ(Math.PI * 2 / cuts);
	}
	GL11.glEnd();
    }

}
