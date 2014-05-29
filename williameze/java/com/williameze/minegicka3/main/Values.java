package com.williameze.minegicka3.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;
import com.williameze.api.math.IntVector;
import com.williameze.minegicka3.ModBase;

public abstract class Values
{
    /** ResourceLocation **/
    public static ResourceLocationCustom elementsTexture = new ResourceLocationCustom("drawables/elements.png");

    /** Toggleable **/
    public static GuiPosition gui_Position = GuiPosition.BOTTOM_RIGHT;

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

    public static void onWorldUnload()
    {
	worldEntitiesUUIDMap.clear();
    }
}
