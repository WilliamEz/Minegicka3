package com.williameze.minegicka3.main.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Occurs;
import com.williameze.api.gui.PanelScrollVertical;
import com.williameze.api.gui.ScrollObject;
import com.williameze.api.lib.DrawHelper;
import com.williameze.minegicka3.functional.PlayerData;
import com.williameze.minegicka3.mechanics.Element;

public class ScrollElements extends ScrollObject
{
    public List<Element> elements = new ArrayList();
    public PlayerData playerData;
    public boolean checkElementsUnlocked;
    public double elementSize;
    public boolean xCentered;
    public boolean yCentered;

    public ScrollElements(PanelScrollVertical panel, double occupyHeight, Element... es)
    {
	super(panel, occupyHeight);
	elements = Arrays.asList(es);
	xCentered = true;
	yCentered = true;
	elementSize = 20;
    }

    @Override
    public void draw()
    {
	super.draw();
	for (int a = 0; a < elements.size(); a++)
	{
	    Element e = elements.get(a);
	    double startX = elementSize * a + (xCentered ? (width - elements.size() * elementSize) / 2D : 0);
	    double startY = yCentered ? (height - elementSize) / 2 : 0;
	    DrawHelper.drawElementIcon(mc.renderEngine, e, checkElementsUnlocked && playerData != null && !playerData.isUnlocked(e) ? true : false,
		    startX, startY, elementSize, elementSize, 0.95);
	}
    }

}
