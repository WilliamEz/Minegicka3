package com.williameze.minegicka3.core;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.server.FMLServerHandler;

public class CoreServer
{
    /** Instantiate the CoreServer **/
    private CoreServer()
    {
    }

    private static CoreServer instance;

    public static CoreServer instance()
    {
	return instance == null ? (instance = new CoreServer()) : instance;
    }

    /** Minecraft server instance **/
    public static MinecraftServer mcs = FMLServerHandler.instance().getServer();

    public void onServerTick(ServerTickEvent event)
    {
    }

    public void onServerWorldTick(WorldTickEvent event)
    {
    }

    public void onServerPlayerTick(PlayerTickEvent event)
    {
    }

    public void onServerRenderTick(RenderTickEvent event)
    {
    }
    
}
