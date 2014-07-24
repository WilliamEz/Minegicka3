package com.williameze.api.gui;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;

public class Panel
{
    public static Minecraft mc = Minecraft.getMinecraft();
    public static Tessellator tess = Tessellator.instance;
    public static Random rnd = new Random();

    /**
     * all values below are for scale factor = 1
     **/
    public double originX, originY;
    public double startX, startY;
    public double panelWidth, panelHeight;

    public IGuiHasScrollPanel parent;
    public boolean prevClicked;
    public boolean isMouseOn;

    public Panel(IGuiHasScrollPanel pare, double cx, double cy, double x, double y, double width, double height)
    {
	parent = pare;
	originX = cx;
	originY = cy;
	startX = x;
	startY = y;
	panelWidth = width;
	panelHeight = height;
    }

    public void objectClickedFeedback(ScrollObject obj)
    {
	parent.panelObjClickedFeedback(obj);
    }

    public void objectHoverFeedback(ScrollObject obj)
    {
	parent.panelObjHoverFeedback(obj);
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
	}
	else
	{
	    isMouseOn = false;
	}
    }

    public void drawPanel()
    {
	ScaledResolution scl = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
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
    }
}
