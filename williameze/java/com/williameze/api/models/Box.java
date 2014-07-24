package com.williameze.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.math.Vector;

public class Box extends ModelObject
{
    public Vector med;
    public List<Quad> faces = new ArrayList();
    public Quad top;
    public Quad bottom;
    public Quad left;
    public Quad front;
    public Quad right;
    public Quad back;

    public static Box create(Vector mid, double halfSize)
    {
	return create(mid, halfSize, halfSize, halfSize);
    }

    public static Box create(Vector mid, double xhalf, double yhalf, double zhalf)
    {
	return create(mid.x - xhalf, mid.y - yhalf, mid.z - zhalf, mid.x + xhalf, mid.y + yhalf, mid.z + zhalf, 0);
    }

    public static Box create(Vector mid, double xhalf, double yhalf, double zhalf, double yRotation)
    {
	return create(mid.x - xhalf, mid.y - yhalf, mid.z - zhalf, mid.x + xhalf, mid.y + yhalf, mid.z + zhalf, yRotation);
    }

    public static Box create(double minx, double miny, double minz, double maxx, double maxy, double maxz)
    {
	return create(minx, miny, minz, maxx, maxy, maxz, 0);
    }

    public static Box create(double minx, double miny, double minz, double maxx, double maxy, double maxz, double yRotation)
    {
	if (minx > maxx)
	{
	    double x1 = minx;
	    minx = maxx;
	    maxx = x1;
	}
	if (miny > maxy)
	{
	    double y1 = miny;
	    miny = maxy;
	    maxy = y1;
	}
	if (minz > maxz)
	{
	    double z1 = minz;
	    minz = maxz;
	    maxz = z1;
	}
	Vector v01 = new Vector(minx, maxy, minz);
	Vector v02 = new Vector(minx, maxy, maxz);
	Vector v03 = new Vector(maxx, maxy, maxz);
	Vector v04 = new Vector(maxx, maxy, minz);
	Vector v11 = new Vector(minx, miny, minz);
	Vector v12 = new Vector(minx, miny, maxz);
	Vector v13 = new Vector(maxx, miny, maxz);
	Vector v14 = new Vector(maxx, miny, minz);
	if (yRotation != 0)
	{
	    Vector med = Vector.median(v01, v02, v03, v04, v11, v12, v13, v14);
	    v01 = med.add(v01.subtract(med).rotateAround(Vector.unitY, yRotation));
	    v02 = med.add(v02.subtract(med).rotateAround(Vector.unitY, yRotation));
	    v03 = med.add(v03.subtract(med).rotateAround(Vector.unitY, yRotation));
	    v04 = med.add(v04.subtract(med).rotateAround(Vector.unitY, yRotation));
	    v11 = med.add(v11.subtract(med).rotateAround(Vector.unitY, yRotation));
	    v12 = med.add(v12.subtract(med).rotateAround(Vector.unitY, yRotation));
	    v13 = med.add(v13.subtract(med).rotateAround(Vector.unitY, yRotation));
	    v14 = med.add(v14.subtract(med).rotateAround(Vector.unitY, yRotation));
	}
	return new Box(v01, v02, v03, v04, v11, v12, v13, v14);
    }

    public Box(Vector v01, Vector v02, Vector v03, Vector v04, Vector v11, Vector v12, Vector v13, Vector v14)
    {
	med = Vector.median(v01, v02, v03, v04, v11, v12, v13, v14);

	top = new Quad(v01, v02, v03, v04, v01.subtract(med), false);
	bottom = new Quad(v14, v13, v12, v11, v11.subtract(med), false);
	left = new Quad(v02, v01, v11, v12, v02.subtract(med), false);
	front = new Quad(v03, v02, v12, v13, v03.subtract(med), false);
	right = new Quad(v04, v03, v13, v14, v04.subtract(med), false);
	back = new Quad(v01, v04, v14, v11, v01.subtract(med), false);

	faces.clear();
	faces.add(top);
	faces.add(bottom);
	faces.add(left);
	faces.add(front);
	faces.add(right);
	faces.add(back);
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
    public ModelObject setTextureQuad(double minX, double minY, double maxX, double maxY, double... additional)
    {
	double quadW = maxX - minX;
	double quadH = maxY - minY;
	double topSide = 0;
	if (additional.length > 0)
	{
	    topSide = additional[0];
	}
	else
	{
	    double d1 = left.v2.subtract(left.v3).lengthVector();
	    double d2 = left.v2.subtract(left.v1).lengthVector();
	    topSide = d2 / (d2 + d1) * quadH;
	}
	double leftShort = topSide;
	if (texture != null)
	{
	    int[] dimensions = DrawHelper.getTextureDimensions(texture);
	    leftShort *= (double) dimensions[1] / (double) dimensions[0];
	}
	double sideLong = quadH - topSide;
	double rightShort = quadW / 2 - leftShort;
	double x1 = minX;
	double x2 = minX + leftShort;
	double x3 = x2 + rightShort;
	double x4 = x3 + leftShort;
	double x5 = maxX;
	double y1 = minY;
	double y2 = minY + topSide;
	double y3 = maxY;

	top.tv1 = new Vector(x2, y1);
	top.tv2 = new Vector(x2, y2);
	top.tv3 = new Vector(x3, y2);
	top.tv4 = new Vector(x3, y1);
	bottom.tv1 = new Vector(x5, y2);
	bottom.tv2 = new Vector(x5, y1);
	bottom.tv3 = new Vector(x4, y1);
	bottom.tv4 = new Vector(x4, y2);
	left.tv1 = new Vector(x2, y2);
	left.tv2 = new Vector(x1, y2);
	left.tv3 = new Vector(x1, y3);
	left.tv4 = new Vector(x2, y3);
	front.tv1 = new Vector(x3, y2);
	front.tv2 = new Vector(x2, y2);
	front.tv3 = new Vector(x2, y3);
	front.tv4 = new Vector(x3, y3);
	right.tv1 = new Vector(x4, y2);
	right.tv2 = new Vector(x3, y2);
	right.tv3 = new Vector(x3, y3);
	right.tv4 = new Vector(x4, y3);
	back.tv1 = new Vector(x5, y2);
	back.tv2 = new Vector(x4, y2);
	back.tv3 = new Vector(x4, y3);
	back.tv4 = new Vector(x5, y3);

	return this;
    }

    @Override
    public void doRender()
    {
	begin(GL11.GL_QUADS);
	glSetColor();
	for (Quad f : faces)
	{
	    f.addQuadToGL();
	}
	end();
	glResetColor();
    }

}
