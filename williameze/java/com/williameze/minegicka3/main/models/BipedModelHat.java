package com.williameze.minegicka3.main.models;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class BipedModelHat extends ModelBiped
{
    public static Map<ModelHat, BipedModelHat> modelsMap = new HashMap();

    public static BipedModelHat getBipedModel(ModelHat model)
    {
	if (!modelsMap.containsKey(model))
	{
	    modelsMap.put(model, new BipedModelHat(model));
	}
	return modelsMap.get(model);
    }

    public ModelHat hat;

    private BipedModelHat(ModelHat hat)
    {
	this.hat = hat;
    }

    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
	this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
	hat.setRotationToMatch(bipedHead);

	GL11.glPushMatrix();
	GL11.glDisable(GL11.GL_CULL_FACE);
	if (this.isChild)
	{
	    float f6 = 2.0F;
	    GL11.glPushMatrix();
	    GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
	    GL11.glTranslatef(0.0F, 16.0F * par7, 0.0F);
	    GL11.glScaled(1, -1, 1);
	    hat.render(par1Entity, par7);
	    GL11.glPopMatrix();
	}
	else
	{
	    GL11.glPushMatrix();
	    //GL11.glTranslated(0, -0.5, 0);
	    GL11.glScaled(-1, -1, -1);
	    hat.render(par1Entity, par7);
	    GL11.glPopMatrix();
	}
	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glPopMatrix();
	hat.setRotationToMatch(null);
    }

}
