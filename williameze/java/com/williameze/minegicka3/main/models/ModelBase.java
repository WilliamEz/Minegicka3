package com.williameze.minegicka3.main.models;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.williameze.api.models.ModelObject;

public class ModelBase
{
    public List<ModelObject> components = new ArrayList();

    public ModelBase()
    {
	addComponents();
    }

    public void addComponents()
    {

    }

    public void render(Entity e, float f)
    {
	GL11.glPushMatrix();
	doRenderParameters(e, f);
	doRenderComponents(e, f);
	GL11.glPopMatrix();
    }

    public void doRenderParameters(Entity e, float f)
    {
	// GL11.glScaled(1 / 16D, 1 / 16D, 1 / 16D);
    }

    public void doRenderComponents(Entity e, float f)
    {
	renderList(e, f, components);
    }

    public void renderList(Entity e, float f, List<ModelObject> l)
    {
	for (ModelObject o : l)
	{
	    onComponentPreRender(e, f, o);
	    o.render();
	    onComponentPostRender(e, f, o);
	}
    }

    public void onComponentPreRender(Entity e, float f, ModelObject o)
    {

    }

    public void onComponentPostRender(Entity e, float f, ModelObject o)
    {

    }
}
