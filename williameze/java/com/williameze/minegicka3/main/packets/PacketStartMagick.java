package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.main.magicks.Magick;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.server.FMLServerHandler;

public class PacketStartMagick extends Packet
{
    public Magick magick;
    public int magickID;
    public int dimensionID;
    public double x, y, z;
    public UUID casterUUID;
    public NBTTagCompound tag;

    public PacketStartMagick()
    {
	tag = new NBTTagCompound();
    }

    public PacketStartMagick(Magick m, World world, double x, double y, double z, UUID uuid, NBTTagCompound tag)
    {
	magick = m;
	magickID = m.getID();
	dimensionID = world.provider.dimensionId;
	this.x = x;
	this.y = y;
	this.z = z;
	casterUUID = uuid;
	this.tag = tag;
	if (tag == null) tag = new NBTTagCompound();
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
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
	    byte[] b = CompressedStreamTools.compress(tag);
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
	    magickID = buffer.readInt();
	    magick = Magick.getMagickFromID(magickID);
	    dimensionID = buffer.readInt();
	    x = buffer.readDouble();
	    y = buffer.readDouble();
	    z = buffer.readDouble();
	    casterUUID = new UUID(buffer.readLong(), buffer.readLong());
	    byte[] b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    tag = CompressedStreamTools.decompress(b);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {
	if (dimensionID == ModBase.proxy.getClientWorld().provider.dimensionId) magick.clientReceivedMagick(ModBase.proxy.getClientWorld(),
		x, y, z, CoreBridge.instance().getEntityByUUID(dimensionID, casterUUID), tag);
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
	World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimensionID);
	magick.serverReceivedMagick(world, x, y, z, CoreBridge.instance().getEntityByUUID(dimensionID, casterUUID), tag);
    }

}
