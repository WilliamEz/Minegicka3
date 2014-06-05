package com.williameze.minegicka3.main.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.NoiseGen2D;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.main.entities.EntityBoulder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEntityBoulder extends ModelBase
{
    public static List<Color> colors = new ArrayList();
    public static List<Color> iceColors = new ArrayList();
    public static List<Sphere> pregeneratedModels = new ArrayList();
    public static List<Sphere> pregeneratedIceModels = new ArrayList();

    public static void load()
    {
	pregeneratedModels.clear();
	pregeneratedIceModels.clear();

	colors.add(new Color(122, 100, 60));
	colors.add(new Color(94, 69, 23));
	colors.add(new Color(123, 115, 101));
	colors.add(new Color(71, 65, 53));
	colors.add(new Color(122, 100, 60));
	for (int a = 0; a < 8; a++)
	{
	    Sphere sp = new Sphere(0, 0, 0, 0.5, 24, 48);
	    sp.setColor(colors.get(new Random().nextInt(colors.size())));
	    NoiseGen2D noise = new NoiseGen2D(pregeneratedModels.hashCode(), sp.stacks, sp.slices);
	    noise.setCap(-0.4, 0.4);
	    noise.setNoisetainProps(sp.stacks / 100D, sp.stacks / 10D, (noise.maxCap - noise.minCap) / 2D);
	    noise.generate(16, true, 0);
	    noise.smooth(0.3, 2);
	    noise.mirrorOver(true, true);
	    noise.mirrorOver(false, true);
	    sp.applyNoiseMap(noise);
	    pregeneratedModels.add(sp);
	}

	iceColors.add(new Color(116, 255, 255));
	iceColors.add(new Color(178, 236, 237));
	iceColors.add(new Color(158, 250, 249));
	iceColors.add(new Color(53, 232, 255));
	iceColors.add(new Color(66, 255, 253));
	for (int a = 0; a < 8; a++)
	{
	    Sphere sp = new Sphere(0, 0, 0, 0.5, 24, 48);
	    sp.setColor(iceColors.get(new Random().nextInt(iceColors.size())));
	    NoiseGen2D noise = new NoiseGen2D(pregeneratedIceModels.hashCode(), sp.stacks, sp.slices);
	    noise.setCap(-0.4, 0.4);
	    noise.setNoisetainProps(sp.stacks / 100D, sp.stacks / 10D, (noise.maxCap - noise.minCap) / 2D);
	    noise.generate(16, true, 0);
	    noise.smooth(0.3, 2);
	    // noise.mirrorOver(true, true);
	    sp.applyNoiseMap(noise);
	    pregeneratedIceModels.add(sp);
	}
    }

    @Override
    public void doRenderParameters(Object o, float f)
    {
	// load();
	super.doRenderParameters(o, f);
	if (o instanceof EntityBoulder)
	{
	    Entity e = (Entity) o;
	    GL11.glScaled(e.width, e.height, e.width);
	}
    }

    @Override
    public void doRenderComponents(Object o, float f)
    {
	if (o instanceof EntityBoulder)
	{
	    Entity e = (Entity) o;
	    if (!pregeneratedModels.isEmpty())
	    {
		Sphere s = pregeneratedModels.get(e.hashCode() % pregeneratedModels.size());

		if (e instanceof EntityBoulder && ((EntityBoulder) e).isIce())
		{
		    s = pregeneratedIceModels.get(e.hashCode() % pregeneratedIceModels.size());
		}
		renderComponent(e, f, s);
	    }
	}
    }
}
