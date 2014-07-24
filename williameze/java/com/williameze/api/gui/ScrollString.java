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
    public double fontSize;
    public boolean hasShadow;

    public ScrollString(PanelScrollVertical panel, String s, double occupyHeight)
    {
	super(panel, occupyHeight);
	display = s;
	stringColor = 0xffffff;
	fontSize = mc.fontRenderer.FONT_HEIGHT;
	hasShadow = true;
	scaleDownToFit = true;
    }

    @Override
    public void draw()
    {
	super.draw();
	double sheight = fontSize;
	double swidth = mc.fontRenderer.getStringWidth(display) * fontSize / mc.fontRenderer.FONT_HEIGHT;
	double scale = Math.min(scaleDownToFit ? (width - xMarginString * 2) / swidth : 1, 1);
	double realsheight = sheight * scale;
	double realswidth = swidth * scale;
	double startX = xMarginString;
	double startY = yMarginString;
	if (yCentered) startY = height / 2 - realsheight / 2;
	if (xCentered) startX = width / 2 - realswidth / 2;
	scale *= fontSize / mc.fontRenderer.FONT_HEIGHT;
	GL11.glPushMatrix();
	GL11.glTranslated(startX, startY, 0);
	GL11.glScaled(scale, scale, scale);
	if (hasShadow) mc.fontRenderer.drawStringWithShadow(display, 0, 0, stringColor);
	else mc.fontRenderer.drawString(display, 0, 0, stringColor);
	GL11.glPopMatrix();
    }
}
