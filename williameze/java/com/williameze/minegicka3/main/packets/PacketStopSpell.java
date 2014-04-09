package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.spells.Spell;

public class PacketStopSpell extends Packet
{
    public Spell spell;

    public PacketStopSpell()
    {
    }

    public PacketStopSpell(Spell s)
    {
	spell = s;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
	NBTTagCompound tag = spell.writeToNBT();
	try
	{
	    byte[] b = CompressedStreamTools.compress(tag);
	    buffer.writeInt(b.length);
	    buffer.writeBytes(b);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
	byte[] b = new byte[buffer.readInt()];
	buffer.readBytes(b);
	try
	{
	    NBTTagCompound tag = CompressedStreamTools.decompress(b);
	    spell = Spell.createFromNBT(tag);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {
	ModBase.proxy.getCoreClient().spellTriggerReceived(spell, false);
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
	ModBase.proxy.getCoreServer().spellTriggerReceived(spell, false);
    }

}
