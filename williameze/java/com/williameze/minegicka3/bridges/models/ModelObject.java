package com.williameze.minegicka3.bridges.models;

import net.minecraft.client.renderer.Tessellator;

import com.williameze.minegicka3.bridges.math.Vector;

public abstract class ModelObject
{
    public static Tessellator tess = Tessellator.instance;
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
