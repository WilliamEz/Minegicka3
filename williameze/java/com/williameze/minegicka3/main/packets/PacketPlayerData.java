package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.functional.PlayerData;
import com.williameze.minegicka3.functional.PlayersData;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketPlayerData extends Packet<PacketPlayerData>
{
    public PlayerData p;

    public PacketPlayerData()
    {
    }

    public PacketPlayerData(PlayerData pd)
    {
	p = pd;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
	try
	{
	    String s = p.dataToString();
	    byte[] b = s.getBytes();
	    if (b == null) b = new byte[0];
	    buffer.writeInt(b.length);
	    buffer.writeBytes(b);
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
	    int length = buffer.readInt();
	    byte[] bytes = new byte[length];
	    buffer.readBytes(bytes);
	    String data = new String(bytes);
	    p = PlayerData.stringToData(data);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void handleClientSide(Object ctx)
    {
	PlayersData.addPlayerDataToClient(p);
    }

    @Override
    public void handleServerSide(Object ctx)
    {
	PlayersData.addPlayerDataToServer(p);
    }

}
