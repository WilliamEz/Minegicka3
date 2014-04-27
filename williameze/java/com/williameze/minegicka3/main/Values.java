package com.williameze.minegicka3.main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.williameze.minegicka3.ModBase;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class Values
{
    /** ResourceLocation **/
    public static ResourceLocationCustom elementsTexture = new ResourceLocationCustom("drawables/elements.png");

    /** Toggleable **/
    public static GuiPosition gui_Position = GuiPosition.BOTTOM_RIGHT;

    /** Values **/
    public static int clientTicked = 0;
    public static Map<World, Map<UUID, Entity>> worldEntitiesUUIDMap = new HashMap();
    public static double renderDistance = 32;
    public static double spellUpdateRange = 64;

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
