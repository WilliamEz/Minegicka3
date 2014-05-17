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
public class PanelScrollList
{
    public static Minecraft mc = Minecraft.getMinecraft();
    public static Tessellator tess = Tessellator.instance;
    public static Random rnd = new Random();

    /** all values below are for scale factor = 1 **/
    /****/
    public double originX, originY;
    public double startX, startY;
    public double panelWidth, panelHeight;
    public double panelEndHeight;
    public double scrollHeight;
    public double scrollBarWidth;
    public double separatorThickness;

    public IGuiHasScrollPanel parent;
    public List<ScrollObject> objects = new ArrayList();
    public int selectedIndex;
    public Color separatorColor;
    public Color selectedObjectBackgroundColor;

    public boolean prevClicked;
    public boolean isMouseOn;

    public PanelScrollList(IGuiHasScrollPanel pare, double cx, double cy, double x, double y, double width, double height, double sbw)
    {
	parent = pare;
	selectedIndex = -1;
	originX = cx;
	originY = cy;
	startX = x;
	startY = y;
	panelWidth = width;
	panelEndHeight = panelHeight = height;
	scrollBarWidth = sbw;
	separatorThickness = 0.5;
    }

    public PanelScrollList setSeparatorColor(Color c)
    {
	separatorColor = c;
	return this;
    }

    public PanelScrollList setSelectedColor(Color c)
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
	parent.panelObjClickedFeedback(obj);
	selectedIndex = objects.indexOf(obj);
    }

    public void objectHoverFeedback(ScrollObject obj)
    {
	parent.panelObjHoverFeedback(obj);
    }

    public void onUpdate()
    {
	if (!Mouse.isButtonDown(0)) prevClicked = false;

	ScaledResolution scl = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
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

    public void drawPanel()
    {
	ScaledResolution scl = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	double scale = scl.getScaleFactor();
	GL11.glPushMatrix();
	GL11.glScissor((int) Math.round(originX + startX * scale),
		(int) Math.round(mc.displayHeight - (originY + panelHeight * scale + startY * scale)), (int) Math.ceil(panelWidth * scale),
		(int) Math.ceil(panelHeight * scale));
	GL11.glTranslated(originX, originY, 0);
	GL11.glScaled(scale, scale, scale);
	GL11.glTranslated(startX, startY, 0);
	drawLocalScaledBackground();
	GL11.glEnable(GL11.GL_SCISSOR_TEST);
	drawPanelLocalScaled();
	GL11.glDisable(GL11.GL_SCISSOR_TEST);
	drawLocalScaledForeground();
	GL11.glPopMatrix();
    }

    public void drawPanelLocalScaled()
    {
	drawLocalScaledObjects();
	if (isMouseOn && panelEndHeight > panelHeight) drawLocalScaledScrollBar();
    }

    public void drawLocalScaledBackground()
    {
	if (parent.drawPanelBackground(this))
	{
	    return;
	}
	GL11.glPushMatrix();
	DrawHelper.drawRect(0, 0, panelWidth, panelHeight, 0.2, 0.2, 0.2, 1);
	GL11.glPopMatrix();

    }

    public void drawLocalScaledForeground()
    {
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
		GL11.glTranslated(0, passedHeight-scrollHeight, 0);
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
