package com.williameze.minegicka3.main.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.api.models.CylinderConjunc;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.main.Values;

public class ModelStaffSuper extends ModelStaff
{
    @Override
    public void addComponents()
    {
	Color c = Values.yellow;
	Color c1 = Values.red;
	List<Vector> list = new LinkedList<Vector>();
	list.add(new Vector(0, 12.5, 0));
	list.add(new Vector(0, 12.5, -2.5));
	list.add(new Vector(0, 11.5, -3.2));
	list.add(new Vector(0, 8, -0.6));
	list.add(new Vector(0, 8, 0.6));
	list.add(new Vector(0, 11.5, 3.2));
	list.add(new Vector(0, 12.5, 2.5));
	list.add(new Vector(0, 12.5, -2.5));
	list.add(new Vector(0, 11.5, -3.2));
	components.add(new CylinderConjunc(0.4685, 8, list).setColor(c1));
	list.clear();
	list.add(new Vector(0, 10, 2));
	list.add(new Vector(0, 10.7, 2.3));
	list.add(new Vector(0, 12, 1.25));
	list.add(new Vector(0, 12, -1.25));
	list.add(new Vector(0, 10.9, -2.1));
	list.add(new Vector(0, 10.3, -1.7));
	list.add(new Vector(0, 9.7, 1.1));
	list.add(new Vector(0, 9.5, 1.2));
	list.add(new Vector(0, 8.8, 0.6));
	list.add(new Vector(0, 8.8, -0.6));
	list.add(new Vector(0, 9.3, -1.1));
	list.add(new Vector(0, 9.3, -2));
	components.add(new CylinderConjunc(0.234, 8, list).setColor(c1));

	components.add(Cylinder.create(new Vector(0, 8, 0), new Vector(0, -6, 0), 0.4685, 16).setColor(c));
	components.add(new Sphere(0, -6.25, 0, 0.75, 2, 4).setColor(c1));
    }

    @Override
    public void render(ItemStack staff)
    {
	components.clear();
	addComponents();
	doRenderParameters(staff);
    }

}
