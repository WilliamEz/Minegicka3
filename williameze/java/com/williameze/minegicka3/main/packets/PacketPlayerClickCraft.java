package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.main.ClickCraft;
import com.williameze.minegicka3.main.spells.Spell;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class PacketPlayerClickCraft extends Packet
{
    public EntityPlayer crafter;
    public UUID playerUUID;
    public ItemStack toCraft;
    public int repeat;

    public PacketPlayerClickCraft()
    {
    }

    public PacketPlayerClickCraft(EntityPlayer p, ItemStack is, int rp)
    {
	crafter = p;
	playerUUID = crafter.getPersistentID();
	toCraft = is;
	repeat = rp;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
	try
	{
	    buffer.writeLong(playerUUID.getMostSignificantBits());
	    buffer.writeLong(playerUUID.getLeastSignificantBits());
	    buffer.writeInt(crafter.worldObj.provider.dimensionId);
	    buffer.writeInt(repeat);
	    NBTTagCompound isTag = toCraft.writeToNBT(new NBTTagCompound());
	    byte[] b = CompressedStreamTools.compress(isTag);
	    buffer.writeInt(b.length);
	    buffer.writeBytes(b);
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
	    playerUUID = new UUID(buffer.readLong(), buffer.readLong());
	    int dimension = buffer.readInt();
	    repeat = buffer.readInt();
	    byte[] b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    NBTTagCompound isTag = CompressedStreamTools.decompress(b);
	    toCraft = ItemStack.loadItemStackFromNBT(isTag);
	    crafter = (EntityPlayer) CoreBridge.instance().getEntityByUUID(dimension, playerUUID);
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
	ClickCraft.playerCraft(crafter, toCraft, repeat);
    }

}
