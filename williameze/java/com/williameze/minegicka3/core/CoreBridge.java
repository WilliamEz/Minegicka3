package com.williameze.minegicka3.core;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class CoreBridge
{

    /** Instantiate the Core **/
    private CoreBridge()
    {
    }

    private static CoreBridge instance;

    public static CoreBridge instance()
    {
	return instance == null ? (instance = new CoreBridge()) : instance;
    }

    /** Instances **/
    public static Minecraft mc = Minecraft.getMinecraft();
    public static CoreClient client = CoreClient.instance();
    public static CoreServer server = CoreServer.instance();

    public void onTick(TickEvent event)
    {
	if (event instanceof ClientTickEvent)
	{
	    client.onClientTick((ClientTickEvent) event);
	}
	if (event instanceof ServerTickEvent)
	{
	    server.onServerTick((ServerTickEvent) event);
	}
	if (event instanceof WorldTickEvent)
	{
	    if (event.side == Side.CLIENT) client.onClientWorldTick((WorldTickEvent) event);
	    if (event.side == Side.SERVER) server.onServerWorldTick((WorldTickEvent) event);
	}
	if (event instanceof PlayerTickEvent)
	{
	    if (event.side == Side.CLIENT) client.onClientPlayerTick((PlayerTickEvent) event);
	    if (event.side == Side.SERVER) server.onServerPlayerTick((PlayerTickEvent) event);
	}
	if (event instanceof RenderTickEvent)
	{
	    if (event.side == Side.CLIENT) client.onClientRenderTick((RenderTickEvent) event);
	    if (event.side == Side.SERVER) server.onServerRenderTick((RenderTickEvent) event);
	}
    }
}
