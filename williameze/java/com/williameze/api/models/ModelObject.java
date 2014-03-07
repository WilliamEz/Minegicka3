package com.williameze.api.models;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;

import com.williameze.api.math.Vector;

public abstract class ModelObject
{
    public static Tessellator tess = Tessellator.instance;
    public List<Quad> childQuads = new ArrayList();
    public int color = 0xffffff;
    public int opacity = 255;

    public ModelObject setColor(int c)
    {
	color = c;
	return this;
    }

    public ModelObject setOpacity(int op)
    {
	opacity = op;
	return this;
    }

    public abstract void render();
    
    public void addVertex(Vector v)
    {
	tess.addVertex(v.x, v.y, v.z);
    }
}
