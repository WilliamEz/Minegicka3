package com.williameze.minegicka3;

import net.minecraftforge.common.MinecraftForge;

import com.williameze.minegicka3.core.Core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(name = ModBase.MODNAME, modid = ModBase.MODID, version = ModBase.VERSION)
public class ModBase
{
    public static final String PACKAGE = "com.williameze.minegicka3";
    public static final String MODNAME = "Minegicka III";
    public static final String MODID = "minegicka3";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = PACKAGE+".ClientProxy", serverSide = PACKAGE+".CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
	FMLCommonHandler.instance().bus().register(new TickHandler());
	MinecraftForge.EVENT_BUS.register(new TickHandler());
	Core.instance();
	initBlocksAndItems();
	initRecipes();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
	proxy.registerRenderHandler();
    }

    public void initBlocksAndItems()
    {
    }

    public void initRecipes()
    {

    }
}
