package com.williameze.minegicka3.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.packets.PacketPlayerData;
import com.williameze.minegicka3.main.packets.PacketPlayerMana;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PlayersData
{
    public static Map<World, PlayersData> worldsPlayersDataMap = new HashMap();
    public static PlayerData clientPlayerData;
    static
    {
	clientPlayerData = new PlayerData();
    }

    public World world;
    public File saveDir;
    public Set<PlayerData> allPlayersData = new HashSet();

    public PlayersData(World w)
    {
	world = w;
	if (!w.isRemote)
	{
	    try
	    {
		saveDir = new File(world.getSaveHandler().getWorldDirectory().getCanonicalPath() + File.separatorChar + "minegicka" + File.separatorChar
			+ "Players-WDI-" + world.provider.dimensionId + ".dat");
		saveDir.getParentFile().mkdirs();
	    }
	    catch (IOException e)
	    {
		e.printStackTrace();
	    }
	}
	createPlayersData();
    }

    public void savePlayersData() throws Exception
    {
	if (world.isRemote) return;

	saveDir.createNewFile();
	NBTTagCompound tag = new NBTTagCompound();
	String playerIDs = "";
	Iterator<PlayerData> ite = allPlayersData.iterator();
	while (ite.hasNext())
	{
	    PlayerData data = ite.next();
	    String name = data.playerUUID.toString();
	    if (name != null && data != null && data.dimensionID == world.provider.dimensionId)
	    {
		tag.setString(name, data.dataToString());
		playerIDs += name + ";";
	    }
	}
	tag.setString("IDS", playerIDs);
	tag.setBoolean("Made", true);
	CompressedStreamTools.safeWrite(tag, saveDir);
    }

    public void loadPlayersData() throws Exception
    {
	if (world.isRemote) return;

	if (saveDir.exists())
	{
	    NBTTagCompound tag = CompressedStreamTools.read(saveDir);
	    if (tag.getBoolean("Made"))
	    {
		String[] ids = tag.getString("IDS").split(";");
		for (String id : ids)
		{
		    if (id.length() > 0)
		    {
			String data = tag.getString(id);
			PlayerData pd = PlayerData.stringToData(data);
			allPlayersData.add(pd);
		    }
		}
	    }
	}
    }

    public void createPlayersData()
    {
	for (Object o : world.playerEntities)
	{
	    EntityPlayer p = (EntityPlayer) o;
	    PlayerData pd = new PlayerData(p);
	    addOrModifyPlayerData(pd);
	}
    }

    public PlayerData getPlayerData(EntityPlayer p)
    {
	PlayerData pd = getPlayerData(p.getGameProfile().getName());
	if (pd != null) return pd;
	pd = new PlayerData(p);
	addOrModifyPlayerData(pd);
	return pd;
    }

    public PlayerData getPlayerData(String name)
    {
	Iterator<PlayerData> ite = allPlayersData.iterator();
	while (ite.hasNext())
	{
	    PlayerData data = ite.next();
	    if (data != null && data.playerName.equals(name))
	    {
		return data;
	    }
	}
	return null;
    }

    public PlayerData addOrModifyPlayerData(PlayerData pd)
    {
	PlayerData pd1 = getPlayerData(pd.playerName);
	if (pd1 != null)
	{
	    pd1.dataFromString(pd.dataToString());
	    return pd1;
	}
	else
	{
	    allPlayersData.add(pd);
	    return pd;
	}
    }

    public static PlayerData getPlayerData_static(EntityPlayer p)
    {
	return getWorldPlayersData(p.worldObj).getPlayerData(p);
    }

    public static PlayerData getPlayerData_static(String name, int dimension)
    {
	if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
	{
	    if (ModBase.proxy.getClientWorld().provider.dimensionId == dimension)
	    {
		return getWorldPlayersData(ModBase.proxy.getClientWorld()).getPlayerData(name);
	    }
	}
	else if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
	{
	    return getWorldPlayersData(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimension)).getPlayerData(name);
	}
	return null;
    }

    public static void sendPlayerDataToAllClients(EntityPlayer pToSend)
    {
	ModBase.packetPipeline.sendToAll(new PacketPlayerData(getPlayerData_static(pToSend)));
    }

    public static void sendPlayerManaToAllClients(EntityPlayer pToSend)
    {
	ModBase.packetPipeline.sendToAll(new PacketPlayerMana(getPlayerData_static(pToSend)));
    }

    public static void sendPlayerDataToWorldClients(EntityPlayer pToSend)
    {
	PlayerData pd = getPlayerData_static(pToSend);
	ModBase.packetPipeline.sendToDimension(new PacketPlayerData(pd), pd.dimensionID);
    }

    public static void sendPlayerManaToWorldClients(EntityPlayer pToSend)
    {
	PlayerData pd = getPlayerData_static(pToSend);
	ModBase.packetPipeline.sendToDimension(new PacketPlayerMana(pd), pd.dimensionID);
    }

    public static void sendPlayerDataToClientsAround(EntityPlayer pToSend, TargetPoint point)
    {
	ModBase.packetPipeline.sendToAllAround(new PacketPlayerData(getPlayerData_static(pToSend)), point);
    }

    public static void sendPlayerManaToClientsAround(EntityPlayer pToSend, TargetPoint point)
    {
	ModBase.packetPipeline.sendToAllAround(new PacketPlayerMana(getPlayerData_static(pToSend)), point);
    }

    public static void sendPlayerDataToClient(EntityPlayer pToSend, EntityPlayer pDest)
    {
	ModBase.packetPipeline.sendTo(new PacketPlayerData(getPlayerData_static(pToSend)), (EntityPlayerMP) pDest);
    }

    public static void sendPlayerManaToClient(EntityPlayer pToSend, EntityPlayer pDest)
    {
	ModBase.packetPipeline.sendTo(new PacketPlayerMana(getPlayerData_static(pToSend)), (EntityPlayerMP) pDest);
    }

    public static PlayersData getWorldPlayersData(World w)
    {
	if (worldsPlayersDataMap.containsKey(w)) return worldsPlayersDataMap.get(w);
	else
	{
	    PlayersData psd = new PlayersData(w);
	    worldsPlayersDataMap.put(w, psd);
	    return psd;
	}
    }

    public static void addPlayerDataToServer(PlayerData pd)
    {
	World w = MinecraftServer.getServer().worldServerForDimension(pd.dimensionID);
	PlayersData psd = getWorldPlayersData(w);
	psd.addOrModifyPlayerData(pd);
    }

    @SideOnly(Side.CLIENT)
    public static void addPlayerDataToClient(PlayerData pd)
    {
	World w = ModBase.proxy.getClientWorld();
	if (w.provider.dimensionId == pd.dimensionID)
	{
	    PlayerData pd1 = getWorldPlayersData(w).addOrModifyPlayerData(pd);
	    if (Minecraft.getMinecraft().thePlayer.getGameProfile().getName().equals(pd.playerName))
	    {
		clientPlayerData = pd1;
	    }
	}
    }
}
