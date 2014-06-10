package com.williameze.minegicka3.core;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;

import cpw.mods.fml.common.FMLCommonHandler;
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

    /**
     * Search for players based on name. Seach for entities based on uuid.
     * 
     * @param uuid
     *            target UUID
     * @param dim
     *            world dimension id
     * @param name
     *            target name(mostly username)
     * @param checkInWorldPlayers
     *            whether to find players in world.playerEntities
     * @param checkInWorldEntities
     *            whether to search in world.loadedEntityList if mentioned uuid
     *            is not saved
     * @param playerPriority
     *            prefer to return player (true) or non-player (false)
     **/
    public Entity getEntityFromArgs(UUID uuid, int dim, String name, boolean checkInWorldPlayers, boolean checkInWorldEntities, boolean playerPriority)
    {
	World w = getWorldByDim(dim);
	EntityPlayer p = null;
	if (checkInWorldPlayers)
	{
	    if (w != null)
	    {
		for (Object o : w.playerEntities)
		{
		    EntityPlayer pl = (EntityPlayer) o;
		    if (name != null && name.equals(pl.getGameProfile().getName()))
		    {
			if (playerPriority) return pl;
			else p = pl;
			break;
		    }
		}
	    }
	}
	Entity e = getEntityByUUID(dim, uuid);
	if (checkInWorldEntities && uuid != null)
	{
	    if (w != null)
	    {
		for (Object o : w.loadedEntityList)
		{
		    Entity ent = (Entity) o;
		    if (ent != null && ent != p && uuid.equals(ent.getPersistentID()))
		    {
			if (!playerPriority) return ent;
			else e = ent;
			break;
		    }
		}
	    }
	}
	if (e == null) return p;
	else if (p == null) return e;
	else
	{
	    if (playerPriority) return p;
	    else return e;
	}
    }

    public Entity getEntityByUUID(int dimensionID, UUID uuid)
    {
	if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
	{
	    if (ModBase.proxy.getClientWorld().provider.dimensionId == dimensionID
		    && Values.worldEntitiesUUIDMap.containsKey(ModBase.proxy.getClientWorld()))
	    {
		return Values.worldEntitiesUUIDMap.get(ModBase.proxy.getClientWorld()).get(uuid);
	    }
	}
	else if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
	{
	    World w = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimensionID);
	    if (w != null && Values.worldEntitiesUUIDMap.containsKey(w))
	    {
		return Values.worldEntitiesUUIDMap.get(w).get(uuid);
	    }
	}
	return null;
    }

    public World getWorldByDim(int dimensionID)
    {
	if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
	{
	    if (ModBase.proxy.getClientWorld().provider.dimensionId == dimensionID)
	    {
		return ModBase.proxy.getClientWorld();
	    }
	}
	else if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
	{
	    World w = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimensionID);
	    return w;
	}
	return null;
    }
}
