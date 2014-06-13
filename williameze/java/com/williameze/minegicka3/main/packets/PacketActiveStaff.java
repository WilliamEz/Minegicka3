package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.main.objects.items.ItemStaff;

public class PacketActiveStaff extends Packet<PacketActiveStaff>
{
    public ItemStack is;
    public int dimensionID;
    public UUID casterUUID;
    public String casterName = null;

    public PacketActiveStaff()
    {
    }

    public PacketActiveStaff(World world, EntityPlayer e, ItemStack is)
    {
	dimensionID = world.provider.dimensionId;
	casterUUID = e.getPersistentID();
	casterName = e.getGameProfile().getName();
	this.is = is;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
	try
	{
	    buffer.writeInt(dimensionID);
	    buffer.writeLong(casterUUID.getMostSignificantBits());
	    buffer.writeLong(casterUUID.getLeastSignificantBits());
	    String name = casterName;
	    if (name == null) name = "@NULL@#";
	    byte[] b = name.getBytes();
	    buffer.writeInt(b.length);
	    buffer.writeBytes(b);
	    NBTTagCompound tag = new NBTTagCompound();
	    is.writeToNBT(tag);
	    b = CompressedStreamTools.compress(tag);
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
	    dimensionID = buffer.readInt();
	    casterUUID = new UUID(buffer.readLong(), buffer.readLong());
	    byte[] b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    String name = new String(b);
	    if (name.equals("@NULL@#")) name = null;
	    casterName = name;
	    b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    NBTTagCompound tag = CompressedStreamTools.decompress(b);
	    is = ItemStack.loadItemStackFromNBT(tag);
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
	World world = ModBase.proxy.getWorldForDimension(dimensionID);
	Entity e = CoreBridge.instance().getEntityFromArgs(casterUUID, dimensionID, casterName, true, false, true);
	if (e instanceof EntityLivingBase && is != null && is.getItem() instanceof ItemStaff)
	{
	    ((ItemStaff) is.getItem()).activeAbility(world, (EntityLivingBase) e, is);
	}
    }
}
