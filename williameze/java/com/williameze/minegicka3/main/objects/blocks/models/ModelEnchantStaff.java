package com.williameze.minegicka3.main.objects.blocks.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Box;
import com.williameze.api.models.ModelBase;
import com.williameze.api.models.ModelObject;
import com.williameze.api.models.Ring;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.main.Values;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEnchantStaff extends ModelBase
{
    Sphere mid;
    Ring spin1, spin2, spin3;

    @Override
    public void addComponents()
    {
	components.clear();
	Color c1 = new Color(79, 19, 76);
	components.add(Box.create(0, 0, 0, 1, 0.1, 1).setColor(c1));
	components.add(Box.create(0.1, 0.1, 0.1, 0.9, 0.2, 0.9).setColor(c1));

	Vector center = new Vector(0.5, 0.6, 0.5);
	components.add(mid = (Sphere) new Sphere(0.5, 0.6, 0.5, 0.2, 2, 4).setPivot(center).setColor(Values.purple));
	components.add(spin1 = (Ring) new Ring(center, Vector.unitX, 0.3, 0.35, 32).setColor(Values.yellow));
	components.add(spin2 = (Ring) new Ring(center, Vector.unitY, 0.3, 0.35, 32).setColor(Values.yellow));
	components.add(spin3 = (Ring) new Ring(center, new Vector(1, 1, 0), 0.3, 0.35, 32).setColor(Values.yellow));

	//components.add(Box.create(0, 0.1, 0, 1, 1, 1).setColor(new Color(255, 255, 255, 50)));
    }

    @Override
    public void componentPreRender(Object obj, float f, ModelObject o)
    {
	super.componentPreRender(obj, f, o);
	if (o == mid)
	{
	    mid.setRotation((Values.clientTicked + f) * 2, 0);
	}
	if (o == spin1)
	{
	    spin1.setRotation((Values.clientTicked + f) * 8, 0);
	}
	if (o == spin2)
	{
	    spin2.setRotation(0, (Values.clientTicked + f) * 8);
	}
	if (o == spin3)
	{
	    spin3.setRotation(-(Values.clientTicked + f) * 8, -(Values.clientTicked + f) * 8);
	}
    }
}
