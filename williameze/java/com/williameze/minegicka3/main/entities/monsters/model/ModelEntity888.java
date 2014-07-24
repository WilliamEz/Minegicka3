package com.williameze.minegicka3.main.entities.monsters.model;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;
import com.williameze.api.models.Box;
import com.williameze.api.models.ModelBase;
import com.williameze.api.models.ModelObject;
import com.williameze.minegicka3.main.Values.ResourceLocationCustom;
import com.williameze.minegicka3.main.entities.monsters.Entity888;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEntity888 extends ModelBase
{
    public static ResourceLocation bodyTexture = new ResourceLocationCustom("entities/888.png");
    public static ResourceLocation eyesTexture = new ResourceLocationCustom("entities/888eyes.png");

    public Box eyes;

    @Override
    public void addComponents()
    {
	components.clear();

	Box body = Box.create(new Vector(0, 4, 0), 4);
	body.setTexture(bodyTexture);
	body.setTextureQuad(0, 0, 1, 1);
	components.add(body);

	eyes = Box.create(new Vector(0, 4, 0), 4);
	eyes.setTexture(eyesTexture);
	eyes.setTextureQuad(0, 0, 1, 1);
	components.add(eyes);
    }

    @Override
    public void componentPreRender(Object obj, float f, ModelObject o)
    {
	super.componentPreRender(obj, f, o);
	if (obj instanceof Entity888)
	{
	    if (o == eyes)
	    {
		EntityLivingBase e = Minecraft.getMinecraft().renderViewEntity;
		if (e != null)
		{
		    Vector diffVec = FuncHelper.getCenter((Entity) obj).subtract(new Vector(e.posX, e.posY + e.getEyeHeight(), e.posZ));
		    double dist = diffVec.lengthVector();

		    double yDiff = (e.posY + e.getEyeHeight()) - (((Entity888) obj).posY + 4);
		    double yTextureShift = yDiff / dist * 0.2;

		    Vector xzDiff = new Vector(diffVec.x, 0, diffVec.z);
		    if (xzDiff.isZeroVector()) xzDiff = new Vector(1, 0, 0);
		    double angle = Math.atan2(xzDiff.z, xzDiff.x);
		    double xTextureShift = angle / Math.PI / 2;

		    eyes.setTextureQuad(xTextureShift, yTextureShift, 1 + xTextureShift, 1 + yTextureShift);
		}
	    }
	}
    }

    @Override
    public void renderComponent(Object obj, float f, ModelObject o)
    {
	if (o == eyes && obj instanceof Entity888 && ((Entity888) obj).getAwaken() == false)
	{
	}
	else
	{
	    super.renderComponent(obj, f, o);
	}
    }
}
