package com.williameze.minegicka3.main.models;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Box;
import com.williameze.api.models.Cylinder;
import com.williameze.minegicka3.core.PlayersData;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.magicks.Magick;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelItemMagickTablet extends ModelBase
{
    public Magick currentRendering;
    public Box box = Box.create(Vector.root.copy(), 0.3);

    @Override
    public void addComponents()
    {
	Color c = new Color(255, 255, 100, 255);
	components.clear();
	components.add(Box.create(-2.5, 0, -0.3, 2.5, 6, 0.1).setColor(0x555555, 255));
	components.add(Cylinder.create(new Vector(-2.5, 0.2, 0), new Vector(-2.5, 5.8, 0), 0.4, 6).setColor(c));
	components.add(Cylinder.create(new Vector(2.5, 0.2, 0), new Vector(2.5, 5.8, 0), 0.4, 6).setColor(c));
	components.add(Cylinder.create(new Vector(-2.88, 0, 0), new Vector(2.88, 0, 0), 0.4, 6).setColor(c));
	components.add(Cylinder.create(new Vector(-2.88, 6, 0), new Vector(2.88, 6, 0), 0.4, 6).setColor(c));
    }

    @Override
    public void doRenderComponents(Object e, float f)
    {
	super.doRenderComponents(e, f);
	if (currentRendering != null)
	{
	    Element[] es = currentRendering.getCombination();
	    if (es != null)
	    {
		for (int a = 0; a < es.length; a++)
		{
		    int xindex = a % 3;
		    int yindex = (int) Math.floor(a / 3);
		    Element el = es[a];

		    double translateX = 1.3D * (xindex - 1);
		    double translateY = Math.max(5 - yindex * 2D, 0.8);
		    double translateZ = 0.1;
		    GL11.glPushMatrix();
		    GL11.glTranslated(translateX, translateY, translateZ);
		    if (PlayersData.clientPlayerData.isUnlocked(el)) box.setColor(el.getColor());
		    else box.setColor(0x000000, 255);
		    box.render();
		    GL11.glPopMatrix();
		}
	    }
	}
    }
}
