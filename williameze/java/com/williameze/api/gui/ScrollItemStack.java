package com.williameze.api.gui;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.math.Vector;

public class ScrollItemStack extends ScrollObject
{
    public ItemStack is;
    public int invIndex;

    public ScrollItemStack(PanelScrollVertical panel, ItemStack is, double h)
    {
	this(panel, is, h, panel.panelWidth, 16D);
    }

    public ScrollItemStack(PanelScrollHorizontal panel, ItemStack is, double w)
    {
	this(panel, is, panel.panelHeight, w, 16D);
    }

    public ScrollItemStack(Panel panel, ItemStack is, double h, double w)
    {
	this(panel, is, h, w, 16D);
    }

    public ScrollItemStack(Panel panel, ItemStack is, double h, double w, double stackDrawSize)
    {
	this(panel, is, h, w, true, true, stackDrawSize);
    }

    public ScrollItemStack(Panel panel, ItemStack is, double h, double w, boolean xCentered, boolean yCentered, double stackDrawSize)
    {
	this(panel, is, h, w, new Vector(xCentered ? w / 2 : stackDrawSize / 2, yCentered ? h / 2 : stackDrawSize / 2, 0), stackDrawSize);
    }

    public ScrollItemStack(Panel panel, ItemStack is, double h, double w, Vector stackCenter, double stackDrawSize)
    {
	this(panel, is, h, w, new Vector(stackCenter.x - stackDrawSize / 2, stackCenter.y - stackDrawSize / 2, stackCenter.z), new Vector(
		stackCenter.x + stackDrawSize / 2, stackCenter.y + stackDrawSize / 2, stackCenter.z));
    }

    public ScrollItemStack(Panel panel, ItemStack is, double h, double w, Vector stackDrawV1, Vector stackDrawV2)
    {
	super(panel, w, h);
	localObjectMinX = Math.min(stackDrawV1.x, stackDrawV2.x);
	localObjectMinY = Math.min(stackDrawV1.y, stackDrawV2.y);
	localObjectMaxX = Math.max(stackDrawV1.x, stackDrawV2.x);
	localObjectMaxY = Math.max(stackDrawV1.y, stackDrawV2.y);
	this.is = is;
    }

    @Override
    public void draw()
    {
	super.draw();
	double size = Math.min(localObjectMaxX - localObjectMinX, localObjectMaxY - localObjectMinY);
	double originalSize = 16D;
	double scale = size / originalSize;
	GL11.glPushMatrix();
	GL11.glTranslated((localObjectMinX + localObjectMaxX) / 2 - size / 2, (localObjectMinY + localObjectMaxY) / 2 - size / 2, 0);
	GL11.glScaled(scale, scale, scale);
	DrawHelper.drawItemStack(is, 0, 0, null);
	GL11.glPopMatrix();
    }
}
