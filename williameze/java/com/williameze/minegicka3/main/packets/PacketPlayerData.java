package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.core.PlayerData;
import com.williameze.minegicka3.core.PlayersData;
import com.williameze.minegicka3.main.spells.Spell;

public class PacketPlayerData extends Packet
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
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
	try
	{
	    buffer.writeInt(p.dataToString().getBytes().length);
	    buffer.writeBytes(p.dataToString().getBytes());
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
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
    public void handleClientSide(EntityPlayer player)
    {
	PlayersData.addPlayerDataToClient(p);
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
	PlayersData.addPlayerDataToServer(p);
    }

}
