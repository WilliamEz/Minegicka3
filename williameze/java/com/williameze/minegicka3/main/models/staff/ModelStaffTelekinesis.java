package com.williameze.minegicka3.main.models.staff;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import scala.util.Random;
import net.minecraft.item.ItemStack;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.api.models.DPRect;
import com.williameze.api.models.ModelObject;
import com.williameze.api.models.Ring;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.main.Values;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelStaffTelekinesis extends ModelStaff
{
    public Ring ring;

    @Override
    public void addComponents()
    {
	components.add(new Sphere(0, 7, 0, 0.4685, 2, 16).setColor(new Color(0, 200, 0)));
	components.add(Cylinder.create(new Vector(0, 7, 0), new Vector(0, -6, 0), 0.4685, 16).setColor(Values.yellow));
	components.add(new Sphere(0, -6.25, 0, 0.75, 2, 4).setColor(new Color(100, 100, 100)));

	components.add(new Sphere(0, 11, 0, 2, 2, 32).setColor(new Color(100, 100, 100)));
	ring = new Ring(new Vector(0, 11, 0), new Vector(0, 0, 1), 2.7, 3.2, 32);
	ring.setColor(new Color(0, 200, 0));
	ring.setPivot(new Vector(0, 11, 0));
	components.add(ring);
    }

    @Override
    public void componentPreRender(ItemStack staff, ModelObject o)
    {
	super.componentPreRender(staff, o);
	if (o == ring)
	{
	    o.setRotation(Values.clientTicked * 7D, 0);
	}
    }
}
