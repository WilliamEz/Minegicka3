package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.main.magicks.Magick;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketStartMagick extends Packet<PacketStartMagick>
{
    public Magick magick;
    public int magickID;
    public int dimensionID;
    public double x, y, z;
    public UUID casterUUID;
    public String casterName = null;
    public NBTTagCompound tag;

    public Entity loadedCaster;

    public PacketStartMagick()
    {
	tag = new NBTTagCompound();
    }

    public PacketStartMagick(Magick m, World world, double x, double y, double z, UUID uuid, String name, NBTTagCompound tag)
    {
	magick = m;
	magickID = m.getID();
	dimensionID = world.provider.dimensionId;
	this.x = x;
	this.y = y;
	this.z = z;
	casterUUID = uuid;
	if (name != null && name.length() > 0 && name != "") casterName = name;
	this.tag = tag;
	if (tag == null) tag = new NBTTagCompound();
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
	try
	{
	    buffer.writeInt(magickID);
	    buffer.writeInt(dimensionID);
	    buffer.writeDouble(x);
	    buffer.writeDouble(y);
	    buffer.writeDouble(z);
	    buffer.writeLong(casterUUID.getMostSignificantBits());
	    buffer.writeLong(casterUUID.getLeastSignificantBits());
	    String name = casterName;
	    if (name == null) name = "@NULL@#";
	    byte[] b = name.getBytes();
	    buffer.writeInt(b.length);
	    buffer.writeBytes(b);
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
	    magickID = buffer.readInt();
	    magick = Magick.getMagickFromID(magickID);
	    dimensionID = buffer.readInt();
	    x = buffer.readDouble();
	    y = buffer.readDouble();
	    z = buffer.readDouble();
	    casterUUID = new UUID(buffer.readLong(), buffer.readLong());
	    byte[] b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    String name = new String(b);
	    if (name.equals("@NULL@#")) name = null;
	    casterName = name;
	    b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    tag = CompressedStreamTools.decompress(b);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void handleClientSide(Object ctx)
    {
	if (dimensionID == ModBase.proxy.getClientWorld().provider.dimensionId)
	{
	    loadedCaster = CoreBridge.instance().getEntityFromArgs(casterUUID, dimensionID, casterName, true, false, true);
	    magick.clientReceivedMagick(ModBase.proxy.getClientWorld(), x, y, z, loadedCaster, tag);
	}
    }

    @Override
    public void handleServerSide(Object ctx)
    {
	World world = ModBase.proxy.getWorldForDimension(dimensionID);
	loadedCaster = CoreBridge.instance().getEntityFromArgs(casterUUID, dimensionID, casterName, true, false, true);
	magick.serverReceivedMagick(world, x, y, z, loadedCaster, tag);
    }
}
