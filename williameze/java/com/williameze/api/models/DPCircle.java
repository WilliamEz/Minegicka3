package com.williameze.api.models;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

public class DPCircle extends DirectionalPanel
{
    public double radius;
    public int cuts;
    public double initialRotation;

    public DPCircle(double x, double y, double z, double r, int c)
    {
	orX = x;
	orY = y;
	orZ = z;
	radius = r;
	cuts = c;
    }

    @Override
    public void dpAddVertexes()
    {
	Vector pointing = new Vector(0, radius, 0);
	pointing.rotateAroundZ(initialRotation);
	for (int a = 0; a < cuts; a++)
	{
	    VertexData vd = new VertexData(pointing.copy(), opposeDirection.copy(), color);
	    dpAddVertex(vd);
	    pointing.rotateAroundZ(Math.PI * 2 / cuts);
	}
    }

    @Override
    public int dpThicknessVertexStep()
    {
	return 1;
    }

    @Override
    public int dpPrimitive()
    {
	return GL11.GL_POLYGON;
    }
}
