package com.williameze.minegicka3.main.models;

import java.util.HashMap;
import java.util.Map;

import com.williameze.minegicka3.main.objects.ItemHat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHat extends ModelBase
{
    public Map<ItemHat, ModelHat> models = new HashMap();
    
    public static void load()
    {
	
    }
}
