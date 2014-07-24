package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import com.williameze.minegicka3.functional.PlayerData;
import com.williameze.minegicka3.functional.PlayersData;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketPlayerUnlockAll extends Packet<PacketPlayerUnlockAll>
{
    public String playerName;
    public int dim;

    public PacketPlayerUnlockAll()
    {
    }

    public PacketPlayerUnlockAll(EntityPlayer p)
    {
	playerName = p.getGameProfile().getName();
	dim = p.worldObj.provider.dimensionId;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
	try
	{
	    buffer.writeInt(dim);
	    buffer.writeInt(playerName.getBytes().length);
	    buffer.writeBytes(playerName.getBytes());
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void decodeFrom(ByteBuf buffer)
    {
	try
	{
	    dim = buffer.readInt();
	    byte[] b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    playerName = new String(b);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void handleClientSide(Object ctx)
    {
    }

    @Override
    public void handleServerSide(Object ctx)
    {
	PlayerData pd = PlayersData.getPlayerData_static(playerName, dim);
	pd.unlockEverything();
    }

}
