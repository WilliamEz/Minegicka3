package com.williameze.minegicka3;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.williameze.minegicka3.core.PlayersData;
import com.williameze.minegicka3.main.Values;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.IEventListener;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class EventsHandler implements IEventListener
{
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
	if (!event.world.isRemote)
	{
	    PlayersData psd = new PlayersData(event.world);
	    try
	    {
		psd.loadPlayersData();
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }
	    PlayersData.worldsPlayersDataMap.put(event.world, psd);
	}
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event)
    {
	if (!event.world.isRemote)
	{
	    try
	    {
		PlayersData.getWorldPlayersData(event.world).savePlayersData();
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }
	}
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
	Values.onWorldUnload();
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
	Entity e = event.entity;
	World w = event.world;
	if (!Values.worldEntitiesUUIDMap.containsKey(w))
	{
	    Values.worldEntitiesUUIDMap.put(w, new HashMap());
	}
	Values.worldEntitiesUUIDMap.get(w).put(e.getPersistentID(), e);
    }

    @SubscribeEvent
    public void onClientPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event)
    {
	PlayersData.getWorldPlayersData(event.player.worldObj).sendPlayerDataToClient(event.player, event.player);
    }

    @SubscribeEvent
    public void onEntityKilled(LivingDropsEvent event)
    {
	if (!event.entityLiving.worldObj.isRemote && new Random().nextInt(1000) == 0)
	{
	    EntityItem ei = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY,
		    event.entityLiving.posZ, new ItemStack(ModBase.thingy));
	    ei.delayBeforeCanPickup = 100;
	    event.drops.add(ei);
	}
    }

    @Override
    public void invoke(Event event)
    {
    }

}
