package com.williameze.api.models;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

public class DPStar extends DirectionalPanel
{
    public double innerRadius, outerRadius;
    public int cuts;
    public double initialRotation;
    public Color outerColor;
    public Color centerColor;

    public DPStar(double x, double y, double z, double r1, double r2, int tip)
    {
	orX = x;
	orY = y;
	orZ = z;
	innerRadius = Math.min(r1, r2);
	outerRadius = Math.max(r1, r2);
	cuts = tip * 2;
    }

    public DPStar setOuterColor(Color c)
    {
	outerColor = c;
	return this;
    }

    public DPStar setOuterColor(int c, int op)
    {
	outerColor = new Color(c);
	int red = outerColor.getRed();
	int green = outerColor.getGreen();
	int blue = outerColor.getBlue();
	outerColor = new Color(red, green, blue, op);
	return this;
    }

    public DPStar setCenterColor(Color c)
    {
	centerColor = c;
	return this;
    }

    public DPStar setCenterColor(int c, int op)
    {
	centerColor = new Color(c);
	int red = centerColor.getRed();
	int green = centerColor.getGreen();
	int blue = centerColor.getBlue();
	centerColor = new Color(red, green, blue, op);
	return this;
    }

    @Override
    public void dpAddVertexes()
    {
	if (outerColor == null) outerColor = color;
	if (centerColor == null) centerColor = color;
	Vector pointing = new Vector(0, 1, 0);
	pointing.rotateAroundZ(initialRotation);

	for (int a = 0; a < cuts + 1; a++)
	{
	    if (a % 2 == 0)
	    {
		dpAddVertex(new VertexData(pointing.multiply(innerRadius), opposeDirection, color));
		dpAddVertex(new VertexData(Vector.root.copy(), opposeDirection, centerColor));
	    }
	    else
	    {
		dpAddVertex(new VertexData(pointing.multiply(outerRadius), opposeDirection, outerColor));
		dpAddVertex(new VertexData(Vector.root.copy(), opposeDirection, centerColor));
	    }
	    pointing.rotateAroundZ(Math.PI * 2 / cuts);
	}
    }

    @Override
    public int dpPrimitive()
    {
	return GL11.GL_QUAD_STRIP;
    }

    @Override
    public int dpThicknessVertexStep()
    {
	return 2;
    }
}
