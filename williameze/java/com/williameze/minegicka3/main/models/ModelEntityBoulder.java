package com.williameze.minegicka3.main.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.NoiseGen2D;
import com.williameze.api.models.Sphere;

public class ModelEntityBoulder extends ModelBase
{
    public static List<Color> colors = new ArrayList();
    public static List<Sphere> pregeneratedModels = new ArrayList();

    public static void load()
    {
	colors.add(new Color(122, 100, 60));
	colors.add(new Color(94, 69, 23));
	colors.add(new Color(123, 115, 101));
	colors.add(new Color(71, 65, 53));
	colors.add(new Color(122, 100, 60));

	for (int a = 0; a < 8; a++)
	{
	    Sphere sp = new Sphere(0, 0, 0, 1, 12, 24);
	    sp.setColor(colors.get(new Random().nextInt(colors.size())));
	    NoiseGen2D noise = new NoiseGen2D(pregeneratedModels.hashCode(), 12, 24);
	    noise.setCap(-0.6, 0.6);
	    noise.setNoisetainProps(0.1, 1.3, 0.6);
	    noise.generate(16, true, 0);
	    noise.smooth(0.3, 1);
	    noise.mirrorOver(true, true);
	    sp.setNoiseMap(noise);
	    pregeneratedModels.add(sp);
	}
    }

    @Override
    public void doRenderParameters(Entity e, float f)
    {
	super.doRenderParameters(e, f);
	GL11.glScaled(e.width, e.height, e.width);
    }

    @Override
    public void doRenderComponents(Entity e, float f)
    {
	if (!pregeneratedModels.isEmpty())
	{
	    Sphere s = pregeneratedModels.get(e.hashCode() % pregeneratedModels.size());
	    renderComponent(e, f, s);
	}
    }
}
