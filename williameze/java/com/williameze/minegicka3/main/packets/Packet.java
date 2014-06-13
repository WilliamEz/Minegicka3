package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public abstract class Packet<REQ extends Packet> implements IMessage, IMessageHandler<REQ, IMessage>
{
    @Override
    public void toBytes(ByteBuf buf)
    {
	encodeInto(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
	decodeFrom(buf);
	if(FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER) handleServerSide(null);
	else handleClientSide(null);
    }

    @Override
    public IMessage onMessage(Packet message, MessageContext ctx)
    {
	return null;
    }

    public abstract void encodeInto(ByteBuf buffer);

    public abstract void decodeFrom(ByteBuf buffer);

    public abstract void handleClientSide(Object ctx);

    public abstract void handleServerSide(Object ctx);
}