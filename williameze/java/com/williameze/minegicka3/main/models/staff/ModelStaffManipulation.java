package com.williameze.minegicka3.main.models.staff;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.api.models.CylinderConjunc;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.main.Values;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelStaffManipulation extends ModelStaff
{
    @Override
    public void addComponents()
    {
	components.add(Cylinder.create(new Vector(0, 5, 0), new Vector(0, -6, 0), 0.4685, 16).setColor(Values.yellow));
	components.add(new Sphere(0, -6.25, 0, 0.75, 2, 4).setColor(Values.purple));

	List<Vector> l = new ArrayList();
	l.add(new Vector(0, 9, -3));
	l.add(new Vector(0, 10.75, -3));
	l.add(new Vector(0, 11.75, -3));
	l.add(new Vector(0, 13.25, -2));
	l.add(new Vector(0, 14, 0));
	l.add(new Vector(0, 13, 2));
	l.add(new Vector(0, 11, 3));
	l.add(new Vector(0, 9.75, 2.25));
	l.add(new Vector(0, 9.25, 1));
	l.add(new Vector(0, 8, 0));
	l.add(new Vector(0, 7, 0));
	l.add(new Vector(0, 2, 0));
	components.add(new CylinderConjunc(0.5, 16, l).setColor(Values.purple));

	components.add(new Sphere(0, 6, 0, 0.5, 8, 16).setColor(Values.purple));
    }

}
