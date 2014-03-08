package com.williameze.api.models;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Line;
import com.williameze.api.math.Plane;
import com.williameze.api.math.Vector;

public class Triangle extends ModelObject
{
    public Vector v1, v2, v3;
    public Vector normal;
    public double currentScale = 1;

    public Triangle(Vector vec1, Vector vec2, Vector vec3)
    {
	this(vec1, vec2, vec3, new Vector((vec1.x + vec2.x + vec3.x) / 3, (vec1.y + vec2.y + vec3.y) / 3, (vec1.z + vec2.z + vec3.z) / 3), false);
    }

    public Triangle(Vector vec1, Vector vec2, Vector vec3, Vector nor, boolean setNormalDirectly)
    {
	v1 = vec1;
	v2 = vec2;
	v3 = vec3;
	if (setNormalDirectly)
	{
	    normal = nor.normalize();
	}
	else
	{
	    Vector n = v2.subtract(v1).crossProduct(v3.subtract(v1)).normalize();
	    if (n.dotProduct(nor) >= 0) normal = n;
	    else normal = n.multiply(-1);
	}
	orderVertexesCounterClockwise();
    }

    public Triangle orderVertexesCounterClockwise()
    {
	Vector mid = getMidVec();
	Line normalLine = new Line(mid, normal);

	Vector pointV1 = v1.subtract(normalLine.intersectWith(new Plane(normal, v1)));
	Vector pointV2 = v2.subtract(normalLine.intersectWith(new Plane(normal, v2)));
	Vector pointV3 = v3.subtract(normalLine.intersectWith(new Plane(normal, v3)));
	double angleV2 = pointV2.getAngleBetween(pointV1);
	double angleV3 = pointV3.getAngleBetween(pointV1);
	if (normal.dotProduct(pointV1.crossProduct(pointV2)) < 0) angleV2 = Math.PI * 2 - angleV2;
	if (normal.dotProduct(pointV1.crossProduct(pointV3)) < 0) angleV3 = Math.PI * 2 - angleV3;

	Vector dummy2, dummy3;
	if (angleV2 >= angleV3)
	{
	    dummy3 = v2;
	    dummy2 = v3;
	}
	else
	{
	    dummy3 = v3;
	    dummy2 = v2;
	}
	v2 = dummy2;
	v3 = dummy3;
	return this;
    }

    public Vector getMidVec()
    {
	return new Vector((v1.x + v2.x + v3.x) / 3, (v1.y + v2.y + v3.y) / 3, (v1.z + v2.z + v3.z) / 3);
    }

    public Triangle scaleRelative(double d)
    {
	return scaleDefinite(d * currentScale);
    }

    public Triangle scaleDefinite(double d)
    {
	Vector mid = getMidVec();
	v1 = mid.add(v1.subtract(mid).multiply(d / currentScale));
	v2 = mid.add(v2.subtract(mid).multiply(d / currentScale));
	v3 = mid.add(v3.subtract(mid).multiply(d / currentScale));
	currentScale = d;
	return this;
    }

    public Triangle translate(Vector v)
    {
	v1 = v1.add(v);
	v2 = v2.add(v);
	v3 = v3.add(v);
	return this;
    }

    public Triangle rotateAroundNormal(double rad)
    {
	Vector mid = getMidVec();
	return rotate(mid, normal, rad);
    }

    public Triangle rotate(Vector pivot, Vector axis, double rad)
    {
	v1 = v1.subtract(pivot).rotateAround(axis, rad).add(pivot);
	v2 = v2.subtract(pivot).rotateAround(axis, rad).add(pivot);
	v3 = v3.subtract(pivot).rotateAround(axis, rad).add(pivot);
	return this;
    }

    public void addTriangleToGL()
    {
	GL11.glNormal3d(normal.x, normal.y, normal.z);
	GL11.glVertex3d(v1.x, v1.y, v1.z);
	GL11.glVertex3d(v2.x, v2.y, v2.z);
	GL11.glVertex3d(v3.x, v3.y, v3.z);
    }

    @Override
    public void render()
    {
	GL11.glPushMatrix();
	GL11.glBegin(GL11.GL_TRIANGLES);
	glSetColor();
	addTriangleToGL();
	GL11.glEnd();
	glResetColor();
	GL11.glPopMatrix();
    }
}
