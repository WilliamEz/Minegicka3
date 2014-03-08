package com.williameze.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

public class Sphere extends ModelObject
{
    public double orgX, orgY, orgZ;
    public double radius;
    public int stacks;
    public int slices;
    public List<Triangle> faces = new ArrayList();

    public Sphere(double orX, double orY, double orZ, double r, int stack, int slice)
    {
	orgX = orX;
	orgY = orY;
	orgZ = orZ;
	radius = r;
	stacks = stack;
	slices = slice;
	calculateVectexes();
    }

    public void calculateVectexes()
    {
	double vertRotAngle = Math.PI / stacks;
	double horzRotAngle = Math.PI * 2 / slices;
	Vector vert = new Vector(0, radius, 0);
	for (int a = 0; a < stacks; a++)
	{
	    for (int b = 0; b < slices; b++)
	    {
		Vector v1 = vert.rotateAround(Vector.unitX, a * vertRotAngle).rotateAround(Vector.unitY, b * horzRotAngle);
		Vector v2 = vert.rotateAround(Vector.unitX, (a + 1) * vertRotAngle).rotateAround(Vector.unitY, b * horzRotAngle);
		Vector v3 = vert.rotateAround(Vector.unitX, a * vertRotAngle).rotateAround(Vector.unitY, (b + 1) * horzRotAngle);
		Vector v4 = vert.rotateAround(Vector.unitX, (a + 1) * vertRotAngle).rotateAround(Vector.unitY, (b + 1) * horzRotAngle);
		if (a < stacks - 1)
		{
		    Triangle tri1 = new Triangle(v1, v2, v4);
		    faces.add(tri1);
		}
		if (a > 0)
		{
		    Triangle tri2 = new Triangle(v1, v3, v4);
		    faces.add(tri2);
		}
	    }
	}
    }

    @Override
    public void render()
    {
	GL11.glPushMatrix();
	GL11.glTranslated(orgX, orgY, orgZ);
	GL11.glBegin(GL11.GL_TRIANGLES);
	glSetColor();
	for (Triangle t : faces)
	{
	    t.addTriangleToGL();
	}
	GL11.glEnd();
	glResetColor();
	GL11.glPopMatrix();
    }

}
