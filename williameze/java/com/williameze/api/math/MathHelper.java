package com.williameze.api.math;

public class MathHelper
{
    /***
     * Get cosin of angle between 2 vectors, -1 to 1 inclusive
     */
    public static double getCosAngleBetweenVector(Vector v1, Vector v2)
    {
	return v1.dotProduct(v2) / v1.lengthVector() / v2.lengthVector();
    }

    /***
     * Get angle between 2 vectors, 0 to Pi inclusive
     */
    public static double getRadAngleBetweenVector(Vector v1, Vector v2)
    {
	return Math.acos(getCosAngleBetweenVector(v1, v2));
    }
}
