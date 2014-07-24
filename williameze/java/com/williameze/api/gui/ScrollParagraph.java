package com.williameze.api.gui;

import java.util.ArrayList;
import java.util.List;

public class ScrollParagraph extends ScrollString
{
    public List<String> lines;

    public ScrollParagraph(PanelScrollVertical panel, double xMargin, double yMargin, List<String> strings)
    {
	super(panel, "", 1);
	xMarginString = xMargin;
	yMarginString = yMargin;
	lines = formatParagraph(strings);
	height = lines.size() * 8 + Math.max(0, lines.size() - 1) * 4 + yMarginString * 2;
    }

    public List<String> formatParagraph(List<String> strings)
    {
	double maxLineWidth = width - 2 * xMarginString;
	List<String> paragraph = new ArrayList();
	for (int a = 0; a < strings.size(); a++)
	{
	    String[] words = strings.get(a).split(" ");
	    String aLine = "";
	    for (int b = 0; b < words.length; b++)
	    {
		String word = words[b];
		if (mc.fontRenderer.getStringWidth(aLine + " " + word) > maxLineWidth)
		{
		    paragraph.add(aLine);
		    aLine = "";
		}
		aLine += " " + word;
		if (b == words.length - 1)
		{
		    paragraph.add(aLine);
		}
	    }
	    if (words.length == 0)
	    {
		paragraph.add("");
	    }
	}
	return paragraph;
    }

    @Override
    public void draw()
    {
	for (int a = 0; a < lines.size(); a++)
	{
	    String line = lines.get(a);
	    if (hasShadow)
	    {
		mc.fontRenderer.drawStringWithShadow(line, (int) xMarginString, (int) (yMarginString + a * 8 + a * 4), stringColor);
	    }
	    else
	    {
		mc.fontRenderer.drawString(line, (int) xMarginString, (int) (yMarginString + a * 8 + a * 4), stringColor);
	    }
	}
    }
}
