package com.williameze.minegicka3.main.models;

import java.util.Random;

import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.NoiseGen2D;
import com.williameze.api.models.Sphere;

public class ModelEntityBoulder extends ModelBase
{
    public static Sphere boulder = new Sphere(0, 0, 0, 1, 8, 8);
    public static NoiseGen2D noise = new NoiseGen2D(new Random().nextLong(), 8 * 8 + 1, 8);
    static
    {
	noise.setCap(0, 20);
	noise.setNoisetainProps(0, 2, 8);
	noise.generate(8, true, 0);
	noise.smooth(1, 8);
    }

    @Override
    public void addComponents()
    {
	components.clear();
	components.add(boulder);
    }

    @Override
    public void doRenderParameters(Entity e, float f)
    {
	super.doRenderParameters(e, f);
	GL11.glScaled(e.width, e.height, e.width);
    }
}
