package com.williameze.minegicka3.main.guis;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import com.williameze.api.gui.PanelScrollList;
import com.williameze.api.gui.ScrollItemStack;
import com.williameze.api.lib.DrawHelper;
import com.williameze.api.math.Vector;

public class ScrollClickCraftRecipeItemStack extends ScrollItemStack
{
    public int quantityNeed;
    public int quantityHave;

    public ScrollClickCraftRecipeItemStack(PanelScrollList panel, ItemStack is, double occupyHeight)
    {
	super(panel, is, occupyHeight, new Vector(18, occupyHeight / 2, 0), 16);
    }

    @Override
    public void draw()
    {
	super.draw();
	double size = Math.min(localObjectMaxX - localObjectMinX, localObjectMaxY - localObjectMinY);
	double stringHeight = mc.fontRenderer.FONT_HEIGHT;

	GL11.glPushMatrix();
	GL11.glTranslated((localObjectMinX + localObjectMaxX) / 2 + size / 2 + 6, (localObjectMinY + localObjectMaxY) / 2 - stringHeight
		/ 2, 0);
	mc.fontRenderer.drawStringWithShadow(quantityHave + "/" + quantityNeed, 0, 0, quantityHave >= quantityNeed ? 0x00ff00 : 0xffffff);
	GL11.glPopMatrix();
    }

}
