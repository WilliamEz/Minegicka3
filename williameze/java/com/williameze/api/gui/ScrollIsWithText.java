package com.williameze.api.gui;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;

public class ScrollIsWithText extends ScrollItemStack
{
    public String text;
    public int color;
    public double fontSize;
    public double leftMargin;
    public double rightMargin;
    public double textImageSpace;
    public boolean scaleDownToFit;

    public ScrollIsWithText(Panel panel, ItemStack is, double h, double w, double isSize, double lm)
    {
	super(panel, is, h, w, new Vector(lm, h / 2, 0), isSize);
	fontSize = mc.fontRenderer.FONT_HEIGHT;
	leftMargin = lm;
	scaleDownToFit = true;
	color = 0xffffff;
    }

    public ScrollIsWithText(Panel panel, ItemStack is, double h, double w, double isSize)
    {
	super(panel, is, h, w, new Vector(18, h / 2, 0), isSize);
	fontSize = mc.fontRenderer.FONT_HEIGHT;
	leftMargin = 18;
	scaleDownToFit = true;
	color = 0xffffff;
    }

    @Override
    public void draw()
    {
	super.draw();
	double size = Math.min(localObjectMaxX - localObjectMinX, localObjectMaxY - localObjectMinY);
	double stringWidth = mc.fontRenderer.getStringWidth(text) * fontSize / mc.fontRenderer.FONT_HEIGHT;
	double widthLeft = width - leftMargin - textImageSpace - rightMargin - size;
	double scale = fontSize / mc.fontRenderer.FONT_HEIGHT * (scaleDownToFit ? Math.min(1, widthLeft / stringWidth) : 1);

	GL11.glPushMatrix();
	GL11.glTranslated(localObjectMaxX + textImageSpace, (localObjectMinY + localObjectMaxY) / 2 - mc.fontRenderer.FONT_HEIGHT * scale / 2, 0);
	GL11.glScaled(scale, scale, scale);
	mc.fontRenderer.drawStringWithShadow(text, 0, 0, color);
	GL11.glPopMatrix();
    }

}
