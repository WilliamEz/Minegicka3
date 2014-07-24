package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import com.williameze.minegicka3.functional.PlayerData;
import com.williameze.minegicka3.functional.PlayersData;
import com.williameze.minegicka3.mechanics.magicks.Magick;

public class PacketClientUnlockMagick extends Packet<PacketClientUnlockMagick>
{
    public PlayerData p;
    public Magick magick;

    public PacketClientUnlockMagick()
    {
    }

    public PacketClientUnlockMagick(PlayerData e, Magick m)
    {
	p = e;
	magick = m;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
	try
	{
	    String s = p.dimensionID + ";" + p.playerName + ";" + magick.getID();
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
	    magick = Magick.getMagickFromID(Integer.parseInt(strings[2]));
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
	    p.unlock(magick);
	    PlayersData.addPlayerDataToClient(p);
	}
    }

    @Override
    public void handleServerSide(Object ctx)
    {
	if (p != null)
	{
	    p.unlock(magick);
	    PlayersData.addPlayerDataToServer(p);
	}
    }

}
