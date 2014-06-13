package com.williameze.minegicka3.main;

import java.awt.Color;
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

    /** Colors **/
    public static Color white = new Color(255, 255, 255);
    public static Color yellow = new Color(255, 255, 100);
    public static Color cyan = new Color(29, 177, 255);
    public static Color purple = new Color(197, 0, 204);
    public static Color red = new Color(200, 0, 0);

    /** Values **/
    public static int clientTicked = 0;
    public static double renderDistance = 32;
    public static double spellUpdateRange = 64;
    public static double magickUpdateRange = 64;
    public static double minManaToCastSpell = 20;
    public static int customRenderId = 10111996;

    /** Lists and maps **/
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

    public static void onWorldUnload(World world)
    {
	worldEntitiesUUIDMap.remove(world);
    }

}
