package com.williameze.api.models;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Plane;
import com.williameze.api.math.Vector;

public class Ring extends ModelObject
{
    public Vector center;
    public Vector normal;
    public double innerRadius, outerRadius;
    public int loops;
    public List<Vector> vertexes = new ArrayList();

    public Ring(Vector cent, Vector nor, double inner, double outer, int loop)
    {
	center = cent;
	normal = nor;
	innerRadius = inner;
	outerRadius = outer;
	loops = loop;
	calculateVertexes();
    }

    public void calculateVertexes()
    {
	Plane p = new Plane(normal, center);
	Vector point = p.getAssurancePoint();
	Vector toward = point.subtract(center).normalize();
	double angle = Math.PI * 2D / loops;
	for (int a = 0; a <= loops; a++)
	{
	    toward = toward.rotateAround(normal, angle);
	    vertexes.add(center.add(toward.multiply(innerRadius)));
	    vertexes.add(center.add(toward.multiply(outerRadius)));
	}
    }

    @Override
    public void render()
    {
	GL11.glPushMatrix();
	begin(GL11.GL_QUAD_STRIP);
	glSetColor();
	setNormal(normal);
	for (Vector v : vertexes)
	{
	    addVertex(v);
	}
	end();
	glResetColor();
	GL11.glPopMatrix();
    }

}
