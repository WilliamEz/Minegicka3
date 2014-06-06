package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.main.magicks.Magick;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class PacketStartMagick extends Packet
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
    public void handleClientSide(EntityPlayer player)
    {
	if (dimensionID == ModBase.proxy.getClientWorld().provider.dimensionId)
	{
	    loadedCaster = CoreBridge.instance().getEntityByUUID(dimensionID, casterUUID);
	    if (casterName != null)
	    {
		World world = ModBase.proxy.getClientWorld();
		for (Object p : world.playerEntities)
		{
		    if (p instanceof EntityPlayer && ((EntityPlayer) p).getGameProfile().getName().equals(casterName))
		    {
			loadedCaster = (EntityPlayer) p;
			break;
		    }
		}
	    }
	    magick.clientReceivedMagick(ModBase.proxy.getClientWorld(), x, y, z, loadedCaster, tag);
	}

    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
	World world = ModBase.proxy.getWorldForDimension(dimensionID);
	loadedCaster = CoreBridge.instance().getEntityByUUID(dimensionID, casterUUID);
	if (casterName != null)
	{
	    for (Object p : world.playerEntities)
	    {
		if (p instanceof EntityPlayer && ((EntityPlayer) p).getGameProfile().getName().equals(casterName))
		{
		    loadedCaster = (EntityPlayer) p;
		    break;
		}
	    }
	}
	magick.serverReceivedMagick(world, x, y, z, loadedCaster, tag);
    }
}
