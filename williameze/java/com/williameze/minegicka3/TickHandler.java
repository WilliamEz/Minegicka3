package com.williameze.minegicka3;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.core.PlayersData;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.IEventListener;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public class TickHandler implements IEventListener
{
    @SubscribeEvent
    public void serverTick(ServerTickEvent event)
    {
	CoreBridge.instance().onTick(event);
    }

    @SubscribeEvent
    public void worldTick(WorldTickEvent event)
    {
	CoreBridge.instance().onTick(event);
    }

    @SubscribeEvent
    public void playerTick(PlayerTickEvent event)
    {
	CoreBridge.instance().onTick(event);
    }

    @Override
    public void invoke(Event event)
    {
	// if(event instanceof RenderGameOverlayEvent)
	// renderGameOverlayTick((RenderGameOverlayEvent) event);
    }
}
