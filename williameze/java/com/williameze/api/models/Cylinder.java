package com.williameze.api.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Cylinder extends ModelObject
{
    public boolean renderCaps = true;
    public Vector center1;
    public Vector center2;
    public Vector face1Normal;
    public Vector face2Normal;
    public double radius1;
    public double radius2;
    public int cuts;
    public List<Vector> face1, face2;
    public List<Quad> sideQuads;

    public static Cylinder create(Vector cen1, Vector cen2, double radius1, int cuts)
    {
	return create(cen1, cen2, radius1, radius1, cuts, 0);
    }

    /**
     * 
     * @param cen1
     * @param cen2
     * @param radius1
     * @param cuts
     *            amounts of side faces
     * @param type
     *            0: normal, 1: slanted against Y, 2: slanted against X, 3: slanted against Z
     * @return
     */
    public static Cylinder create(Vector cen1, Vector cen2, double radius1, double radius2, int cuts, int type)
    {
	if (type == 1)
	{
	    return new Cylinder(cen1, cen2, new Vector(0, 1, 0), new Vector(0, -1, 0), radius1, radius2, cuts);
	}
	else if (type == 2)
	{
	    return new Cylinder(cen1, cen2, new Vector(1, 0, 0), new Vector(-1, 0, 0), radius1, radius2, cuts);
	}
	else if (type == 3)
	{
	    return new Cylinder(cen1, cen2, new Vector(0, 0, 1), new Vector(0, 0, -1), radius1, radius2, cuts);
	}
	else
	{
	    return new Cylinder(cen1, cen2, cen2.subtract(cen1.add(cen2).multiply(0.5)), cen1.subtract(cen1.add(cen2).multiply(0.5)), radius1,
		    radius2, cuts);
	}
    }

    public Cylinder(Vector from, Vector to, Vector fromPlaneNormal, Vector toPlaneNormal, double radiusFrom, double radiusTo, int cuts)
    {
	this.center1 = from;
	this.center2 = to;
	this.face1Normal = fromPlaneNormal;
	this.face2Normal = toPlaneNormal;
	this.radius1 = radiusFrom;
	this.radius2 = radiusTo;
	this.cuts = cuts;
	makeCylinder();
	untwist();
    }

    public Cylinder makeCylinder()
    {
	face1 = new ArrayList();
	face2 = new ArrayList();
	sideQuads = new ArrayList();
	face1Normal = face1Normal.normalize();
	face2Normal = face2Normal.normalize();
	face1.add(face1Normal);
	face2.add(face2Normal);

	Vector axis = face1Normal.crossProduct(face2Normal);
	if (!axis.isZeroVector())
	{
	    axis = axis.normalize();
	}
	else
	{
	    axis = face1Normal.crossProduct(!(face1Normal.y == 0 && face1Normal.z == 0) ? face1Normal.add(1, 0, 0) : face1Normal.add(0, 0, 1));
	    axis = axis.rotateAround(face1Normal, Math.PI / 4);
	}
	Vector face1Circling = face1Normal.rotateAround(axis, Math.PI / 2).normalize().multiply(radius1);
	Vector face2Circling = face2Normal.rotateAround(axis, -Math.PI / 2).normalize().multiply(radius2);
	for (int a = 0; a < cuts; a++)
	{

	    Vector v1 = center2.add(face2Circling);
	    Vector v2 = center1.add(face1Circling);
	    face1Circling = face1Circling.rotateAround(face1Normal, Math.PI * 2D / cuts);
	    face2Circling = face2Circling.rotateAround(face2Normal, -Math.PI * 2D / cuts);
	    Vector v3 = center1.add(face1Circling);
	    Vector v4 = center2.add(face2Circling);

	    face2.add(v1);
	    face1.add(v2);

	    Vector normalGuide = Vector.median(v1, v2, v3, v4).subtract(center1.add(center2).multiply(0.5));
	    sideQuads.add(new Quad(v1, v2, v3, v4, normalGuide, true));
	}
	return this;
    }

    public Cylinder untwist()
    {
	if (center1.subtract(center1.add(center2).multiply(0.5)).dotProduct(face1Normal) > 0)
	{
	    if (center2.subtract(center1.add(center2).multiply(0.5)).dotProduct(face2Normal) < 0) face2Normal.multiply(-1);
	}
	else
	{
	    if (center2.subtract(center1.add(center2).multiply(0.5)).dotProduct(face2Normal) > 0) face2Normal.multiply(-1);
	}
	return this;
    }

    public Cylinder setRenderCaps(boolean b)
    {
	renderCaps = b;
	return this;
    }
    
    @Override
    public void render()
    {
	GL11.glPushMatrix();

	if (renderCaps)
	{
	    begin(GL11.GL_POLYGON);
	    glSetColor();
	    GL11.glNormal3d(face1.get(0).x, face1.get(0).y, face1.get(0).z);
	    for (int a = 1; a < face1.size(); a++)
	    {
		addVertex(face1.get(a));
	    }
	    end();
	    glResetColor();
	    
	    begin(GL11.GL_POLYGON);
	    glSetColor();
	    GL11.glNormal3d(face2.get(0).x, face2.get(0).y, face2.get(0).z);
	    for (int a = 1; a < face2.size(); a++)
	    {
		addVertex(face2.get(a));
	    }
	    end();
	    glResetColor();
	}

	begin(GL11.GL_QUADS);
	glSetColor();
	for (Quad q : sideQuads)
	{
	    q.addQuadToGL();
	}
	end();
	glResetColor();

	GL11.glPopMatrix();
    }

}
