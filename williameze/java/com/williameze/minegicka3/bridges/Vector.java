package com.williameze.minegicka3.bridges;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Vector
{
    /**
     * X coordinate of VectorD
     */
    public double x;
    /**
     * Y coordinate of VectorD
     */
    public double y;
    /**
     * Z coordinate of VectorD
     */
    public double z;

    public Vector(double i, double j, double k)
    {
	x = i;
	y = j;
	z = k;
    }

    /**
     * Sets the x,y,z components of the vector as specified.
     */
    protected Vector setComponents(double par1, double par3, double par5)
    {
	x = par1;
	y = par3;
	z = par5;
	return this;
    }

    /**
     * Returns a new vector with the result of this vector minus the specified vector
     */
    public Vector subtractVector(Vector par1Vector)
    {
	return new Vector(x - par1Vector.x, y - par1Vector.y, z - par1Vector.z);
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public Vector normalize()
    {
	double d0 = Math.sqrt(x * x + y * y + z * z);
	return d0 < 1.0E-4D ? new Vector(0.0D, 0.0D, 0.0D) : new Vector(x / d0, y / d0, z / d0);
    }

    public double dotProduct(Vector par1Vector)
    {
	return x * par1Vector.x + y * par1Vector.y + z * par1Vector.z;
    }

    /**
     * Returns a new vector with the result of this vector x the specified
     * vector.
     */
    public Vector crossProduct(Vector par1Vector)
    {
	return new Vector(y * par1Vector.z - z * par1Vector.y, z * par1Vector.x - x * par1Vector.z, x * par1Vector.y
		- y * par1Vector.x);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the
     * resulting vector. Does not change this vector.
     */
    public Vector addVector(double par1, double par3, double par5)
    {
	return new Vector(x + par1, y + par3, z + par5);
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
	x = d0;
	y = d1;
	z = d2;
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
	x = d0;
	y = d1;
	z = d2;
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
	x = d0;
	y = d1;
	z = d2;
    }

    public boolean isZeroVector()
    {
	return x==0 && y==0 && z==0;
    }
    
    public boolean parallel(Vector v)
    {
	int clamp = 100000000;
	return Math.round(crossProduct(v).lengthVector()*clamp)==0; 
    }
}