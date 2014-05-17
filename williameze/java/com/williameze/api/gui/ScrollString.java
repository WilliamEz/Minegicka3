package com.williameze.api.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;

public class ScrollString extends ScrollObject
{
    public String display;
    public double xMarginString, yMarginString;
    public boolean xCentered, yCentered;
    public boolean scaleDownToFit;
    public int stringColor;
    public boolean hasShadow;

    public ScrollString(PanelScrollList panel, String s, double occupyHeight)
    {
	super(panel, occupyHeight);
	display = s;
	stringColor = 0xffffff;
	hasShadow = true;
    }

    @Override
    public void draw()
    {
	super.draw();
	double sheight = mc.fontRenderer.FONT_HEIGHT;
	double swidth = mc.fontRenderer.getStringWidth(display);
	double scale = Math.min(scaleDownToFit ? (width - xMarginString * 2) / swidth : 1, 1);
	double realsheight = sheight * scale;
	double realswidth = swidth * scale;
	double startX = xMarginString;
	double startY = yMarginString;
	if (yCentered) startY = height / 2 - realsheight / 2;
	if (xCentered) startX = width / 2 - realswidth / 2;
	GL11.glPushMatrix();
	GL11.glTranslated(startX, startY, 0);
	GL11.glScaled(scale, scale, scale);
	if (hasShadow) mc.fontRenderer.drawStringWithShadow(display, 0, 0, stringColor);
	else mc.fontRenderer.drawString(display, 0, 0, stringColor);
	GL11.glPopMatrix();
    }
}
