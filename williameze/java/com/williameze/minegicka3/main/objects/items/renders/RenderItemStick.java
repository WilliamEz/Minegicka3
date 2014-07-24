package com.williameze.minegicka3.main.objects.items.renders;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.api.models.ModelObject;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.objects.items.ItemEssence;
import com.williameze.minegicka3.mechanics.Element;

public class RenderItemStick implements IItemRenderer
{
    ModelObject cyl = Cylinder.create(Vector.root.copy(), new Vector(0, 7, 0), 0.4685, 16);

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
	return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
	if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) return false;
	return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
	{
	    GL11.glPushMatrix();
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	    if (type == ItemRenderType.ENTITY)
	    {
		GL11.glTranslated(0, 0, 0);
		GL11.glScaled(3, 3, 3);
	    }
	    if (type == ItemRenderType.INVENTORY)
	    {
		GL11.glScaled(3, 3, 3);
		GL11.glTranslated(-0.20, -0.25, 0);
		GL11.glRotated(45, 0, -1, 0);
		GL11.glRotated(30, 0, 0, 1);
		GL11.glRotated(45, -1, 0, 0);
	    }
	    if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
	    {
		GL11.glTranslated(0.45, 0.3, -0.25);
		GL11.glRotated(25, 0, 0, 1);
		GL11.glRotated(135, 0, 1, 0);
		GL11.glRotated(15, 1, 0, 0);
	    }
	    if (type == ItemRenderType.EQUIPPED)
	    {
		GL11.glTranslated(0, 1, 0);
		GL11.glRotated(70, -1, 0, 1);
		GL11.glTranslated(0, -1.65, 0);
		GL11.glScaled(4, 4, 4);
		GL11.glRotated(45, 0, 1, 0);
	    }

	    GL11.glScaled(1 / 16D, 1 / 16D, 1 / 16D);
	    if (item.getItem() == ModBase.stick) cyl.setColor(Values.yellow);
	    if (item.getItem() == ModBase.stickGood) cyl.setColor(Values.cyan);
	    if (item.getItem() == ModBase.stickSuper) cyl.setColor(Values.purple);
	    cyl.render();

	    GL11.glColor4d(1, 1, 1, 1);
	    GL11.glPopMatrix();
	}
    }
}
