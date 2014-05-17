package com.williameze.minegicka3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.williameze.minegicka3.main.Element;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ModKeybinding extends KeyBinding
{
    public Element element;

    public static Map<Element, ModKeybinding> elementToKeyMap = new HashMap();
    public static ModKeybinding keyWater = new ModKeybinding("Water", Keyboard.KEY_Y, Element.Water);
    public static ModKeybinding keyLife = new ModKeybinding("Life", Keyboard.KEY_U, Element.Life);
    public static ModKeybinding keyShield = new ModKeybinding("Shield", Keyboard.KEY_I, Element.Shield);
    public static ModKeybinding keyCold = new ModKeybinding("Cold", Keyboard.KEY_O, Element.Cold);
    public static ModKeybinding keyLightning = new ModKeybinding("Lightning", Keyboard.KEY_H, Element.Lightning);
    public static ModKeybinding keyArcane = new ModKeybinding("Arcane", Keyboard.KEY_J, Element.Arcane);
    public static ModKeybinding keyEarth = new ModKeybinding("Earth", Keyboard.KEY_K, Element.Earth);
    public static ModKeybinding keyFire = new ModKeybinding("Fire", Keyboard.KEY_L, Element.Fire);
    public static List<ModKeybinding> elementKeys = Arrays.asList(keyWater, keyLife, keyShield, keyCold, keyLightning, keyArcane, keyEarth, keyFire);

    public static KeyBinding keyArea = new KeyBinding("Area Cast", Keyboard.KEY_F, "key.categories.gameplay");
    public static KeyBinding keyClear = new KeyBinding("Clear Queued", Keyboard.KEY_V, "key.categories.gameplay");
    public static KeyBinding keyMagick = new KeyBinding("Cast Magick", Keyboard.KEY_R, "key.categories.gameplay");

    public static void load()
    {
	for (ModKeybinding k : elementKeys)
	{
	    ClientRegistry.registerKeyBinding(k);
	}
	ClientRegistry.registerKeyBinding(keyArea);
	ClientRegistry.registerKeyBinding(keyClear);
	ClientRegistry.registerKeyBinding(keyMagick);
    }

    public ModKeybinding(String description, int id, Element e)
    {
	super(description, id, "key.categories." + ModBase.MODID);
	element = e;
	elementToKeyMap.put(e, this);
    }
}
