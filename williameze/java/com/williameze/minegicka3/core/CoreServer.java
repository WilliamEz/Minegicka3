package com.williameze.minegicka3.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.objects.ItemStaff;
import com.williameze.minegicka3.main.packets.PacketPlayerMana;
import com.williameze.minegicka3.main.packets.PacketStartSpell;
import com.williameze.minegicka3.main.packets.PacketStopSpell;
import com.williameze.minegicka3.main.spells.Spell;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

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
    public static MinecraftServer mcs = FMLCommonHandler.instance().getMinecraftServerInstance();
    public Map<World, List<Spell>> worldsSpellsList = new HashMap();
    public Map<EntityPlayer, Spell> playerToSpell = new HashMap();

    public void onServerTick(ServerTickEvent event)
    {
    }

    public void onServerWorldTick(WorldTickEvent event)
    {
	updateSpells(event.world);
    }

    public void onServerPlayerTick(PlayerTickEvent event)
    {
	if (event.phase == Phase.END)
	{
	    if (!playerToSpell.containsKey(event.player)) recoverMana(event.player);
	}
	if (event.player.ticksExisted % 20 == 0)
	{
	    PlayersData.sendPlayerDataToClient(event.player, event.player);
	}
    }

    public void onServerRenderTick(RenderTickEvent event)
    {
    }

    public void updateSpells(World world)
    {
	if (!worldsSpellsList.containsKey(world))
	{
	    worldsSpellsList.put(world, new ArrayList());
	}
	List<Spell> l = worldsSpellsList.get(world);
	List<Spell> toRemove = new ArrayList();
	for (Spell s : l)
	{
	    s.updateSpell();
	    if (s.toBeStopped) toRemove.add(s);
	}
	for (Spell s : toRemove)
	{
	    spellTriggerReceived(s, false);
	}
    }

    public void recoverMana(EntityPlayer p)
    {
	PlayerData pd = PlayersData.getPlayerData_static(p);
	pd.recoverMana();
    }

    public void spellTriggerReceived(Spell s, boolean start)
    {
	World w = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(s.dimensionID);
	Entity caster = s.getCaster();
	if (w != null && caster != null)
	{
	    if (!worldsSpellsList.containsKey(w))
	    {
		worldsSpellsList.put(w, new ArrayList());
	    }
	    List<Spell> l = worldsSpellsList.get(w);
	    if (start)
	    {
		ModBase.packetPipeline.sendToAllAround(new PacketStartSpell(s), new TargetPoint(s.dimensionID, caster.posX,
			caster.posY, caster.posZ, 128));
		l.add(s);
		if (caster instanceof EntityPlayer)
		{
		    playerToSpell.put((EntityPlayer) caster, s);
		}
	    }
	    else
	    {
		ModBase.packetPipeline.sendToAllAround(new PacketStopSpell(s), new TargetPoint(s.dimensionID, caster.posX,
			caster.posY, caster.posZ, 128));
		l.remove(s);
		if (caster instanceof EntityPlayer)
		{
		    playerToSpell.remove(caster);
		}
	    }
	}
    }
}
