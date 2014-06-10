package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.EnumMap;

import com.williameze.minegicka3.ModBase;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketHandler extends FMLIndexedMessageToMessageCodec<Packet>
{
    private static int discriminator;
    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    public PacketHandler()
    {
	channels = NetworkRegistry.INSTANCE.newChannel("SpaceDistortion", this);
	registerPackets();
    }

    public void registerPackets()
    {
	registerPacket(PacketStartSpell.class);
	registerPacket(PacketStopSpell.class);
	registerPacket(PacketPlayerData.class);
	registerPacket(PacketPlayerMana.class);
	registerPacket(PacketStartMagick.class);
	registerPacket(PacketPlayerClickCraft.class);
	registerPacket(PacketPlayerUnlockAll.class);
    }

    public void registerPacket(Class<? extends Packet> pkt)
    {
	addDiscriminator(discriminator++, pkt);
    }

    public void sendToServer(Packet packet)
    {
	FMLEmbeddedChannel channel = PacketHandler.channels.get(Side.CLIENT);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
	channel.writeOutbound(packet);
    }

    public void sendTo(Packet packet, EntityPlayer player)
    {
	FMLEmbeddedChannel channel = PacketHandler.channels.get(Side.SERVER);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
	channel.writeOutbound(packet);
    }

    public void sendToAllAround(Packet packet, TargetPoint point)
    {
	FMLEmbeddedChannel channel = PacketHandler.channels.get(Side.SERVER);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
	channel.writeOutbound(packet);
    }

    public void sendToDimension(Packet packet, int dimensionId)
    {
	FMLEmbeddedChannel channel = PacketHandler.channels.get(Side.SERVER);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
	channel.writeOutbound(packet);
    }

    public void sendToAll(Packet packet)
    {
	FMLEmbeddedChannel channel = PacketHandler.channels.get(Side.SERVER);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
	channel.writeOutbound(packet);
    }

    @Override
    public void encodeInto(ChannelHandlerContext context, Packet packet, ByteBuf data) throws Exception
    {
	packet.encodeInto(data);
    }

    @Override
    public void decodeInto(ChannelHandlerContext context, ByteBuf data, Packet packet)
    {
	// read the packet
	packet.decodeFrom(data);

	Side side = context.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get();
	EntityPlayer player;
	switch (side)
	{
	    case CLIENT:
		player = ModBase.proxy.getClientPlayer();
		packet.handleClientSide(player);
		break;
	    case SERVER:
		INetHandler net = context.channel().attr(NetworkRegistry.NET_HANDLER).get();
		player = ((NetHandlerPlayServer) net).playerEntity;
		packet.handleServerSide(player);
		break;
	}
    }
}