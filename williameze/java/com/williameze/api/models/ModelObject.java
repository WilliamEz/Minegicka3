package com.williameze.api.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ModelObject
{
    public static Tessellator tess = Tessellator.instance;
    public static TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

    public ResourceLocation texture;
    public boolean useTexture = false;
    public List<ModelObject> childModels = new ArrayList();
    public Color color = Color.white;
    public Vector pivot = Vector.root.copy();
    public Vector translate = Vector.root.copy();
    public double rotationYaw, rotationPitch;
    public double scale = 1;

    public ModelObject setPivot(Vector v)
    {
	if (v != null) pivot = v;
	return this;
    }

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
	color = new Color(red, green, blue, op);
	return this;
    }

    public ModelObject setTexture(String path)
    {
	return setTexture(new ResourceLocation(path));
    }

    public ModelObject setTexture(ResourceLocation tex)
    {
	useTexture = tex != null ? true : false;
	texture = tex;
	return this;
    }

    public ModelObject setTextureQuad(double minX, double minY, double maxX, double maxY, double... additional)
    {
	return this;
    }

    public ModelObject setScale(double d)
    {
	scale = d;
	return this;
    }

    public ModelObject setTranslation(Vector v)
    {
	translate = v.copy();
	return this;
    }

    public ModelObject setTranslation(double x, double y, double z)
    {
	translate = new Vector(x, y, z);
	return this;
    }

    public ModelObject setRotation(double yaw, double pitch)
    {
	rotationPitch = pitch;
	rotationYaw = yaw;
	return this;
    }

    public void render()
    {
	GL11.glPushMatrix();
	preRenderScale();
	preRenderTranslate();
	preRenderRotate();
	if (!useTexture || texture == null) GL11.glDisable(GL11.GL_TEXTURE_2D);
	else
	{
	    if (textureManager == null)
	    {
		textureManager = Minecraft.getMinecraft().getTextureManager();
	    }
	    textureManager.bindTexture(texture);
	}
	doRender();
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	for (ModelObject child : childModels)
	{
	    child.render();
	}
	GL11.glPopMatrix();
    }

    public void preRenderTranslate()
    {
	if (translate != null)
	{
	    GL11.glTranslated(translate.x, translate.y, translate.z);
	}
    }

    public void preRenderRotate()
    {
	if (pivot != null) GL11.glTranslated(pivot.x, pivot.y, pivot.z);
	GL11.glRotated(rotationYaw, 0, 1, 0);
	GL11.glRotated(rotationPitch, 1, 0, 0);
	if (pivot != null) GL11.glTranslated(-pivot.x, -pivot.y, -pivot.z);
    }

    public void preRenderScale()
    {
	GL11.glScaled(scale, scale, scale);
    }

    public abstract void doRender();

    public void begin(int i)
    {
	GL11.glBegin(i);
    }

    public void addVertex(double x, double y, double z)
    {
	if (useTexture && texture != null)
	{
	    addTextureUVFor(x, y, z);
	}
	GL11.glVertex3d(x, y, z);
    }

    public void addVertex(Vector v)
    {
	addVertex(v.x, v.y, v.z);
    }

    public void addTextureUVFor(double x, double y, double z)
    {

    }

    public void addTextureUV(double x, double y)
    {
	GL11.glTexCoord2d(x, y);
    }

    public void glResetColor()
    {
	GL11.glColor4d(1, 1, 1, 1);
    }

    public void glSetColor()
    {
	glSetColor(color);
    }

    public void glSetColor(Color c)
    {
	GL11.glColor4ub((byte) c.getRed(), (byte) c.getGreen(), (byte) c.getBlue(), (byte) c.getAlpha());
    }

    public void setNormal(double x, double y, double z)
    {
	GL11.glNormal3d(x, y, z);
    }

    public void setNormal(Vector v)
    {
	setNormal(v.x, v.y, v.z);
    }

    public void end()
    {
	GL11.glEnd();
    }
}
