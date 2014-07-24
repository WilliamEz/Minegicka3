package com.williameze.minegicka3.main.guis;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.williameze.api.gui.PanelScrollVertical;
import com.williameze.api.gui.ScrollIsWithText;
import com.williameze.api.gui.ScrollItemStack;
import com.williameze.api.math.Vector;

public class ScrollRecipeIS extends ScrollIsWithText
{
    public int quantityNeed;
    public int quantityHave;
    public int color2;

    public ScrollRecipeIS(PanelScrollVertical panel, ItemStack is, double h, double w, double isSize, double lm)
    {
	super(panel, is, h, w, isSize, lm);
	color2 = 0x00ff00;
    }

    public ScrollRecipeIS(PanelScrollVertical panel, ItemStack is, double h, double w, double isSize)
    {
	super(panel, is, h, w, isSize);
	color2 = 0x00ff00;
    }

    @Override
    public void draw()
    {
	color = quantityHave >= quantityNeed ? color2 : color;
	text = quantityHave + "/" + quantityNeed;
	super.draw();
    }

}
