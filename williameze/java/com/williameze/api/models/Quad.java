package com.williameze.api.models;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Line;
import com.williameze.api.math.Plane;
import com.williameze.api.math.Vector;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;

public class Quad extends ModelObject
{
    public Vector v1, v2, v3, v4;
    public Vector tv1, tv2, tv3, tv4;
    public Vector normal;
    public double currentScale = 1;

    public Quad(Vector vec1, Vector vec2, Vector vec3, Vector vec4)
    {
	this(vec1, vec2, vec3, vec4, Vector.median(vec1, vec2, vec3, vec4), false);
    }

    public Quad(Vector vec1, Vector vec2, Vector vec3, Vector vec4, Vector nor, boolean setNormalDirectly)
    {
	v1 = vec1;
	v2 = vec2;
	v3 = vec3;
	v4 = vec4;
	if (setNormalDirectly)
	{
	    normal = nor.normalize();
	}
	else
	{
	    Vector n1 = v2.subtract(v1).crossProduct(v3.subtract(v1));
	    Vector n2 = v2.subtract(v4).crossProduct(v3.subtract(v4));
	    Vector n = n1.add(n2).normalize();
	    if (n.dotProduct(nor) >= 0) normal = n;
	    else normal = n.reverse();
	}
	// orderVertexesCounterClockwise();
    }

    public Quad orderVertexesCounterClockwise()
    {
	// if(true) return this;

	if (normal == null || normal.isZeroVector()) return this;
	Vector mid = getMidVec();
	Line normalLine = new Line(mid, normal);

	Vector pointV1 = v1.subtract(mid);
	Vector pointV2 = v2.subtract(mid);
	Vector pointV3 = v3.subtract(mid);
	Vector pointV4 = v4.subtract(mid);
	double angleV2 = pointV2.getAngleBetween(pointV1);
	double angleV3 = pointV3.getAngleBetween(pointV1);
	double angleV4 = pointV4.getAngleBetween(pointV1);
	if (normal.dotProduct(pointV1.crossProduct(pointV2)) < 0) angleV2 = Math.PI * 2 - angleV2;
	if (normal.dotProduct(pointV1.crossProduct(pointV3)) < 0) angleV3 = Math.PI * 2 - angleV3;
	if (normal.dotProduct(pointV1.crossProduct(pointV4)) < 0) angleV4 = Math.PI * 2 - angleV4;

	Vector dummy2, dummy3, dummy4;
	if (angleV2 >= angleV3 && angleV2 >= angleV4)
	{
	    dummy4 = v2;
	    if (angleV4 > angleV3)
	    {
		dummy3 = v4;
		dummy2 = v3;
	    }
	    else
	    {
		dummy3 = v3;
		dummy2 = v4;
	    }
	}
	else if (angleV3 >= angleV2 && angleV3 >= angleV4)
	{
	    dummy4 = v3;
	    if (angleV4 > angleV2)
	    {
		dummy3 = v4;
		dummy2 = v2;
	    }
	    else
	    {
		dummy3 = v2;
		dummy2 = v4;
	    }
	}
	else
	{
	    dummy4 = v4;
	    if (angleV3 > angleV2)
	    {
		dummy3 = v3;
		dummy2 = v2;
	    }
	    else
	    {
		dummy3 = v2;
		dummy2 = v3;
	    }
	}
	v2 = dummy2;
	v3 = dummy3;
	v4 = dummy4;
	return this;
    }

    public Vector getMidVec()
    {
	return Vector.median(v1, v2, v3, v4);
    }

    public Quad scaleRelative(double d)
    {
	return scaleDefinite(d * currentScale);
    }

    public Quad scaleDefinite(double d)
    {
	Vector mid = getMidVec();
	v1 = mid.add(v1.subtract(mid).multiply(d / currentScale));
	v2 = mid.add(v2.subtract(mid).multiply(d / currentScale));
	v3 = mid.add(v3.subtract(mid).multiply(d / currentScale));
	v4 = mid.add(v4.subtract(mid).multiply(d / currentScale));
	currentScale = d;
	return this;
    }

    public Quad translate(Vector v)
    {
	v1 = v1.add(v);
	v2 = v2.add(v);
	v3 = v3.add(v);
	v4 = v4.add(v);
	return this;
    }

    public Quad rotateAroundNormal(double rad)
    {
	Vector mid = getMidVec();
	return rotate(mid, normal, rad);
    }

    public Quad rotate(Vector pivot, Vector axis, double rad)
    {
	v1 = v1.subtract(pivot).rotateAround(axis, rad).add(pivot);
	v2 = v2.subtract(pivot).rotateAround(axis, rad).add(pivot);
	v3 = v3.subtract(pivot).rotateAround(axis, rad).add(pivot);
	v4 = v4.subtract(pivot).rotateAround(axis, rad).add(pivot);
	return this;
    }

    public void addQuadToGL()
    {
	boolean texturize = tv1 != null && tv2 != null && tv3 != null && tv4 != null;
	setNormal(normal);
	if (texturize) addTextureUV(tv1.x, tv1.y);
	addVertex(v1);
	if (texturize) addTextureUV(tv2.x, tv2.y);
	addVertex(v2);
	if (texturize) addTextureUV(tv3.x, tv3.y);
	addVertex(v3);
	if (texturize) addTextureUV(tv4.x, tv4.y);
	addVertex(v4);
    }

    public void addReverseQuadToGL()
    {
	boolean texturize = tv1 != null && tv2 != null && tv3 != null && tv4 != null;
	setNormal(normal);
	if (texturize) addTextureUV(tv1.x, tv1.y);
	addVertex(v1);
	if (texturize) addTextureUV(tv4.x, tv4.y);
	addVertex(v4);
	if (texturize) addTextureUV(tv3.x, tv3.y);
	addVertex(v3);
	if (texturize) addTextureUV(tv2.x, tv2.y);
	addVertex(v2);
    }

    @Override
    public void doRender()
    {
	GL11.glPushMatrix();
	begin(GL11.GL_QUADS);
	glSetColor();
	addQuadToGL();
	end();
	glResetColor();
	GL11.glPopMatrix();
    }
}
