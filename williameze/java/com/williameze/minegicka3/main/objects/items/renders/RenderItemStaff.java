package com.williameze.minegicka3.main.objects.items.renders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.objects.items.Staff;
import com.williameze.minegicka3.main.objects.items.models.staff.ModelStaff;
import com.williameze.minegicka3.main.objects.items.models.staff.ModelStaffDefault;

public class RenderItemStaff implements IItemRenderer
{

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
	if (item.getItem() instanceof Staff && type != ItemRenderType.INVENTORY)
	{
	    // DrawHelper.enableLighting(1.8f);
	}
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
	if (item.getItem() instanceof Staff)
	{
	    GL11.glPushMatrix();
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	    if (type == ItemRenderType.ENTITY)
	    {
		GL11.glTranslated(0, 1, 0);
		GL11.glScaled(3, 3, 3);
	    }
	    if (type == ItemRenderType.INVENTORY)
	    {
		GL11.glScaled(1.2, 1.2, 1.2);
		GL11.glTranslated(-0.25, -0.35, 0);
		GL11.glRotated(45, 0, -1, 0);
		GL11.glRotated(30, 0, 0, 1);
		GL11.glRotated(45, -1, 0, 0);
	    }
	    if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
	    {
		if (Minecraft.getMinecraft().thePlayer.getItemInUseCount() > 0)
		{
		    GL11.glTranslated(0.7, -0.35, -0.35);
		    GL11.glRotated(45, 0, 0, 1);
		    GL11.glRotated(-50, 0, 1, 0);
		}
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
		GL11.glRotated(-90, 0, 1, 0);
	    }

	    ModelStaff.getModel(item).render(item);

	    if (type != ItemRenderType.INVENTORY)
	    {
		// DrawHelper.disableLighting();
	    }

	    GL11.glColor4d(1, 1, 1, 1);
	    GL11.glPopMatrix();
	}
    }
}
