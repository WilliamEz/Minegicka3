package com.williameze.minegicka3.main.objects.items.renders;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.objects.items.ItemMagickTablet;
import com.williameze.minegicka3.main.objects.items.models.ModelItemMagickTablet;

public class RenderItemMagickTablet implements IItemRenderer
{
    ModelItemMagickTablet model = new ModelItemMagickTablet();

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
	model.addComponents();
	GL11.glPushMatrix();
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	if (type == ItemRenderType.ENTITY)
	{
	    //GL11.glTranslated(0, 1, 0);
	    GL11.glScaled(3, 3, 3);
	}
	if (type == ItemRenderType.INVENTORY)
	{
	    GL11.glScaled(3, 3, -3);
	    GL11.glTranslated(0, -0.18, 0);
	    GL11.glRotated(-45, 0, 1, 0);
	    GL11.glRotated(35, 1, 0, 0);
	    GL11.glScaled(1, 1, -1);
	}
	if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
	{
	    if (Minecraft.getMinecraft().thePlayer.getItemInUseCount() > 0)
	    {
		GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(-60.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(+80.0F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0F, -1F, -1.6F);
	    }
	    GL11.glRotated(15, -1, 0, 1);
	    GL11.glScaled(1, 1, -1);
	    GL11.glTranslated(3, -1.7, 0);
	    GL11.glScaled(6, 6, 6);
	    GL11.glRotated(10, 1, 0, -1);
	    GL11.glRotated(-120, 0, 1, 0);
	    GL11.glScaled(-1, 1, 1);
	}
	if (type == ItemRenderType.EQUIPPED)
	{
	    GL11.glTranslated(0, 1, 0);
	    GL11.glRotated(70, -1, 0, 1);
	    GL11.glTranslated(0, -1.2, 0);
	    GL11.glScaled(4, 4, 4);
	    GL11.glRotated(45, 0, 1, 0);
	}

	GL11.glScaled(1 / 20D, 1 / 20D, 1 / 20D);
	model.currentRendering = ((ItemMagickTablet)ModBase.magickTablet).getUnlocking(item); 
	model.render(null, 1);

	GL11.glColor4d(1, 1, 1, 1);
	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glPopMatrix();
    }
}
