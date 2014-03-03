package com.williameze.minegicka3.core.rendering.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.williameze.minegicka3.bridges.Vector;

public class Sphere implements IModelComponent
{
    double orgX, orgY, orgZ;
    double radius;
    int color;
    int opacity;
    List<List<Vertex>> vertexStacks;

    public Sphere(double orX, double orY, double orZ, double r)
    {
	orgX = orX;
	orgY = orY;
	orgZ = orZ;
	radius = r;
	setColorOpacity(0xffffff, 255);
	calculateVectexes();
    }

    public Sphere setColorOpacity(int c, int o)
    {
	color = c;
	opacity = o;
	return this;
    }

    public void calculateVectexes()
    {
	vertexStacks = new ArrayList();
	vertexStacks.add(Arrays.asList(new Vertex(0, radius, 0)));
	int stacks = (int) (Math.max(4,Math.pow(radius,0.5)*16));
	int slices = stacks*2;
	Vector latitude = new Vector(0, 1, 0);
	for (int a = 1; a <= stacks; a++)
	{
	    List<Vertex> aStack = new ArrayList();

	    latitude.rotateAroundX((float) (Math.PI / stacks));
	    Vector longitude = new Vector(latitude.x, latitude.y, latitude.z);

	    for (int b = 0; b <= slices; b++)
	    {
		longitude.rotateAroundY((float) (Math.PI * 2D / slices));
		aStack.add(new Vertex(longitude.x * radius, longitude.y * radius, longitude.z * radius));
	    }

	    vertexStacks.add(aStack);
	}
	vertexStacks.add(Arrays.asList(new Vertex(0, -radius, 0)));
    }

    @Override
    public void render()
    {
	GL11.glPushMatrix();
	GL11.glTranslated(orgX, orgY, orgZ);
	if (vertexStacks.size() > 2)
	{
	    tess.setColorRGBA_I(color, opacity);
	    int stacks = vertexStacks.size() - 1;
	    int slices = vertexStacks.get(1).size();
	    for (int a = 1; a <= stacks; a++)
	    {
		List<Vertex> stackTop = vertexStacks.get(a - 1);
		List<Vertex> stackBottom = vertexStacks.get(a);
		if (a == 1)
		{
		    tess.startDrawing(GL11.GL_TRIANGLE_FAN);
		    addVertex(stackTop.get(0));
		    for (Vertex v : stackBottom)
		    {
			addVertex(v);
		    }
		    tess.draw();
		}
		else if (a == stacks)
		{
		    tess.startDrawing(GL11.GL_TRIANGLE_FAN);
		    addVertex(stackBottom.get(0));
		    for (Vertex v : stackTop)
		    {
			addVertex(v);
		    }
		    tess.draw();
		}
		else
		{
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

    public void addVertex(Vertex v)
    {
	GL11.glNormal3d(v.x, v.y, v.z);
	tess.addVertex(v.x, v.y, v.z);
    }
}
