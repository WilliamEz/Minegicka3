package com.williameze.api.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.java.games.input.Component.Identifier.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;

/** A panel that displays and interacts with the gui having it, center-based **/
public class PanelScrollHorizontal extends Panel
{
    /**
     * all values below are for scale factor = 1
     **/
    public double panelEndWidth;
    public double scrollPosition;
    public double scrollBarHeight;
    public double separatorThickness;

    public List<ScrollObject> objects = new ArrayList();
    public int selectedIndex;
    public Color separatorColor;
    public Color selectedObjectBackgroundColor;

    public PanelScrollHorizontal(IGuiHasScrollPanel pare, double cx, double cy, double x, double y, double width, double height, double sbw)
    {
	super(pare, cx, cy, x, y, width, height);
	selectedIndex = -1;
	panelEndWidth = width;
	scrollBarHeight = sbw;
	separatorThickness = 0.5;
    }

    public PanelScrollHorizontal setSeparatorColor(Color c)
    {
	separatorColor = c;
	return this;
    }

    public PanelScrollHorizontal setSelectedColor(Color c)
    {
	selectedObjectBackgroundColor = c;
	return this;
    }

    public boolean isObjectOfPanel(ScrollObject obj)
    {
	return objects.contains(obj);
    }

    public void addObject(ScrollObject obj)
    {
	if (obj == null) return;
	objects.add(obj);
	listUpdated();
    }

    public void clearObjects()
    {
	objects.clear();
	selectedIndex = -1;
	listUpdated();
    }

    public void listUpdated()
    {
	double h = 0;
	for (ScrollObject sco : objects)
	{
	    h += sco.width;
	}
	panelEndWidth = Math.max(h, panelWidth);
    }

    public void objectClickedFeedback(ScrollObject obj)
    {
	selectedIndex = objects.indexOf(obj);
	super.objectClickedFeedback(obj);
    }

    public void onUpdate()
    {
	if (!Mouse.isButtonDown(0)) prevClicked = false;

	ScaledResolution scl = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
	double mx = Mouse.getX();
	double my = mc.displayHeight - Mouse.getY();
	double abstractMX = (mx - originX) / (double) scl.getScaleFactor() + originX;
	double abstractMY = (my - originY) / (double) scl.getScaleFactor() + originY;
	double localMX = abstractMX - originX - startX;
	double localMY = abstractMY - originY - startY;
	if (localMX >= 0 && localMX <= panelWidth && localMY >= 0 && localMY <= panelHeight)
	{
	    isMouseOn = true;
	    double scrollChange = -Mouse.getDWheel() / 12D;
	    if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) scrollChange += 10;
	    else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) scrollChange += -10;
	    scrollPosition = Math.max(0, Math.min(scrollPosition + scrollChange, panelEndWidth - panelWidth));

	    ScrollObject mouseOver = null;
	    double mouseScrolledX = localMX + scrollPosition;
	    double passedWidth = 0;
	    for (ScrollObject sco : objects)
	    {
		if (mouseScrolledX >= passedWidth && mouseScrolledX <= passedWidth + sco.width)
		{
		    mouseOver = sco;
		    break;
		}
		passedWidth += sco.width;
	    }
	    if (mouseOver != null)
	    {
		double mouseOverLocalX = mouseScrolledX - passedWidth;
		mouseOver.mouseOver(mx, my, mouseOverLocalX, localMY);
		if (!prevClicked && Mouse.isButtonDown(0) && Mouse.getEventButtonState())
		{
		    mouseOver.mouseClick(mx, my, mouseOverLocalX, localMY);
		    prevClicked = true;
		}
	    }
	}
	else
	{
	    isMouseOn = false;
	}
    }

    public void drawPanelLocalScaled()
    {
	drawLocalScaledObjects();
	if (isMouseOn && panelEndWidth > panelWidth) drawLocalScaledScrollBar();
    }

    public void drawLocalScaledObjects()
    {
	double passedWidth = 0;
	for (int a = 0; a < objects.size(); a++)
	{
	    ScrollObject obj = objects.get(a);
	    double minObjScrolledX = passedWidth;
	    double maxObjScrolledX = passedWidth + obj.width;
	    if (minObjScrolledX <= panelWidth || maxObjScrolledX >= 0)
	    {
		GL11.glPushMatrix();
		GL11.glTranslated(passedWidth - scrollPosition, 0, 0);
		drawLocalScaledObject(obj);
		if (a < objects.size() - 1)
		{
		    GL11.glTranslated(obj.width, 0, 0);
		    drawSeparator();
		}
		GL11.glPopMatrix();
	    }
	    passedWidth += obj.width;
	}
    }

    public void drawLocalScaledObject(ScrollObject toDraw)
    {
	if (selectedObjectBackgroundColor != null && selectedIndex == objects.indexOf(toDraw)) toDraw
		.drawSelectedBackground(selectedObjectBackgroundColor);
	toDraw.draw();
    }

    public void drawSeparator()
    {
	if (separatorColor != null)
	{
	    GL11.glPushMatrix();
	    GL11.glTranslated(0, 0, 100);
	    DrawHelper.drawRect(-separatorThickness / 2, scrollBarHeight + 3, separatorThickness / 2, panelHeight - (scrollBarHeight + 3),
		    separatorColor.getRed() / 255D, separatorColor.getGreen() / 255D, separatorColor.getBlue() / 255D,
		    separatorColor.getAlpha() / 255D);
	    GL11.glPopMatrix();
	}
    }

    public void drawLocalScaledScrollBar()
    {
	GL11.glPushMatrix();
	GL11.glTranslated(0, 0, 101);
	double scrollBarW = panelWidth - 2;
	DrawHelper.drawRect(1, panelHeight - 1 - scrollBarHeight, 1 + scrollBarW, panelHeight - 1, 0, 0, 0, 0.3);
	DrawHelper.drawRect(1 + scrollPosition / panelEndWidth * scrollBarW, panelHeight - 1 - scrollBarHeight, 1 + (scrollPosition + panelWidth)
		/ panelEndWidth * scrollBarW, panelHeight - 1, 0.9, 0.9, 0.9, 0.6);
	GL11.glPopMatrix();
    }

}
