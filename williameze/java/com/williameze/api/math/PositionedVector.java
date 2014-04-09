package com.williameze.api.math;

public class PositionedVector
{
    public Vector pos;
    public Vector dir;

    public PositionedVector(Vector v1, Vector v2)
    {
	pos = v1;
	dir = v2;
    }

    public PositionedVector(double x1, double y1, double z1, double x2, double y2, double z2)
    {
	pos = new Vector(x1, y1, z1);
	dir = new Vector(x2, y2, z2);
    }

    public PositionedVector(double x1, double y1, double z1, Vector v2)
    {
	pos = new Vector(x1, y1, z1);
	dir = v2;
    }

    public PositionedVector(Vector v1, double x2, double y2, double z2)
    {
	pos = v1;
	dir = new Vector(x2, y2, z2);
    }

}
