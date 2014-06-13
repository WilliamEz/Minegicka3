package com.williameze.api.models;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

public class DPRect extends DirectionalPanel
{
    public double xLength, yLength;

    public DPRect(double x, double y, double z, double xl, double yl)
    {
	orX = x;
	orY = y;
	orZ = z;
	xLength = xl;
	yLength = yl;
    }

    @Override
    public void dpAddVertexes()
    {
	dpAddVertex(new VertexData(new Vector(xLength / 2, yLength / 2, 0), opposeDirection, color));
	dpAddVertex(new VertexData(new Vector(-xLength / 2, yLength / 2, 0), opposeDirection, color));
	dpAddVertex(new VertexData(new Vector(-xLength / 2, -yLength / 2, 0), opposeDirection, color));
	dpAddVertex(new VertexData(new Vector(xLength / 2, -yLength / 2, 0), opposeDirection, color));
    }

    @Override
    public int dpThicknessVertexStep()
    {
	return 1;
    }

    @Override
    public int dpPrimitive()
    {
	return GL11.GL_QUADS;
    }
}
