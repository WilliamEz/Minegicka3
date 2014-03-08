package com.williameze.minegicka3.core.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import com.williameze.minegicka3.objects.ItemStaff;

public class RenderStaff implements IItemRenderer
{

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
	if (item.getItem() instanceof ItemStaff)
	{
	    GL11.glPushMatrix();
	    RenderHelper.enableStandardItemLighting();
	    GL11.glDisable(GL11.GL_CULL_FACE);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
	    GL11.glEnable(GL11.GL_NORMALIZE);
	    if (type == ItemRenderType.ENTITY)
	    {
		GL11.glTranslated(0, 1, 0);
		GL11.glScaled(3, 3, 3);
	    }
	    if (type == ItemRenderType.INVENTORY)
	    {
		GL11.glScaled(1, 1, -1);
		GL11.glTranslated(-0.2, -0.3, 0);
		//GL11.glScaled(1.25, 1.25, 1.25);
		GL11.glRotated(60, 1, 0, 0);
		GL11.glRotated(45, 0, 1, 0);
	    }
	    if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
	    {
		GL11.glRotated(15, -1, 0, 1);
		GL11.glScaled(1, 1, -1);
		GL11.glTranslated(3, -2.5, 0);
		GL11.glScaled(6, 6, 6);
		GL11.glRotated(20, 1, 0, -1);
	    }
	    if (type == ItemRenderType.EQUIPPED)
	    {
		GL11.glTranslated(0, 1, 0);
		GL11.glRotated(70, -1, 0, 1);
		GL11.glTranslated(0, -1.65, 0);
		GL11.glScaled(4, 4, 4);
		GL11.glRotated(45, 0, 1, 0);
	    }

	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	    ((ItemStaff) item.getItem()).getModel(item).render(item);

	    GL11.glEnable(GL11.GL_CULL_FACE);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glPopMatrix();
	}
    }
}
