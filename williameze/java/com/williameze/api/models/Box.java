package com.williameze.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

public class Box extends ModelObject
{
    public List<Quad> faces = new ArrayList();

    public Box(double minx, double miny, double minz, double maxx, double maxy, double maxz)
    {
	this(new Vector(minx, maxy, minz), new Vector(minx, maxy, maxz), new Vector(maxx, maxy, maxz), new Vector(maxx, maxy, minz), new Vector(minx, miny, minz),
		new Vector(minx, miny, maxz), new Vector(maxx, miny, maxz), new Vector(maxx, miny, minz));
    }

    public Box(Vector v01, Vector v02, Vector v03, Vector v04, Vector v11, Vector v12, Vector v13, Vector v14)
    {
	Vector med = Vector.median(v01,v02,v03,v04,v11,v12,v13,v14);
	Quad top = new Quad(v01, v02, v03, v04, v01.subtract(med), false).orderVertexesCounterClockwise();
	Quad bottom = new Quad(v11, v12, v13, v14, v11.subtract(med), false).orderVertexesCounterClockwise();
	Quad left = new Quad(top.v1, top.v2, bottom.v1, bottom.v2, top.v1.subtract(med), false).orderVertexesCounterClockwise();
	Quad front = new Quad(top.v3, top.v2, bottom.v3, bottom.v2, top.v3.subtract(med), false).orderVertexesCounterClockwise();
	Quad right = new Quad(top.v3, top.v4, bottom.v3, bottom.v4, top.v3.subtract(med), false).orderVertexesCounterClockwise();
	Quad back = new Quad(top.v1, top.v4, bottom.v1, bottom.v4, top.v1.subtract(med), false).orderVertexesCounterClockwise();
	faces.clear();
	faces.addAll(Arrays.asList(top,bottom,left,front,right,back));
    }
    
    @Override
    public void render()
    {
	GL11.glPushMatrix();
	tess.setColorRGBA_I(color, opacity);
	for(Quad f : faces)
	{
	    tess.startDrawingQuads();
	    f.addQuadToTess(tess);
	    tess.draw();
	}
	GL11.glPushMatrix();
    }

}
