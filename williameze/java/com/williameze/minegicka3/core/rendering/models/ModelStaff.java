package com.williameze.minegicka3.core.rendering.models;

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

@SideOnly(Side.CLIENT)
public class ModelStaff
{
    public static ModelStaffDefault defaultStaffModel = new ModelStaffDefault();
    public List<ModelObject> components = new ArrayList();

    public ModelStaff()
    {
	addComponents();
    }

    public void addComponents()
    {
	
    }
    
    public void render(ItemStack staff)
    {
	doRenderComponents(staff);
    }

    public void doRenderComponents(ItemStack staff)
    {
	GL11.glPushMatrix();
	GL11.glScaled(1/16D, 1/16D, 1/16D);
	for (ModelObject cmp : components)
	{
	    cmp.render();
	}
	GL11.glPopMatrix();
    }
}
