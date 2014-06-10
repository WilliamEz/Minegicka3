package com.williameze.minegicka3.main.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Box;
import com.williameze.api.models.ModelObject;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.main.Values;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCraftStation extends ModelBase
{
    Sphere mid;
    List<Sphere> spinnings;

    @Override
    public void addComponents()
    {
	components.clear();
	spinnings = new ArrayList();
	Color c1 = new Color(79, 19, 76);
	components.add(Box.create(0, 0, 0, 1, 0.1, 1).setColor(c1));
	components.add(Box.create(0.1, 0.1, 0.1, 0.9, 0.2, 0.9).setColor(c1));
	components.add(mid = (Sphere) new Sphere(0.5, 0.6, 0.5, 0.25, 2, 4).setColor(new Color(0, 255, 9)));
	for (int a = 0; a < 3; a++)
	{
	    Vector v = new Vector(0, 0, 0.3).rotateAround(Vector.unitY, Math.PI / 3D * a * 2D);
	    Sphere sp1 = (Sphere) new Sphere(0.5 + v.x, 0.8, 0.5 + v.z, 0.1, 2, 4).setColor(new Color(90, 204, 255));
	    Sphere sp2 = (Sphere) new Sphere(0.5 - v.x, 0.4, 0.5 - v.z, 0.1, 2, 4).setColor(new Color(255, 255, 0));
	    spinnings.add(sp1);
	    spinnings.add(sp2);
	}
	components.addAll(spinnings);
	components.add(Box.create(0, 0.1, 0, 1, 1, 1).setColor(new Color(255, 255, 255, 50)));
    }

    @Override
    public void componentPreRender(Object obj, float f, ModelObject o)
    {
	super.componentPreRender(obj, f, o);
	if (o == mid)
	{
	    GL11.glTranslated(mid.orgX, mid.orgY, mid.orgZ);
	    GL11.glRotated(-(Values.clientTicked + f) * 3, 0, 1, 0);
	    GL11.glTranslated(-mid.orgX, -mid.orgY, -mid.orgZ);
	}
	if (spinnings.contains(o))
	{
	    GL11.glTranslated(0.5, 0.5, 0.5);
	    GL11.glRotated((Values.clientTicked + f) * 3, 0, 1, 0);
	    GL11.glTranslated(-0.5, -0.5, -0.5);
	}
    }
}
