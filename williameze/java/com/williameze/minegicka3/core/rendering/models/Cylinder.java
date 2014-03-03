package com.williameze.minegicka3.core.rendering.models;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.williameze.minegicka3.bridges.Vector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Cylinder implements IModelComponent
{
    public int color;
    public int opacity;
    public List<Vertex> topVertexes = new ArrayList();
    public List<Vertex> bottomVertexes = new ArrayList();
    
    public Cylinder(Vertex from, Vertex to, Vector fromPlaneNormal, Vector toPlaneNormal, double radius, int cuts)
    {
	
    }

    public Cylinder setColor(int c)
    {
	color = c;
	return this;
    }

    public Cylinder setOpacity(int op)
    {
	opacity = op;
	return this;
    }

    @Override
    public void render()
    {
	GL11.glPushMatrix();
	addVertexesAndDraw();
	GL11.glPopMatrix();
    }

    public void addVertexesAndDraw()
    {
	
    }
    
}
