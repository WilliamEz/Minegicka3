package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import com.williameze.minegicka3.core.PlayerData;
import com.williameze.minegicka3.core.PlayersData;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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
    public void encodeInto(ByteBuf buffer)
    {
	try
	{
	    String s = p.dimensionID + ";" + p.playerName + ";" + p.mana;
	    buffer.writeInt(s.getBytes().length);
	    buffer.writeBytes(s.getBytes());
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
	    byte[] b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    String[] strings = (new String(b)).split(";");
	    int dimension = Integer.parseInt(strings[0]);
	    String playerName = strings[1];
	    p = PlayersData.getPlayerData_static(playerName, dimension);
	    mana = Double.parseDouble(strings[2]);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void handleClientSide(Object ctx)
    {
	if (p != null)
	{
	    p.mana = mana;
	    PlayersData.addPlayerDataToClient(p);
	}
    }

    @Override
    public void handleServerSide(Object ctx)
    {
	if (p != null)
	{
	    p.mana = mana;
	    PlayersData.addPlayerDataToServer(p);
	}
    }

}
