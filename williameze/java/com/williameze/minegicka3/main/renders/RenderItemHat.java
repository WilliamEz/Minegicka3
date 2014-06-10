package com.williameze.minegicka3.main.renders;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import com.williameze.minegicka3.main.models.ModelHat;
import com.williameze.minegicka3.main.objects.ItemHat;

public class RenderItemHat implements IItemRenderer
{
    public RenderItemHat()
    {
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

	if (type == ItemRenderType.EQUIPPED)
	{
	    GL11.glTranslated(0.25, 0.1, 0.25);
	}
	else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
	{
	    GL11.glTranslated(0, 0.5, 0);
	    GL11.glRotated(-90, 0, 1, 0);
	}
	else if (type == ItemRenderType.INVENTORY)
	{
	    GL11.glTranslated(0, -0.5, 0);
	    GL11.glScaled(0.9, 0.9, 0.9);
	}
	ModelHat.getModelHat(item).render(item, 0);
	GL11.glPopMatrix();
    }

}
