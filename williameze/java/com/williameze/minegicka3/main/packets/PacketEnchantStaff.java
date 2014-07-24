package com.williameze.minegicka3.main.packets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import com.williameze.minegicka3.functional.CoreBridge;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.mechanics.Enchant;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class PacketEnchantStaff extends Packet<PacketEnchantStaff>
{
    public EntityPlayer p;
    public int staffInvIndex;
    public List<ItemStack> entries = new ArrayList();

    public String name;
    public int dim;

    public PacketEnchantStaff()
    {
    }

    public PacketEnchantStaff(EntityPlayer pd, int i, List<ItemStack> es)
    {
	p = pd;
	staffInvIndex = i;
	entries = es;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
	try
	{
	    NBTTagCompound tag = new NBTTagCompound();
	    tag.setString("Name", p.getGameProfile().getName());
	    tag.setInteger("Dim", p.worldObj.provider.dimensionId);
	    tag.setInteger("Index", staffInvIndex);
	    tag.setInteger("Count", entries.size());
	    for (int a = 0; a < entries.size(); a++)
	    {
		NBTTagCompound is = new NBTTagCompound();
		entries.get(a).writeToNBT(is);
		tag.setTag("IS" + a, is);
	    }
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
    public void decodeFrom(ByteBuf buffer)
    {
	try
	{
	    entries.clear();
	    byte[] b = new byte[buffer.readInt()];
	    buffer.readBytes(b);
	    NBTTagCompound tag = CompressedStreamTools.func_152457_a(b, Values.nbtSizeTracker);
	    name = tag.getString("Name");
	    dim = tag.getInteger("Dim");
	    staffInvIndex = tag.getInteger("Index");
	    int count = tag.getInteger("Count");
	    for (int a = 0; a < count; a++)
	    {
		NBTTagCompound is = tag.getCompoundTag("IS" + a);
		ItemStack item = ItemStack.loadItemStackFromNBT(is);
		entries.add(item);
	    }
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
	p = (EntityPlayer) CoreBridge.instance().getEntityFromArgs(null, dim, name, true, false, true);
	Enchant.enchantAndConsume(p, entries, staffInvIndex);
    }
}
