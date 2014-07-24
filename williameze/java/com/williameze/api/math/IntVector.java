package com.williameze.api.math;

import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;

public class IntVector
{
    public static IntVector root = new IntVector(0, 0, 0);
    public static IntVector unitX = new IntVector(1, 0, 0);
    public static IntVector unitY = new IntVector(0, 1, 0);
    public static IntVector unitZ = new IntVector(0, 0, 1);
    /**
     * X coordinate of VectorD
     */
    public int x;
    /**
     * Y coordinate of VectorD
     */
    public int y;
    /**
     * Z coordinate of VectorD
     */
    public int z;

    public static IntVector median(IntVector... vs)
    {
	double x = 0;
	double y = 0;
	double z = 0;
	for (int a = 0; a < vs.length; a++)
	{
	    x += vs[a].x;
	    y += vs[a].y;
	    z += vs[a].z;
	}
	x /= vs.length;
	y /= vs.length;
	z /= vs.length;
	return new IntVector(x, y, z);
    }

    public IntVector(double i, double j, double k)
    {
	this((int) i, (int) j, (int) k);
    }

    public IntVector(int i, int j, int k)
    {
	x = i;
	y = j;
	z = k;
    }

    /**
     * Sets the x,y,z components of the vector as specified.
     */
    public IntVector setComponents(int par1, int par3, int par5)
    {
	x = par1;
	y = par3;
	z = par5;
	return this;
    }

    public IntVector multiply(double d)
    {
	return new IntVector(x * d, y * d, z * d);
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public IntVector normalize()
    {
	double d0 = Math.sqrt(x * x + y * y + z * z);
	return isZeroVector() ? new IntVector(0, 0, 0) : new IntVector(x / d0, y / d0, z / d0);
    }

    /**
     * Get angle between vector, frmo 0 -> pi
     * 
     * @param v
     * @return
     */
    public double getAngleBetween(IntVector v)
    {
	return Math.atan2(crossProduct(v).lengthVector(), dotProduct(v));
    }

    public double dotProduct(IntVector v)
    {
	return x * v.x + y * v.y + z * v.z;
    }

    /**
     * Returns a new vector with the result of this vector x the specified vector.
     */
    public IntVector crossProduct(IntVector v)
    {
	return new IntVector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public IntVector add(double i, double j, double k)
    {
	return new IntVector(x + i, y + j, z + k);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this vector.
     */
    public IntVector add(IntVector v)
    {
	return new IntVector(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Returns a new vector with the result of this vector minus the specified vector
     */
    public IntVector subtract(IntVector v)
    {
	if (v == null) v = new IntVector(0, 0, 0);
	return new IntVector(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Returns the length of the vector.
     */
    public double lengthVector()
    {
	return Math.sqrt(x * x + y * y + z * z);
    }

    public String toString()
    {
	return "(" + x + ", " + y + ", " + z + ")";
    }

    public IntVector rotateTowards(IntVector dest)
    {
	IntVector c = crossProduct(dest);
	double l = c.lengthVector();
	if (l > 0)
	{
	    IntVector axis = c.normalize();
	    return rotateAround(axis, Math.atan2(l, dotProduct(dest)));
	}
	else if (dotProduct(dest) < 0)
	{
	    return new IntVector(-c.x, -c.y, -c.z);
	}
	else
	{
	    return c;
	}
    }

    public IntVector rotateAround(IntVector axis, double radian)
    {
	IntVector ax = axis.normalize();
	double cos = Math.cos(radian);
	double sin = Math.sin(radian);
	IntVector seg1 = new IntVector(x * cos, y * cos, z * cos);
	IntVector seg2 = crossProduct(ax).multiply(sin);
	IntVector seg3 = ax.multiply(dotProduct(ax) * (1 - cos));
	return new IntVector(seg1.x + seg2.x + seg3.x, seg1.y + seg2.y + seg3.y, seg1.z + seg2.z + seg3.z);
    }

    /**
     * Rotates the vector around the x axis by the specified angle.
     */
    public void rotateAroundX(double par1)
    {
	double f1 = Math.cos(par1);
	double f2 = Math.sin(par1);
	double d0 = x;
	double d1 = y * f1 + z * f2;
	double d2 = z * f1 - y * f2;
	x = (int) d0;
	y = (int) d1;
	z = (int) d2;
    }

    /**
     * Rotates the vector around the y axis by the specified angle.
     */
    public void rotateAroundY(double par1)
    {
	double f1 = Math.cos(par1);
	double f2 = Math.sin(par1);
	double d0 = x * f1 + z * f2;
	double d1 = y;
	double d2 = z * f1 - x * f2;
	x = (int) d0;
	y = (int) d1;
	z = (int) d2;
    }

    /**
     * Rotates the vector around the z axis by the specified angle.
     */
    public void rotateAroundZ(double par1)
    {
	double f1 = Math.cos(par1);
	double f2 = Math.sin(par1);
	double d0 = x * f1 + y * f2;
	double d1 = y * f1 - x * f2;
	double d2 = z;
	x = (int) d0;
	y = (int) d1;
	z = (int) d2;
    }

    public boolean isZeroVector()
    {
	return x == 0 && y == 0 && z == 0;
    }

    public boolean parallel(IntVector v)
    {
	int clamp = 1000000000;
	return Math.round(crossProduct(v).lengthVector() * clamp) == 0;
    }

    @Override
    public boolean equals(Object obj)
    {
	return obj instanceof IntVector && ((IntVector) obj).x == x && ((IntVector) obj).y == y && ((IntVector) obj).z == z;
    }

    public IntVector copy()
    {
	return new IntVector(x, y, z);
    }

    public Vec3 vec3()
    {
	return Vec3.createVectorHelper(x, y, z);
    }
}