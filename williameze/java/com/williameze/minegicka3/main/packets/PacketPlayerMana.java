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
import com.williameze.minegicka3.main.spells.Spell;

public class PacketPlayerMana extends Packet
{
    public PlayerData p;
    public double mana;

    public PacketPlayerMana()
    {
    }

    public PacketPlayerMana(PlayerData pd)
    {
	p = pd;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
	try
	{
	    String s = p.dimensionID+";"+p.playerProfile.getId()+";"+p.playerProfile.getName()+";"+p.mana;
	    buffer.writeInt(s.getBytes().length);
	    buffer.writeBytes(s.getBytes());
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
	    byte[] b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    String[] strings = (new String(b)).split(";");
	    int dimension = Integer.parseInt(strings[0]);
	    GameProfile gf = new GameProfile(strings[1], strings[2]);
	    p = PlayersData.getPlayerData_static(gf, dimension);
	    mana = Double.parseDouble(strings[3]);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {
	if (p != null)
	{
	    p.mana = mana;
	    PlayersData.addPlayerDataToClient(p);
	}
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
	if (p != null)
	{
	    p.mana = mana;
	    PlayersData.addPlayerDataToServer(p);
	}
    }

}
