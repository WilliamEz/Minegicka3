package com.williameze.minegicka3;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import com.williameze.api.TestOverlay;
import com.williameze.api.math.Line;
import com.williameze.api.math.Plane;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.main.Values;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.IEventListener;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public class TickHandlerClient implements IEventListener
{
    @SubscribeEvent
    public void clientTick(ClientTickEvent event)
    {
	CoreBridge.instance().onTick(event);
	if (TestOverlay.keyToggleTestOverlay.isPressed()) TestOverlay.enabled = !TestOverlay.enabled;
	if (Values.clientTicked % 200 == 0) Values.renderDistance = Math.max(
		Math.min(Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16D, 128), 16);
    }

    @SubscribeEvent
    public void worldTick(WorldTickEvent event)
    {
	// CoreBridge.instance().onTick(event);
	Values.clientTicked++;
    }

    @SubscribeEvent
    public void renderWorldTick(RenderTickEvent event)
    {
	CoreBridge.instance().onTick(event);
    }

    // @SubscribeEvent
    public void playerTick(PlayerTickEvent event)
    {
	CoreBridge.instance().onTick(event);
    }

    @SubscribeEvent
    public void renderGameOverlayTick(RenderGameOverlayEvent event)
    {
	ModBase.proxy.getCoreClient().onRenderGameOverlayTick(event);
	if (TestOverlay.enabled) TestOverlay.render();
    }

    @Override
    public void invoke(Event event)
    {
	// if(event instanceof RenderGameOverlayEvent)
	// renderGameOverlayTick((RenderGameOverlayEvent) event);
    }
}
