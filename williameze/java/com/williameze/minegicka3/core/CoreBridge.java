package com.williameze.minegicka3.core;

import com.williameze.minegicka3.ModBase;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CoreBridge
{

    /** Instantiate the Core **/
    private CoreBridge()
    {
	ModBase.proxy.initCoreBridge(this);
    }

    private static CoreBridge instance;

    public static CoreBridge instance()
    {
	return instance == null ? (instance = new CoreBridge()) : instance;
    }

    public Object client;
    public Object server;

    public void onTick(TickEvent event)
    {
	if (event instanceof ClientTickEvent)
	{
	    ((CoreClient) client).onClientTick((ClientTickEvent) event);
	}
	if (event instanceof ServerTickEvent)
	{
	    ((CoreServer) server).onServerTick((ServerTickEvent) event);
	}
	if (event instanceof WorldTickEvent)
	{
	    if (event.side == Side.SERVER) ((CoreServer) server).onServerWorldTick((WorldTickEvent) event);
	}
	if (event instanceof PlayerTickEvent)
	{
	    if (event.side == Side.CLIENT) ((CoreClient) client).onClientPlayerTick((PlayerTickEvent) event);
	    if (event.side == Side.SERVER) ((CoreServer) server).onServerPlayerTick((PlayerTickEvent) event);
	}
	if (event instanceof RenderTickEvent)
	{
	    if (event.side == Side.CLIENT) ((CoreClient) client).onClientRenderTick((RenderTickEvent) event);
	    if (event.side == Side.SERVER) ((CoreServer) server).onServerRenderTick((RenderTickEvent) event);
	}
    }
}
