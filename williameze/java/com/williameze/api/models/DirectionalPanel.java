package com.williameze.api.models;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

public abstract class DirectionalPanel extends ModelObject
{
    public List<VertexData> vertexes = new ArrayList();
    public Vector opposeDirection;
    public double thickness;

    public double orX, orY, orZ;

    public DirectionalPanel setThickness(double d)
    {
	thickness = d;
	return this;
    }

    public DirectionalPanel setOrigin(double x, double y, double z)
    {
	orX = x;
	orY = y;
	orZ = z;
	return this;
    }

    public DirectionalPanel setOpposing(Vector v)
    {
	opposeDirection = v;
	return this;
    }

    public DirectionalPanel setOpposing(double x, double y, double z)
    {
	opposeDirection = new Vector(x, y, z);
	return this;
    }

    @Override
    public void doRender()
    {
	double xRot = Math.acos(opposeDirection.normalize().y) / Math.PI * 180 - 90;

	Vector ofY = new Vector(opposeDirection.z, opposeDirection.x, 0).normalize();
	double yRot = ofY.isZeroVector() ? 0 : -Math.atan2(ofY.x, ofY.y) / Math.PI * 180D + 90D;

	dpAddVertexes();
	Vector extendForward = new Vector(0, 0, thickness / 2D);

	GL11.glPushMatrix();
	GL11.glShadeModel(GL11.GL_SMOOTH);
	GL11.glTranslated(orX, orY, orZ);
	GL11.glRotated(yRot, 0, 1, 0);
	GL11.glRotated(xRot, 1, 0, 0);

	begin(dpPrimitive());
	for (VertexData vd : vertexes)
	{
	    VertexData vd1 = vd.copy();
	    vd1.position = vd1.position.add(extendForward);
	    addVertex(vd1);
	}
	end();
	if (thickness != 0)
	{
	    begin(dpPrimitive());
	    for (VertexData vd : vertexes)
	    {
		Vector v1 = opposeDirection.reverse().normalize();
		v1.setToLength(Math.abs(vd.normal.dotProduct(opposeDirection) / opposeDirection.lengthVector() / vd.normal.lengthVector() * 2));
		VertexData vd1 = vd.copy();
		vd1.normal = vd1.normal.add(v1);
		vd1.position = vd1.position.subtract(extendForward);
		addVertex(vd1);
	    }
	    end();

	    begin(GL11.GL_QUAD_STRIP);
	    int init = Math.max(dpInitThickVertexSkip(), 0);
	    int step = Math.max(dpThicknessVertexStep(), 1);
	    for (int a = init; a < vertexes.size(); a += step)
	    {
		VertexData vd = vertexes.get(a).copy();
		vd.position = vd.position.add(extendForward);
		addVertex(vd);
		vd = vertexes.get(a).copy();
		vd.position = vd.position.subtract(extendForward);
		addVertex(vd);
		if (a >= vertexes.size() - step)
		{
		    vd = vertexes.get(init).copy();
		    vd.position = vd.position.add(extendForward);
		    addVertex(vd);
		    vd = vertexes.get(init).copy();
		    vd.position = vd.position.subtract(extendForward);
		    addVertex(vd);
		}
	    }
	    end();
	}

	vertexes.clear();
	glResetColor();
	GL11.glPopMatrix();
    }

    public void addVertex(VertexData v)
    {
	glSetColor(v.color);
	setNormal(v.normal);
	if (v.texture != null) addTextureUV(v.texture.x, v.texture.y);
	addVertex(v.position);
    }

    public abstract void dpAddVertexes();

    public abstract int dpPrimitive();

    public abstract int dpThicknessVertexStep();

    public int dpInitThickVertexSkip()
    {
	return 0;
    }

    public void dpAddVertex(VertexData data)
    {
	vertexes.add(data);
    }
}
