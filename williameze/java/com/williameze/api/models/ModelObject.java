package com.williameze.api.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ModelObject
{
    public static Tessellator tess = Tessellator.instance;
    public List<Quad> childQuads = new ArrayList();
    public Color color = Color.white;
    
    public ModelObject setColor(Color c)
    {
	color = c;
	return this;
    }

    public ModelObject setColor(int c, int op)
    {
	color = new Color(c);
	int red = color.getRed();
	int green = color.getGreen();
	int blue = color.getBlue();
	color = new Color(red,green,blue,op);
	return this;
    }
    
    public void glResetColor()
    {
	GL11.glColor4d(1, 1, 1, 1);
    }
    
    public void glSetColor()
    {
	GL11.glColor4d(color.getRed()/255D, color.getGreen()/255D, color.getBlue()/255D, color.getAlpha()/255D);
    }

    public abstract void render();
    
    public void addVertex(Vector v)
    {
	GL11.glVertex3d(v.x, v.y, v.z);
    }
}
