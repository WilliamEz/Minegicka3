package com.williameze.minegicka3.main.renders;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.models.ModelObject;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.models.ModelBase;
import com.williameze.minegicka3.main.objects.ItemEssence;
import com.williameze.minegicka3.main.objects.ItemStaff;

public class RenderItemGeneral implements IItemRenderer
{
    ModelBase model;

    public RenderItemGeneral(ModelObject obj)
    {
	this(new ModelBase(obj));
    }

    public RenderItemGeneral(ModelBase model)
    {
	this.model = model;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
	return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
	return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
	GL11.glPushMatrix();
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	if (type == ItemRenderType.ENTITY)
	{
	    GL11.glScaled(0.75,0.75,0.75);
	}
	if (type == ItemRenderType.INVENTORY)
	{
	    GL11.glRotated(45, 0, 1, 0);
	    GL11.glRotated(-15, 1, 0, 0);
	}
	if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
	{
	    GL11.glTranslated(0.25, 0.75, 0.5);
	}
	if (type == ItemRenderType.EQUIPPED)
	{
	    GL11.glTranslated(0.5, 0.5, 0.5);
	}

	GL11.glPushMatrix();
	GL11.glRotated(90, 1, 1, 0);
	RenderHelper.enableStandardItemLighting();
	GL11.glPopMatrix();
	model.render(item, 0);
	RenderHelper.enableStandardItemLighting();

	GL11.glColor4d(1, 1, 1, 1);
	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glPopMatrix();
    }
}
