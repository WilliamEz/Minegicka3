package com.williameze.api.math;

public class Line
{
    /** A point of Origin **/
    public Vector org;
    /** Uniform vector **/
    public Vector dir;

    public Line(Vector origin, Vector direction)
    {
	if (origin == null) origin = new Vector(0, 0, 0);
	org = origin;
	dir = direction;
    }

    public Vector getPointWithX(double x)
    {
	return new Vector(x, (x - org.x) / dir.x * dir.y + org.y, (x - org.x) / dir.x * dir.z + org.z);
    }

    public Vector getPointWithY(double y)
    {
	return new Vector((y - org.y) / dir.y * dir.x + org.x, y, (y - org.y) / dir.y * dir.z + org.z);
    }

    public Vector getPointWithZ(double z)
    {
	return new Vector((z - org.z) / dir.z * dir.x + org.x, (z - org.z) / dir.z * dir.y + org.y, z);
    }

    public Vector intersectWith(Line l)
    {
	Vector between = org.subtract(l.org);
	Vector cross = dir.crossProduct(l.dir);
	if (!cross.isZeroVector() && cross.dotProduct(between) == 0)
	{
	    double t = (org.x - l.org.x) / (l.dir.x - dir.x);
	    double x = org.x + dir.x * t;
	    double y = org.y + dir.y * t;
	    double z = org.z + dir.z * t;
	    return new Vector(x, y, z);
	}
	return null;
    }

    public Vector intersectWith(Plane p)
    {
	if (dir.dotProduct(p.tang) != 0)
	{
	    double t = (0 - p.free - p.tang.x * org.x - p.tang.y * org.y - p.tang.z * org.z)
		    / (dir.x * p.tang.x + dir.y * p.tang.y + dir.z * p.tang.z);
	    double x = org.x + dir.x * t;
	    double y = org.y + dir.y * t;
	    double z = org.z + dir.z * t;
	    return new Vector(x, y, z);
	}
	else if (p.getPointLackX(org.y, org.z).x == org.x) return org;
	return null;
    }

    public boolean parallel(Line l)
    {
	return dir.parallel(l.dir);
    }

    @Override
    public boolean equals(Object obj)
    {
	if (obj instanceof Line)
	{
	    return dir.parallel(((Line) obj).dir) && dir.parallel(org.subtract(((Line) obj).org));
	}
	return false;
    }

    @Override
    public String toString()
    {
	String sx = "x = " + org.x + (dir.x != 0 ? (dir.x > 0 ? " + " : "") + dir.x + "*t" : "");
	String sy = "y = " + org.y + (dir.y != 0 ? (dir.y > 0 ? " + " : "") + dir.y + "*t" : "");
	String sz = "z = " + org.z + (dir.z != 0 ? (dir.z > 0 ? " + " : "") + dir.z + "*t" : "");
	return "[" + sx + "] ; [" + sy + "] ; [" + sz + "]";
    }
}
