package com.williameze.minegicka3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.williameze.api.TestOverlay;
import com.williameze.minegicka3.main.Element;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ModKeybinding
{
    public static Map<Element, KeyBinding> elementToKeyMap = new HashMap();
    public static Map<KeyBinding, Element> keyToElementMap = new HashMap();
    public static List<KeyBinding> elementKeys = new ArrayList();
    
    public static KeyBinding keyWater = new KeyBinding("Spell Water", Keyboard.KEY_Y, "key.categories." + ModBase.MODID);
    public static KeyBinding keyLife = new KeyBinding("Spell Life", Keyboard.KEY_U, "key.categories." + ModBase.MODID);
    public static KeyBinding keyShield = new KeyBinding("Spell Shield", Keyboard.KEY_I, "key.categories." + ModBase.MODID);
    public static KeyBinding keyCold = new KeyBinding("Spell Cold", Keyboard.KEY_O, "key.categories." + ModBase.MODID);
    public static KeyBinding keyLightning = new KeyBinding("Spell Lightning", Keyboard.KEY_H, "key.categories." + ModBase.MODID);
    public static KeyBinding keyArcane = new KeyBinding("Spell Arcane", Keyboard.KEY_J, "key.categories." + ModBase.MODID);
    public static KeyBinding keyEarth = new KeyBinding("Spell Earth", Keyboard.KEY_K, "key.categories." + ModBase.MODID);
    public static KeyBinding keyFire = new KeyBinding("Spell Fire", Keyboard.KEY_L, "key.categories." + ModBase.MODID);
    public static KeyBinding keyArea = new KeyBinding("Toggle Area spell", Keyboard.KEY_F, "key.categories." + ModBase.MODID);
    public static KeyBinding keyClear = new KeyBinding("Clear Queued", Keyboard.KEY_V, "key.categories." + ModBase.MODID);
    public static KeyBinding keyMagick = new KeyBinding("Cast Magick", Keyboard.KEY_R, "key.categories." + ModBase.MODID);
     
    public static void load()
    {
	elementKeys.add(keyArcane);
	elementKeys.add(keyCold);
	elementKeys.add(keyEarth);
	elementKeys.add(keyFire);
	elementKeys.add(keyLife);
	elementKeys.add(keyLightning);
	elementKeys.add(keyShield);
	elementKeys.add(keyWater);
	addKeyElement(keyArcane, Element.Arcane);
	addKeyElement(keyCold, Element.Cold);
	addKeyElement(keyEarth, Element.Earth);
	addKeyElement(keyFire, Element.Fire);
	addKeyElement(keyLife, Element.Life);
	addKeyElement(keyLightning, Element.Lightning);
	addKeyElement(keyShield, Element.Shield);
	addKeyElement(keyWater, Element.Water);
	
	ClientRegistry.registerKeyBinding(keyWater);
	ClientRegistry.registerKeyBinding(keyLife);
	ClientRegistry.registerKeyBinding(keyShield);
	ClientRegistry.registerKeyBinding(keyCold);
	ClientRegistry.registerKeyBinding(keyLightning);
	ClientRegistry.registerKeyBinding(keyArcane);
	ClientRegistry.registerKeyBinding(keyEarth);
	ClientRegistry.registerKeyBinding(keyFire);
	ClientRegistry.registerKeyBinding(keyArea);
	ClientRegistry.registerKeyBinding(keyClear);
	ClientRegistry.registerKeyBinding(keyMagick);
	ClientRegistry.registerKeyBinding(TestOverlay.keyToggleTestOverlay);
    }
    
    public static void addKeyElement(KeyBinding k, Element e)
    {
	elementToKeyMap.put(e, k);
	keyToElementMap.put(k, e);
    }
}
