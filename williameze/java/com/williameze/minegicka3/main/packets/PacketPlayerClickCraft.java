package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.main.ClickCraft;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketPlayerClickCraft extends Packet
{
    public EntityPlayer crafter;
    public String playerName;
    public int dimension;
    public ItemStack toCraft;
    public int repeat;

    public PacketPlayerClickCraft()
    {
    }

    public PacketPlayerClickCraft(EntityPlayer p, ItemStack is, int rp)
    {
	crafter = p;
	playerName = p.getGameProfile().getName();
	dimension = p.worldObj.provider.dimensionId;
	toCraft = is;
	repeat = rp;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
	try
	{
	    byte[] b = playerName.getBytes();
	    buffer.writeInt(b.length);
	    buffer.writeBytes(b);
	    buffer.writeInt(dimension);
	    buffer.writeInt(repeat);
	    NBTTagCompound isTag = toCraft.writeToNBT(new NBTTagCompound());
	    b = CompressedStreamTools.compress(isTag);
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
	    byte[] b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    playerName = new String(b);
	    dimension = buffer.readInt();
	    repeat = buffer.readInt();
	    b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    NBTTagCompound isTag = CompressedStreamTools.decompress(b);
	    toCraft = ItemStack.loadItemStackFromNBT(isTag);
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
	Entity e = CoreBridge.instance().getEntityFromArgs(null, dimension, playerName, true, false, true);
	if (e instanceof EntityPlayer) crafter = (EntityPlayer) e;
	ClickCraft.playerCraft(crafter, toCraft, repeat);
    }

}
