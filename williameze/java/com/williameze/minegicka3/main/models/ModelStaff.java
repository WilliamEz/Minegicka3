package com.williameze.minegicka3.main.models;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.api.models.ModelObject;
import com.williameze.api.models.Sphere;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModelStaff
{
    public static ModelStaffDefault defaultStaffModel = new ModelStaffDefault();
    public List<ModelObject> components = new ArrayList();

    public ModelStaff()
    {
    }

    public void addComponents()
    {

    }

    public void render(ItemStack staff)
    {
	doRenderParameters(staff);
    }

    public void doRenderParameters(ItemStack staff)
    {
	GL11.glPushMatrix();
	GL11.glScaled(1 / 16D, 1 / 16D, 1 / 16D);
	doRenderComponents(staff);
	GL11.glPopMatrix();
    }

    public void doRenderComponents(ItemStack staff)
    {
	renderList(staff, components);
    }

    public void renderList(ItemStack staff, List<ModelObject> l)
    {
	for (ModelObject o : l)
	{
	    onComponentPreRender(staff, o);
	    o.render();
	    onComponentPostRender(staff, o);
	}
    }

    public void onComponentPreRender(ItemStack staff, ModelObject o)
    {

    }

    public void onComponentPostRender(ItemStack staff, ModelObject o)
    {

    }
}
