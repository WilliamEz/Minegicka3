package com.williameze.minegicka3.core.rendering.models;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.williameze.minegicka3.bridges.math.Vector;
import com.williameze.minegicka3.bridges.models.Cylinder;
import com.williameze.minegicka3.bridges.models.ModelObject;
import com.williameze.minegicka3.bridges.models.Sphere;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelStaff
{
    public static ModelStaff defaultModel = new ModelStaff();
    public List<ModelObject> components = new ArrayList();

    public ModelStaff()
    {
	addDefaultStaffComponents();
    }

    public void addDefaultStaffComponents()
    {
	//components.add(new Sphere(0, 0, 0, 3));
	components.add(new Cylinder(new Vector(0, 8, 0), new Vector(0, 0, 0), new Vector(0, 1, 0), new Vector(0, -1, 0), 2, 2, 8));
    }

    public void render(ItemStack staff)
    {
	components.clear();
	addDefaultStaffComponents();
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
