package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;
import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.core.PlayerData;
import com.williameze.minegicka3.core.PlayersData;
import com.williameze.minegicka3.main.CommandUnlock;
import com.williameze.minegicka3.main.spells.Spell;

public class PacketPlayerUnlockAll extends Packet
{
    public String name;
    public int dim;

    public PacketPlayerUnlockAll()
    {
    }

    public PacketPlayerUnlockAll(EntityPlayer p)
    {
	name = p.getGameProfile().getName();
	dim = p.worldObj.provider.dimensionId;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
	try
	{
	    buffer.writeInt(dim);
	    buffer.writeInt(name.getBytes().length);
	    buffer.writeBytes(name.getBytes());
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
	    dim = buffer.readInt();
	    byte[] b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    name = new String(b);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
	PlayerData pd = PlayersData.getPlayerData_static(name, dim);
	pd.unlockEverything();
    }

}
