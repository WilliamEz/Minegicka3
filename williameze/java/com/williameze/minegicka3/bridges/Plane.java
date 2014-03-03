package com.williameze.minegicka3.bridges;

public class Plane
{
    /** Tangent vector **/
    public Vector tang;
    public double free;

    public Plane(Vector t, double f)
    {
	tang = t;
	free = f;
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

	    return new Line(new Vector(x, y, z), tang.crossProduct(p.tang));
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
	if(obj instanceof Plane)
	{
	    Plane p = (Plane) obj;
	    return tang.parallel(p.tang) && free==p.free;
	}
        return false;
    }
}
