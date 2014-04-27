package com.williameze.minegicka3;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.core.CoreClient;
import com.williameze.minegicka3.core.CoreServer;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommonProxy
{
    public void load()
    {
	FMLCommonHandler.instance().bus().register(new TickHandler());
	FMLCommonHandler.instance().bus().register(new EventsHandler());
	MinecraftForge.EVENT_BUS.register(new TickHandler());
	MinecraftForge.EVENT_BUS.register(new EventsHandler());
	// MinecraftForge.EVENT_BUS.register(CoreBridge.client);
    }

    public void postLoad()
    {

    }

    public EntityPlayer getClientPlayer()
    {
	return null;
    }

    public World getClientWorld()
    {
	return null;
    }

    public void registerRenderHandler()
    {
    }

    public void registerItemRenderer(Item i)
    {

    }

    public void initCoreBridge(CoreBridge cb)
    {
	cb.server = CoreServer.instance();
    }

    public CoreClient getCoreClient()
    {
	return null;
    }

    public CoreServer getCoreServer()
    {
	return (CoreServer) CoreBridge.instance().server;
    }
}
