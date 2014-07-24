package com.williameze.minegicka3.main.entities.magic.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.lib.NoiseGen2D;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.main.entities.magic.EntityIceShard;

public class RenderEntityIceShard extends Render
{

    public static List<Color> iceColors = new ArrayList();
    public static List<Sphere> pregeneratedIceModels = new ArrayList();

    public static void load()
    {
	pregeneratedIceModels.clear();

	iceColors.add(new Color(116, 255, 255));
	iceColors.add(new Color(178, 236, 237));
	iceColors.add(new Color(158, 250, 249));
	iceColors.add(new Color(53, 232, 255));
	iceColors.add(new Color(66, 255, 253));
	for (int a = 0; a < 8; a++)
	{
	    Sphere sp = new Sphere(0, 0, 0, 0.2, 24, 48);
	    sp.setColor(iceColors.get(new Random().nextInt(iceColors.size())));
	    NoiseGen2D noise = new NoiseGen2D(pregeneratedIceModels.hashCode(), sp.stacks, sp.slices);
	    noise.setCap(-0.1, 0.8);
	    noise.setNoisetainProps(sp.stacks / 200D, sp.stacks / 40D, (noise.maxCap - noise.minCap) / 2D);
	    noise.generate(16, true, 0);
	    // noise.mirrorOver(true, true);
	    sp.applyNoiseMap(noise);
	    pregeneratedIceModels.add(sp);
	}
    }

    protected void bindEntityTexture(Entity par1Entity)
    {
    }

    protected void bindTexture(ResourceLocation par1ResourceLocation)
    {
    }

    @Override
    public void doRender(Entity var1, double x, double y, double z, float yaw, float partialTick)
    {
	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glTranslated(x, y + var1.width / 2, z);
	DrawHelper.enableLighting(1.5F);

	double ticked = var1.ticksExisted + partialTick;
	double rate = ticked / ((EntityIceShard) var1).maxTick;
	double tip = 0.4;
	if (rate >= tip) rate = (1 - rate) / (1 - tip);
	else rate = rate / tip;
	double baseScale = 4;
	GL11.glScaled(baseScale * rate, baseScale * rate, baseScale * rate);
	pregeneratedIceModels.get(var1.hashCode() % pregeneratedIceModels.size()).render();

	DrawHelper.disableLighting();
	GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
