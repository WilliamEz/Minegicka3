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
public class PanelScrollVertical extends Panel
{
    /**
     * all values below are for scale factor = 1
     **/
    public double panelEndHeight;
    public double scrollHeight;
    public double scrollBarWidth;
    public double separatorThickness;

    public List<ScrollObject> objects = new ArrayList();
    public int selectedIndex;
    public Color separatorColor;
    public Color selectedObjectBackgroundColor;

    public PanelScrollVertical(IGuiHasScrollPanel pare, double cx, double cy, double x, double y, double width, double height, double sbw)
    {
	super(pare, cx, cy, x, y, width, height);
	selectedIndex = -1;
	panelEndHeight = height;
	scrollBarWidth = sbw;
	separatorThickness = 0.5;
    }

    public PanelScrollVertical setSeparatorColor(Color c)
    {
	separatorColor = c;
	return this;
    }

    public PanelScrollVertical setSelectedColor(Color c)
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
	    h += sco.height;
	}
	panelEndHeight = Math.max(h, panelHeight);
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
	    if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) scrollChange = 10;
	    else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) scrollChange = -10;
	    scrollHeight = Math.max(0, Math.min(scrollHeight + scrollChange, panelEndHeight - panelHeight));

	    ScrollObject mouseOver = null;
	    double mouseScrolledY = localMY + scrollHeight;
	    double passedHeight = 0;
	    for (ScrollObject sco : objects)
	    {
		if (mouseScrolledY >= passedHeight && mouseScrolledY <= passedHeight + sco.height)
		{
		    mouseOver = sco;
		    break;
		}
		passedHeight += sco.height;
	    }
	    if (mouseOver != null)
	    {
		double mouseOverLocalY = mouseScrolledY - passedHeight;
		mouseOver.mouseOver(mx, my, localMX, mouseOverLocalY);
		if (!prevClicked && Mouse.isButtonDown(0) && Mouse.getEventButtonState())
		{
		    mouseOver.mouseClick(mx, my, localMX, mouseOverLocalY);
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
	if (isMouseOn && panelEndHeight > panelHeight) drawLocalScaledScrollBar();
    }

    public void drawLocalScaledObjects()
    {
	double passedHeight = 0;
	for (int a = 0; a < objects.size(); a++)
	{
	    ScrollObject obj = objects.get(a);
	    double minObjScrolledY = passedHeight;
	    double maxObjScrolledY = passedHeight + obj.height;
	    if (minObjScrolledY <= panelHeight || maxObjScrolledY >= 0)
	    {
		GL11.glPushMatrix();
		GL11.glTranslated(0, passedHeight - scrollHeight, 0);
		drawLocalScaledObject(obj);
		if (a < objects.size() - 1)
		{
		    GL11.glTranslated(0, obj.height, 0);
		    drawSeparator();
		}
		GL11.glPopMatrix();
	    }
	    passedHeight += obj.height;
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
	    DrawHelper.drawRect(scrollBarWidth + 3, -separatorThickness / 2, panelWidth - (scrollBarWidth + 3), separatorThickness / 2,
		    separatorColor.getRed() / 255D, separatorColor.getGreen() / 255D, separatorColor.getBlue() / 255D,
		    separatorColor.getAlpha() / 255D);
	    GL11.glPopMatrix();
	}
    }

    public void drawLocalScaledScrollBar()
    {
	GL11.glPushMatrix();
	GL11.glTranslated(0, 0, 101);
	double scrollBarH = panelHeight - 2;
	DrawHelper.drawRect(panelWidth - 1 - scrollBarWidth, 1, panelWidth - 1, 1 + scrollBarH, 0, 0, 0, 0.3);
	DrawHelper.drawRect(panelWidth - 1 - scrollBarWidth, 1 + scrollHeight / panelEndHeight * scrollBarH, panelWidth - 1, 1
		+ (scrollHeight + panelHeight) / panelEndHeight * scrollBarH, 0.9, 0.9, 0.9, 0.6);
	GL11.glPopMatrix();
    }

}
