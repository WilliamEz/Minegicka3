package com.williameze.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

public class Box extends ModelObject
{
    public Vector med;
    public List<Quad> faces = new ArrayList();

    public Box(Vector mid, double halfSize)
    {
	this(mid, halfSize, halfSize, halfSize);
    }

    public Box(Vector mid, double xhalf, double yhalf, double zhalf)
    {
	this(mid.x - xhalf, mid.y - yhalf, mid.z - zhalf, mid.x + xhalf, mid.y + yhalf, mid.z + zhalf);
    }

    public Box(double minx, double miny, double minz, double maxx, double maxy, double maxz)
    {
	this(new Vector(minx, maxy, minz), new Vector(minx, maxy, maxz), new Vector(maxx, maxy, maxz), new Vector(maxx, maxy, minz), new Vector(minx, miny, minz),
		new Vector(minx, miny, maxz), new Vector(maxx, miny, maxz), new Vector(maxx, miny, minz));
    }

    public Box(Vector v01, Vector v02, Vector v03, Vector v04, Vector v11, Vector v12, Vector v13, Vector v14)
    {
	med = Vector.median(v01, v02, v03, v04, v11, v12, v13, v14);
	Quad top = new Quad(v01, v02, v03, v04, v01.subtract(med), false).orderVertexesCounterClockwise();
	Quad bottom = new Quad(v11, v12, v13, v14, v11.subtract(med), false).orderVertexesCounterClockwise();
	Quad left = new Quad(top.v1, top.v2, bottom.v2, bottom.v1, top.v1.subtract(med), false).orderVertexesCounterClockwise();
	Quad front = new Quad(top.v3, top.v2, bottom.v2, bottom.v3, top.v3.subtract(med), false).orderVertexesCounterClockwise();
	Quad right = new Quad(top.v3, top.v4, bottom.v4, bottom.v3, top.v3.subtract(med), false).orderVertexesCounterClockwise();
	Quad back = new Quad(top.v1, top.v4, bottom.v4, bottom.v1, top.v1.subtract(med), false).orderVertexesCounterClockwise();
	faces.clear();
	faces.addAll(Arrays.asList(top, bottom, left, front, right, back));
    }

    public void translate(Vector v)
    {
	for (Quad q : faces)
	{
	    q.translate(v);
	}
    }

    public void rotateAroundMid(Vector axis, double rad)
    {
	rotateAroundPivot(med, axis, rad);
    }

    public void rotateAroundPivot(Vector pivot, Vector axis, double rad)
    {
	for (Quad q : faces)
	{
	    q.rotate(pivot, axis, rad);
	}
    }

    @Override
    public void render()
    {
	GL11.glPushMatrix();
	GL11.glBegin(GL11.GL_QUADS);
	glSetColor();
	for (Quad f : faces)
	{
	    f.addQuadToGL();
	}
	GL11.glEnd();
	glResetColor();
	GL11.glPopMatrix();
    }

}
