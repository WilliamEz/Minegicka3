package com.williameze.minegicka3.main.objects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.williameze.api.lib.NoiseGen1D;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.spells.Spell;

public class TileEntityShield extends TileEntity
{
    public static class ShieldParticleData
    {
	public static Color c1 = new Color(255, 255, 140);
	public static Color c2 = new Color(255, 255, 180);

	public Vector pos;
	public int xoryorz;
	public Color color;
	public NoiseGen1D alphaNoise;

	public ShieldParticleData(TileEntityShield shield)
	{
	    Random rnd = new Random();
	    pos = new Vector(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble());
	    xoryorz = rnd.nextInt(3);
	    color = rnd.nextBoolean() ? c1 : c2;
	    alphaNoise = new NoiseGen1D(hashCode(), 200);
	    alphaNoise.setCap(0, 255);
	    alphaNoise.setDifs(12, 2);
	    alphaNoise.generate();
	    alphaNoise.smooth(1, 4);
	}
    }

    private Spell spell = Spell.none;
    public double life;
    public List<ShieldParticleData> particles = new ArrayList();

    public TileEntityShield()
    {
	life = 40;
    }

    public Spell getSpell()
    {
	return spell;
    }

    public void setSpell(Spell s)
    {
	spell = s;
	life = Math.max((int) (200D * spell.getPower()), life);
    }

    public void blockClicked(EntityPlayer p)
    {
	double damage = 2;
	if (p.getHeldItem() != null && p.getHeldItem().getItem() instanceof ItemSword)
	{
	    damage += ((ItemSword) p.getHeldItem().getItem()).func_150931_i();
	}
	damage *= 2.5;
	damageShield(damage);
    }

    public void damageShield(double dmg)
    {
	life -= dmg;
    }

    @Override
    public boolean canUpdate()
    {
	return true;
    }

    @Override
    public void updateEntity()
    {
	super.updateEntity();
	life--;
	if (life <= 0 && !worldObj.isRemote)
	{
	    worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}
    }

    @Override
    public void readFromNBT(NBTTagCompound var1)
    {
	super.readFromNBT(var1);
	setSpell(Spell.createFromNBT(var1.getCompoundTag("Spell")));
	life = var1.getDouble("Life");
    }

    @Override
    public void writeToNBT(NBTTagCompound var1)
    {
	super.writeToNBT(var1);
	var1.setTag("Spell", spell.writeToNBT());
	var1.setDouble("Life", life);
    }

    @Override
    public Packet getDescriptionPacket()
    {
	NBTTagCompound tag = new NBTTagCompound();
	writeToNBT(tag);
	return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
	super.onDataPacket(net, pkt);
	readFromNBT(pkt.func_148857_g());
    }
}
