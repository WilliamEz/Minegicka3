package com.williameze.api.models;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBase
{
    public List<ModelObject> components = new ArrayList();

    public ModelBase()
    {
	addComponents();
    }

    public ModelBase(ModelObject... obj)
    {
	this();
	for (int a = 0; a < obj.length; a++)
	{
	    components.add(obj[a]);
	}
    }

    public void addComponents()
    {
	components.clear();
    }

    public void render(Object obj, float f)
    {
	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	preRender(obj, f);
	renderComponents(obj, f);
	postRender(obj, f);
	GL11.glPopMatrix();
    }

    public void preRender(Object obj, float f)
    {
    }

    public void renderComponents(Object obj, float f)
    {
	renderList(obj, f, components);
    }

    public void renderList(Object obj, float f, List<ModelObject> l)
    {
	for (ModelObject o : l)
	{
	    renderComponent(obj, f, o);
	}
    }

    public void renderComponent(Object obj, float f, ModelObject o)
    {
	GL11.glPushMatrix();
	componentPreRender(obj, f, o);
	o.render();
	componentPostRender(obj, f, o);
	GL11.glPopMatrix();
    }

    public void componentPreRender(Object obj, float f, ModelObject o)
    {

    }

    public void componentPostRender(Object obj, float f, ModelObject o)
    {

    }

    public void postRender(Object obj, float f)
    {
    }
}
