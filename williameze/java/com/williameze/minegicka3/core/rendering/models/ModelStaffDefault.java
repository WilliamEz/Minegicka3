package com.williameze.minegicka3.core.rendering.models;

import net.minecraft.item.ItemStack;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.api.models.Sphere;

public class ModelStaffDefault extends ModelStaff
{
    public ModelStaffDefault()
    {
	super();
    }

    @Override
    public void addComponents()
    {
	//components.add(new Sphere(0, 95, 0, 40, 4, 4));
	components.add(Cylinder.create(new Vector(0, 80, 0), new Vector(0,10,0), 5, 20));
    }
    
    @Override
    public void render(ItemStack staff)
    {
	components.clear();
	addComponents();
	doRenderComponents(staff);
    }
}
