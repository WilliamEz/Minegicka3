package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.EnumMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

import com.williameze.minegicka3.ModBase;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler// extends FMLIndexedMessageToMessageCodec<Packet>
{
    private static int discriminator;
    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModBase.MODID.toLowerCase());

    public PacketHandler()
    {
	//channels = NetworkRegistry.INSTANCE.newChannel(ModBase.MODID, this);
	registerPackets();
    }

    public void registerPackets()
    {
	networkWrapper.registerMessage(PacketStartSpell.class, PacketStartSpell.class, discriminator, Side.CLIENT);
	discriminator++;
	networkWrapper.registerMessage(PacketStartSpell.class, PacketStartSpell.class, discriminator, Side.SERVER);
	discriminator++;
	networkWrapper.registerMessage(PacketStopSpell.class, PacketStopSpell.class, discriminator, Side.CLIENT);
	discriminator++;
	networkWrapper.registerMessage(PacketStopSpell.class, PacketStopSpell.class, discriminator, Side.SERVER);
	discriminator++;
	networkWrapper.registerMessage(PacketPlayerData.class, PacketPlayerData.class, discriminator, Side.CLIENT);
	discriminator++;
	networkWrapper.registerMessage(PacketPlayerMana.class, PacketPlayerMana.class, discriminator, Side.CLIENT);
	discriminator++;
	networkWrapper.registerMessage(PacketStartMagick.class, PacketStartMagick.class, discriminator, Side.CLIENT);
	discriminator++;
	networkWrapper.registerMessage(PacketStartMagick.class, PacketStartMagick.class, discriminator, Side.SERVER);
	discriminator++;
	networkWrapper.registerMessage(PacketPlayerClickCraft.class, PacketPlayerClickCraft.class, discriminator, Side.CLIENT);
	discriminator++;
	networkWrapper.registerMessage(PacketPlayerClickCraft.class, PacketPlayerClickCraft.class, discriminator, Side.SERVER);
	discriminator++;
	networkWrapper.registerMessage(PacketPlayerUnlockAll.class, PacketPlayerUnlockAll.class, discriminator, Side.CLIENT);
	discriminator++;
	networkWrapper.registerMessage(PacketPlayerUnlockAll.class, PacketPlayerUnlockAll.class, discriminator, Side.SERVER);
	discriminator++;
	networkWrapper.registerMessage(PacketActiveStaff.class, PacketActiveStaff.class, discriminator, Side.SERVER);
	discriminator++;
    }

    public void registerPacket(Class<? extends Packet> pkt)
    {
	discriminator++;
	//addDiscriminator(discriminator++, pkt);
    }

    public void sendToServer(Packet packet)
    {
	/*FMLEmbeddedChannel channel = PacketHandler.channels.get(Side.CLIENT);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
	channel.writeOutbound(packet);*/
	networkWrapper.sendToServer(packet);
    }

    public void sendTo(Packet packet, EntityPlayerMP player)
    {
	/*FMLEmbeddedChannel channel = PacketHandler.channels.get(Side.SERVER);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
	channel.writeOutbound(packet);*/
	networkWrapper.sendTo(packet, player);
    }

    public void sendToAllAround(Packet packet, TargetPoint point)
    {
	/*FMLEmbeddedChannel channel = PacketHandler.channels.get(Side.SERVER);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
	channel.writeOutbound(packet);*/
	networkWrapper.sendToAllAround(packet, point);
    }

    public void sendToDimension(Packet packet, int dimensionId)
    {
	/*FMLEmbeddedChannel channel = PacketHandler.channels.get(Side.SERVER);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
	channel.writeOutbound(packet);*/
	networkWrapper.sendToDimension(packet, dimensionId);
    }

    public void sendToAll(Packet packet)
    {
	/*FMLEmbeddedChannel channel = PacketHandler.channels.get(Side.SERVER);
	channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
	channel.writeOutbound(packet);*/
	networkWrapper.sendToAll(packet);
    }

    public void encodeInto(ChannelHandlerContext context, Packet packet, ByteBuf data) throws Exception
    {
	packet.encodeInto(data);
    }

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