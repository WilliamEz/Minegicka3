package com.williameze.api.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.williameze.api.math.Vector;

public class CylinderConjunc extends ModelObject
{
    public List<Vector> midPoints = new ArrayList();
    public List<Cylinder> segments = new ArrayList();

    public static CylinderConjunc createTorus(Vector startPoint, Vector centerPivot, Vector axis, double initialRot, int segmentCount,
	    double cylRadius, int cylCuts)
    {
	return createSpiral(startPoint, centerPivot, axis, initialRot, Math.PI * 2D / segmentCount, segmentCount, 0, cylRadius, cylCuts);
    }

    public static CylinderConjunc createSpiral(Vector startPoint, Vector centerPivot, Vector axis, double initialRot, double rotAngle,
	    int segmentCount, double incrementAlongAxis, double cylRadius, int cylCuts)
    {
	List<Vector> l = new ArrayList();
	Vector axisNor = axis.normalize();
	Vector rotating = startPoint.subtract(centerPivot);
	rotating = rotating.add(axisNor.multiply(-incrementAlongAxis));
	rotating = rotating.rotateAround(axisNor, initialRot - rotAngle);
	l.add(centerPivot.add(rotating));

	for (int a = 1; a <= segmentCount + 2; a++)
	{
	    rotating = rotating.add(axisNor.multiply(incrementAlongAxis));
	    rotating = rotating.rotateAround(axisNor, rotAngle);
	    l.add(centerPivot.add(rotating));
	}

	return new CylinderConjunc(cylRadius, cylCuts, l);
    }

    /**
     * 
     * @param cylRadius
     * @param cylCuts
     * @param list
     *            list of midpoints of conjunction's inbetween faces, with the
     *            first vector being the guidance for the actual first
     *            cylinder's top face normal, and the last vector being the
     *            guidance for the last cylinder's bottom face
     */
    public CylinderConjunc(double cylRadius, int cylCuts, List<Vector> list)
    {
	if (list.size() > 2)
	{
	    midPoints.addAll(list.subList(1, list.size()));
	    for (int a = 1; a < list.size() - 2; a++)
	    {
		Vector pre1 = list.get(a - 1);
		Vector center1 = list.get(a);
		Vector center2 = list.get(a + 1);
		Vector post2 = list.get(a + 2);
		Vector nor1 = (center2.subtract(center1).crossProduct(pre1.subtract(center1))).crossProduct(pre1.subtract(center1).add(
			center2.subtract(center1)));
		Vector nor2 = (post2.subtract(center2).add(center1).subtract(center2)).crossProduct(post2.subtract(center2).crossProduct(
			center1.subtract(center2)));
		nor1 = pre1.subtract(center1).normalize().add(center1.subtract(center2).normalize());
		nor2 = post2.subtract(center2).normalize().add(center2.subtract(center1).normalize());
		Cylinder c = new Cylinder(center2, center1, nor2, nor1, cylRadius, cylRadius, cylCuts);
		segments.add(c);
	    }
	}
    }

    @Override
    public ModelObject setColor(Color c)
    {
	super.setColor(c);
	for (Cylinder seg : segments)
	{
	    seg.setColor(c);
	}
	return this;
    }

    @Override
    public ModelObject setColor(int c, int op)
    {
	super.setColor(c, op);
	for (Cylinder seg : segments)
	{
	    seg.setColor(c, op);
	}
	return this;
    }

    @Override
    public void render()
    {
	for (Cylinder seg : segments)
	{
	    seg.render();
	}
    }

}
