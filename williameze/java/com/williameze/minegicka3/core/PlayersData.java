package com.williameze.minegicka3.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;
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
    public Map<GameProfile, PlayerData> allPlayersData = new HashMap();

    public PlayersData(World w)
    {
	world = w;
	if (!w.isRemote)
	{
	    try
	    {
		saveDir = new File(world.getSaveHandler().getWorldDirectory().getCanonicalPath() + File.separatorChar + "minegicka"
			+ File.separatorChar + "Players.dat");
		saveDir.mkdirs();
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
	Iterator<Entry<GameProfile, PlayerData>> ite = allPlayersData.entrySet().iterator();
	while (ite.hasNext())
	{
	    Entry<GameProfile, PlayerData> e = ite.next();
	    tag.setString(e.getKey().toString(), e.getValue().dataToString());
	    playerIDs += e.getKey().getId() + "%" + e.getKey().getName() + ";";
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
			allPlayersData.put(pd.playerProfile, pd);
		    }
		}
	    }
	}
    }

    public void createPlayersData()
    {
	List<EntityPlayer> l = new ArrayList();
	l.addAll(world.playerEntities);
	for (Object o : world.playerEntities)
	{
	    EntityPlayer p = (EntityPlayer) o;
	    PlayerData pd = new PlayerData(p);
	    addOrModifyPlayerData(pd);
	}
    }

    public PlayerData getPlayerData(EntityPlayer p)
    {
	if (allPlayersData.containsKey(p.getGameProfile()))
	{
	    return allPlayersData.get(p.getGameProfile());
	}
	else
	{
	    PlayerData pd = new PlayerData(p);
	    allPlayersData.put(p.getGameProfile(), pd);
	    return pd;
	}
    }

    public PlayerData getPlayerData(GameProfile gf)
    {
	return allPlayersData.get(gf);
    }

    public void addOrModifyPlayerData(PlayerData pd)
    {
	allPlayersData.put(pd.playerProfile, pd);
    }

    public static PlayerData getPlayerData_static(EntityPlayer p)
    {
	return getWorldPlayersData(p.worldObj).getPlayerData(p);
    }

    public static PlayerData getPlayerData_static(GameProfile gf, int dimension)
    {
	if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
	{
	    if (ModBase.proxy.getClientWorld().provider.dimensionId == dimension)
	    {
		getWorldPlayersData(ModBase.proxy.getClientWorld()).getPlayerData(gf);
	    }
	}
	else if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
	{
	    getWorldPlayersData(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimension)).getPlayerData(
		    gf);
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
	if (Minecraft.getMinecraft().thePlayer.getGameProfile().getName().equals(pd.playerProfile.getName()))
	{
	    clientPlayerData = pd;
	}
	World w = ModBase.proxy.getClientWorld();
	if (w.provider.dimensionId == pd.dimensionID)
	{
	    getWorldPlayersData(w).addOrModifyPlayerData(pd);
	}
    }
}
