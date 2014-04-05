package com.williameze.minegicka3.bridges;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;

public abstract class Values
{
    /** ResourceLocation **/
    public static ResourceLocationCustom elementsTexture = new ResourceLocationCustom("drawables/elements.png");

    /** Toggleable **/
    public static GuiPosition gui_Position = GuiPosition.BOTTOM_RIGHT;

    /** Values **/
    public static int clientTicked = 0;
    public static Map<World, Map<UUID, Entity>> worldEntitiesUUIDMap = new HashMap();

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
