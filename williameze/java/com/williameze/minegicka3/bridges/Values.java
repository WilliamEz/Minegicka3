package com.williameze.minegicka3.bridges;

import net.minecraft.util.ResourceLocation;

import com.williameze.minegicka3.ModBase;

public abstract class Values
{
    /** ResourceLocation **/
    public static ResourceLocation gui_Main = new ResourceLocationCustom("drawables/gui_main.png");

    /** Toggleable **/
    public static GuiPosition gui_Position = GuiPosition.BOTTOM_RIGHT;

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
	    super(ModBase.PACKAGE, "textures/" + s);
	}
    }
}
