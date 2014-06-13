package com.williameze.minegicka3.main.models.hat;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.models.ModelBase;
import com.williameze.minegicka3.main.objects.items.ItemHat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHat extends ModelBase
{
    public static ModelHatDefault defaultHatModel = new ModelHatDefault();
    public static Map<ItemHat, ModelHat> hatModels = new HashMap();

    public static void load()
    {
	hatModels.put((ItemHat) ModBase.hatImmunity, new ModelHatImmunity());
	hatModels.put((ItemHat) ModBase.hatRisk, new ModelHatRisk());
    }

    public static ModelHat getModelHat(ItemStack is)
    {
	if (is.getItem() instanceof ItemHat)
	{
	    ModelHat model = hatModels.get(is.getItem());
	    if (model != null) return model;
	}
	return defaultHatModel;
    }

    public double rotationPointX, rotationPointY, rotationPointZ;
    public double rotationX, rotationY, rotationZ;

    public void setRotationToMatch(ModelRenderer model)
    {
	if (model == null)
	{
	    rotationPointX = rotationPointY = rotationPointZ = rotationX = rotationY = rotationZ = 0;
	}
	else
	{
	    rotationPointX = model.rotationPointX;
	    rotationPointY = model.rotationPointY;
	    rotationPointZ = model.rotationPointZ;
	    rotationX = model.rotateAngleX * 180D / Math.PI;
	    rotationY = model.rotateAngleY * 180D / Math.PI;
	    rotationZ = model.rotateAngleZ * 180D / Math.PI;
	}
    }

    @Override
    public void preRender(Object obj, float f)
    {
	super.preRender(obj, f);
	GL11.glTranslated(rotationPointX, rotationPointY, rotationPointZ);
	GL11.glRotated(rotationZ, 0, 0, 1);
	GL11.glRotated(rotationY, 0, 1, 0);
	GL11.glRotated(rotationX, 1, 0, 0);
	GL11.glTranslated(-rotationPointX, -rotationPointY, -rotationPointZ);
    }
}
