package com.williameze.minegicka3.main.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Box;
import com.williameze.minegicka3.main.entities.EntitySpray;

public class ModelEntitySpray extends ModelBase
{
    public static Box box = new Box(new Vector(0, 0, 0), 1);

    @Override
    public void addComponents()
    {
	components.clear();
	components.add(box);
    }

    @Override
    public void doRenderParameters(Entity e, float f)
    {
	super.doRenderParameters(e, f);
	GL11.glScaled(e.width, e.height, e.width);
	box.setColor(((EntitySpray) e).color);
    }
}
