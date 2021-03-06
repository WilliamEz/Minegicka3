package com.williameze.api.gui;

import java.awt.Color;
import java.util.Random;

import com.williameze.api.lib.DrawHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

public class ScrollObject
{
    public static Minecraft mc = Minecraft.getMinecraft();
    public static Tessellator tess = Tessellator.instance;
    public static Random rnd = new Random();

    public Panel parent;
    public double width, height;
    public double localObjectMinX, localObjectMinY, localObjectMaxX, localObjectMaxY;
    public double xMarginBG, yMarginBG;

    public ScrollObject(Panel panel, double w, double h)
    {
	parent = panel;
	width = w;
	height = h;
	localObjectMinX = localObjectMinY = 0;
	localObjectMaxX = w;
	localObjectMaxY = h;
    }

    public ScrollObject(PanelScrollVertical panel, double occupyHeight)
    {
	parent = panel;
	height = occupyHeight;
	width = parent.panelWidth;
	localObjectMinX = localObjectMinY = 0;
	localObjectMaxX = width;
	localObjectMaxY = height;
    }

    public ScrollObject(PanelScrollHorizontal panel, double occupyWidth)
    {
	parent = panel;
	width = occupyWidth;
	height = parent.panelHeight;
	localObjectMinX = localObjectMinY = 0;
	localObjectMaxX = width;
	localObjectMaxY = height;
    }

    public boolean isMouseOnMainPart(double localMX, double localMY)
    {
	return localMX >= localObjectMinX && localMX <= localObjectMaxX && localMY >= localObjectMinY && localMY <= localObjectMaxY;
    }

    public void mouseOver(double mx, double my, double localMX, double localMY)
    {
	if (isMouseOnMainPart(localMX, localMY)) parent.objectHoverFeedback(this);
    }

    public void mouseClick(double mx, double my, double localMX, double localMY)
    {
	if (isMouseOnMainPart(localMX, localMY)) parent.objectClickedFeedback(this);
    }

    public void drawSelectedBackground(Color c)
    {
	DrawHelper.drawRect(xMarginBG, yMarginBG, width - xMarginBG, height - yMarginBG, c.getRed() / 255D, c.getGreen() / 255D, c.getBlue() / 255D,
		c.getAlpha() / 255D);
    }

    public void draw()
    {

    }
}
