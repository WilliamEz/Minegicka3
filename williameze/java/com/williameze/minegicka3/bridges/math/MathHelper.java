package com.williameze.minegicka3.bridges.math;


public class MathHelper
{
    /**
     * Calculate the angles needed to rotate the vector (0,1,0) around x-axis and z-axis to be parallel with the passed vector
     * @param vec the passed vector
     * @return 2 angles to rotate vector (0,1,0) around x-axis and z-axis respectively (in radians)
     */
    public static double[] getRotatingAngles(Vector vec)
    {
	double rotateAroundX = 0;
	double rotateAroundZ = 0;
	{
	    double x = vec.y;
	    double y = vec.z;
	    double hyp = Math.sqrt(x*x+y*y);
	    double aCos = Math.acos(x/hyp);
	    rotateAroundX = Math.signum(y)*aCos + (x==-hyp ? Math.PI : 0);
	}
	{
	    double x = vec.x;
	    double y = vec.y;
	    double hyp = Math.sqrt(x*x+y*y);
	    double aCos = Math.acos(x/hyp);
	    rotateAroundZ = Math.signum(y)*aCos + (x==-hyp ? Math.PI : 0);
	}
	return new double[]{rotateAroundX, rotateAroundZ};
    }
}
