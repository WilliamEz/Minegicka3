package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.williameze.minegicka3.functional.CoreBridge;
import com.williameze.minegicka3.mechanics.ClickCraft;
import com.williameze.minegicka3.mechanics.CraftEntry;

public class PacketPlayerClickCraft extends Packet<PacketPlayerClickCraft>
{
    public EntityPlayer crafter;
    public String playerName;
    public int dimension;
    public int toCraft;
    public int repeat;

    public PacketPlayerClickCraft()
    {
    }

    public PacketPlayerClickCraft(EntityPlayer p, CraftEntry entry, int rp)
    {
	crafter = p;
	playerName = p.getGameProfile().getName();
	dimension = p.worldObj.provider.dimensionId;
	toCraft = entry.id;
	repeat = rp;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
	try
	{
	    buffer.writeInt(dimension);
	    buffer.writeInt(toCraft);
	    buffer.writeInt(repeat);
	    byte[] b = playerName.getBytes();
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
	    dimension = buffer.readInt();
	    toCraft = buffer.readInt();
	    repeat = buffer.readInt();
	    byte[] b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    playerName = new String(b);
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
