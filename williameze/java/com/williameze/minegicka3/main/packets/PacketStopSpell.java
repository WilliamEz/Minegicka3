package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.spells.Spell;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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
    public void encodeInto(ByteBuf buffer)
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
    public void decodeFrom(ByteBuf buffer)
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
    public void handleClientSide(Object ctx)
    {
	ModBase.proxy.getCoreClient().spellTriggerReceived(spell, false);
    }

    @Override
    public void handleServerSide(Object ctx)
    {
	ModBase.proxy.getCoreServer().spellTriggerReceived(spell, false);
    }

}
