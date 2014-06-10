package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;

public abstract class Packet
{

    public abstract void encodeInto(ByteBuf buffer);

    public abstract void decodeFrom(ByteBuf buffer);

    public abstract void handleClientSide(Object ctx);

    public abstract void handleServerSide(Object ctx);
}