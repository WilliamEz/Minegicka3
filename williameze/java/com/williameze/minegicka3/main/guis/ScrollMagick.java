package com.williameze.minegicka3.main.guis;

import com.williameze.api.gui.PanelScrollVertical;
import com.williameze.api.gui.ScrollString;
import com.williameze.minegicka3.functional.PlayerData;
import com.williameze.minegicka3.mechanics.magicks.Magick;

public class ScrollMagick extends ScrollString
{
    public PlayerData playerData;
    public Magick magick;

    public ScrollMagick(PanelScrollVertical panel, PlayerData player, Magick m, double occupyHeight)
    {
	super(panel, m.getDisplayName(), occupyHeight);
	playerData = player;
	magick = m;
    }

    public boolean unlocked()
    {
	return playerData.isUnlocked(magick);
    }

    @Override
    public void draw()
    {
	if (unlocked())
	{
	    stringColor = 0xffffff;
	    hasShadow = true;
	}
	else
	{
	    stringColor = 0x404040;
	    hasShadow = false;
	}
	super.draw();
    }
}
