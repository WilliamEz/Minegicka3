package com.williameze.api.models;

import java.awt.Color;

import com.williameze.api.math.Vector;

public class VertexData
{
    public Vector position;
    public Vector normal;
    public Vector specular;
    public Color color;

    public VertexData()
    {
	position = Vector.root.copy();
	normal = Vector.unitY.copy();
	specular = Vector.unitY.copy();
	color = new Color(0, 0, 0);
    }

    public VertexData(Vector p)
    {
	this();
	position = p;
    }

    public VertexData(Vector p, Vector n)
    {
	this();
	position = p;
	normal = n;
    }

    public VertexData(Vector p, Vector n, Color c)
    {
	this();
	position = p;
	normal = n;
	color = c;
    }

    public VertexData copy()
    {
	return new VertexData(position.copy(), normal.copy(), new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
    }
}
