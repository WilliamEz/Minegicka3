package com.williameze.minegicka3.core.rendering.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Box;
import com.williameze.api.models.Cylinder;
import com.williameze.api.models.ModelObject;
import com.williameze.api.models.Sphere;
import com.williameze.api.models.Spiral;
import com.williameze.minegicka3.bridges.Values;

public class ModelStaffDefault extends ModelStaff
{
    public Sphere orb;
    
    public ModelStaffDefault()
    {
	super();
	components.clear();
	addComponents();
    }

    @Override
    public void addComponents()
    {
	Color c = new Color(255, 255, 100, 255);
	Color c1 = new Color(255, 255, 130, 255);
	Color c2 = new Color(255, 255, 70, 255);
	Color c3 = new Color(150, 0, 150, 255);
	components.add(Spiral.create(new Vector(0, 8, 0), new Vector(0, 11, 0), new Vector(1, 0, 0), 0, Math.PI / 4, 8, 0,
		0.4685, 16).setColor(c1));
	components.add(orb = (Sphere) new Sphere(0, 11, 0, 1, 2, 4).setColor(c3));
	components.add(Cylinder.create(new Vector(0, 8, 0), new Vector(0, -6, 0), 0.4685, 16).setColor(c));
	components.add(new Sphere(0, -6.25, 0, 0.75, 2, 4).setColor(c2));
    }

    @Override
    public void render(ItemStack staff)
    {
	components.clear();
	addComponents();
	doRenderParameters(staff);
    }
    
    @Override
    public void onComponentPreRender(ModelObject o)
    {
        super.onComponentPreRender(o);
        if(o==orb)
        {
            GL11.glRotated(Values.clientTicked*4, 0, 1, 0);
        }
    }
    
    @Override
    public void onComponentPostRender(ModelObject o)
    {
        super.onComponentPostRender(o);
        if(o==orb)
        {
            GL11.glRotated(-Values.clientTicked*4, 0, 1, 0);
        }
    }
}
