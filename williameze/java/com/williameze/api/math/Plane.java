package com.williameze.api.math;

public class Plane
{
    /** Tangent vector **/
    public Vector tang;
    public double free;

    public Plane(Vector point1, Vector point2, Vector point3)
    {
	this(point3.subtract(point1).crossProduct(point3.subtract(point2)), point1);
    }

    public Plane(Vector t, double f)
    {
	tang = t;
	free = f;
    }

    public Plane(Vector t, Vector point)
    {
	tang = t;
	free = -(tang.x * point.x + tang.y * point.y + tang.z * point.z);
    }

    public Vector getAssurancePoint()
    {
	double e = Math.E;
	if (tang.x == 0 && tang.y == 0 && tang.z == 0) return new Vector(0, 0, 0);
	if (tang.y == 0 && tang.z == 0) return new Vector(free / tang.x, e, e);
	if (tang.x == 0 && tang.z == 0) return new Vector(e, free / tang.y, e);
	if (tang.x == 0 && tang.y == 0) return new Vector(e, e, free / tang.z);
	if (tang.y == 0) return getPointLackX(e, e);
	if (tang.x == 0) return getPointLackY(e, e);
	if (tang.z == 0) return getPointLackY(e, e);
	return getPointLackY(e, e);
    }

    public Vector getPointLackX(double y, double z)
    {
	return new Vector((free - tang.y * y - tang.z * z) / tang.x, y, z);
    }

    public Vector getPointLackY(double x, double z)
    {
	return new Vector(x, (free - tang.x * x - tang.z * z) / tang.y, z);
    }

    public Vector getPointLackZ(double x, double y)
    {
	return new Vector(x, y, (free - tang.x * x - tang.y * y) / tang.z);
    }

    public Vector intersectWith(Line l)
    {
	return l.intersectWith(this);
    }

    public Line intersectWith(Plane p)
    {
	if (!tang.crossProduct(p.tang).isZeroVector())
	{
	    double z = 0;
	    double x = (free / tang.y - p.free / p.tang.y) / (tang.x / tang.y - p.tang.x / p.tang.y);
	    double y = (free - tang.x * x) / tang.y;

	    return new Line(new Vector(x, y, z).normalize(), tang.crossProduct(p.tang).normalize());
	}
	return null;
    }

    public boolean parallel(Plane p)
    {
	return tang.parallel(p.tang);
    }

    @Override
    public boolean equals(Object obj)
    {
	if (obj instanceof Plane)
	{
	    Plane p = (Plane) obj;
	    return tang.parallel(p.tang) && free == p.free;
	}
	return false;
    }

    @Override
    public String toString()
    {
	return (tang.x != 0 ? (tang.x > 0 ? "+" : "") + tang.x + "*x" : "") + (tang.y != 0 ? (tang.y > 0 ? "+" : "") + tang.y + "*y" : "")
		+ (tang.z != 0 ? (tang.z > 0 ? "+" : "") + tang.z + "*z" : "") + (free != 0 ? " + " + free : "") + " = 0";
    }
}
