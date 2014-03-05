package com.williameze.minegicka3.bridges.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import com.williameze.minegicka3.bridges.math.Vector;

public class Sphere extends ModelObject
{
    double orgX, orgY, orgZ;
    double radius;
    List<List<Vector>> vertexStacks;

    public Sphere(double orX, double orY, double orZ, double r)
    {
	orgX = orX;
	orgY = orY;
	orgZ = orZ;
	radius = r;
	calculateVectexes();
    }

    public void calculateVectexes()
    {
	vertexStacks = new ArrayList();
	vertexStacks.add(Arrays.asList(new Vector(0, radius, 0)));
	int stacks = (int) (Math.max(4, Math.pow(radius, 0.5) * 16));
	int slices = stacks * 2;
	Vector latitude = new Vector(0, radius, 0);
	for (int a = 1; a <= stacks; a++)
	{
	    List<Vector> aStack = new ArrayList();

	    latitude.rotateAroundX((Math.PI / stacks));
	    Vector longitude = new Vector(latitude.x, latitude.y, latitude.z);

	    for (int b = 0; b <= slices; b++)
	    {
		longitude.rotateAroundY((Math.PI * 2D / slices));
		aStack.add(new Vector(longitude.x, longitude.y, longitude.z));
	    }

	    vertexStacks.add(aStack);
	}
	vertexStacks.add(Arrays.asList(new Vector(0, -radius, 0)));
    }

    @Override
    public void render()
    {
	GL11.glPushMatrix();
	GL11.glTranslated(orgX, orgY, orgZ);
	if (vertexStacks.size() > 2)
	{
	    tess.setColorRGBA_I(color, opacity);
	    int size = vertexStacks.size();
	    for (int a = 0; a < size-1; a++)
	    {
		if (a == 0)
		{
		    tess.startDrawing(GL11.GL_TRIANGLE_FAN);
		    addVertex(vertexStacks.get(0).get(0));
		    for (Vector v : vertexStacks.get(1))
		    {
			addVertex(v);
		    }
		    tess.draw();
		}
		else if (a == size - 2)
		{
		    tess.startDrawing(GL11.GL_TRIANGLE_FAN);
		    addVertex(vertexStacks.get(size-1).get(0));
		    for (Vector v : vertexStacks.get(size-2))
		    {
			addVertex(v);
		    }
		    tess.draw();
		}
		else
		{
		    List<Vector> stackTop = vertexStacks.get(a);
		    List<Vector> stackBottom = vertexStacks.get(a+1);
		    int slices = stackTop.size();
		    tess.startDrawing(GL11.GL_TRIANGLE_STRIP);
		    for (int b = 0; b < slices; b++)
		    {
			addVertex(stackTop.get(b));
			addVertex(stackBottom.get(b));
		    }
		    tess.draw();
		}
	    }
	}
	GL11.glPopMatrix();
    }

    @Override
    public void addVertex(Vector v)
    {
	GL11.glNormal3d(v.x, v.y, v.z);
	tess.addVertex(v.x, v.y, v.z);
    }
}
