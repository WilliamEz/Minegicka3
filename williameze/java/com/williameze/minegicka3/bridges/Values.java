package com.williameze.minegicka3.bridges;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.Element;

public abstract class Values
{
    /** ResourceLocation **/
    public static ResourceLocationCustom elementsTexture = new ResourceLocationCustom("drawables/elements.png");

    /** Toggleable **/
    public static GuiPosition gui_Position = GuiPosition.BOTTOM_RIGHT;

    /** Values **/
    public static int clientTicked = 0;

    /** Enums **/
    public static enum GuiPosition
    {
	TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
    }

    /** Classes **/
    public static class ResourceLocationCustom extends ResourceLocation
    {
	public ResourceLocationCustom(String s)
	{
	    super(ModBase.MODID, "textures/" + s);
	}
    }
}
