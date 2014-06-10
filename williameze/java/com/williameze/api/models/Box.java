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

    public static Box create(Vector mid, double halfSize)
    {
	return create(mid, halfSize, halfSize, halfSize);
    }

    public static Box create(Vector mid, double xhalf, double yhalf, double zhalf)
    {
	return create(mid.x - xhalf, mid.y - yhalf, mid.z - zhalf, mid.x + xhalf, mid.y + yhalf, mid.z + zhalf);
    }

    public static Box create(double minx, double miny, double minz, double maxx, double maxy, double maxz)
    {
	Vector v01 = new Vector(minx, maxy, minz);
	Vector v02 = new Vector(minx, maxy, maxz);
	Vector v03 = new Vector(maxx, maxy, maxz);
	Vector v04 = new Vector(maxx, maxy, minz);
	Vector v11 = new Vector(minx, miny, minz);
	Vector v12 = new Vector(minx, miny, maxz);
	Vector v13 = new Vector(maxx, miny, maxz);
	Vector v14 = new Vector(maxx, miny, minz);
	return new Box(v01, v02, v03, v04, v11, v12, v13, v14);
    }

    public Box(Vector v01, Vector v02, Vector v03, Vector v04, Vector v11, Vector v12, Vector v13, Vector v14)
    {
	med = Vector.median(v01, v02, v03, v04, v11, v12, v13, v14);
	Quad top = new Quad(v01, v02, v03, v04, v01.subtract(med), false).orderVertexesCounterClockwise();
	Quad bottom = new Quad(v11, v12, v13, v14, v11.subtract(med), false).orderVertexesCounterClockwise();
	Quad left = new Quad(v01, v02, v12, v11, v01.subtract(med), false).orderVertexesCounterClockwise();
	Quad front = new Quad(v03, v02, v12, v13, v03.subtract(med), false).orderVertexesCounterClockwise();
	Quad right = new Quad(v03, v04, v14, v13, v03.subtract(med), false).orderVertexesCounterClockwise();
	Quad back = new Quad(v01, v04, v14, v11, v01.subtract(med), false).orderVertexesCounterClockwise();
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
	begin(GL11.GL_QUADS);
	glSetColor();
	for (Quad f : faces)
	{
	    f.addQuadToGL();
	}
	end();
	glResetColor();
	GL11.glPopMatrix();
    }

}
