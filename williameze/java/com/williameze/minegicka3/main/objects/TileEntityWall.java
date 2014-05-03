package com.williameze.minegicka3.main.objects;

import java.awt.Color;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.entities.FXESimpleParticle;
import com.williameze.minegicka3.main.spells.Spell;

public class TileEntityWall extends TileEntity
{
    public Random rnd = new Random();
    private Spell spell;
    public double life;

    public TileEntityWall()
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
	life = (int) Math.max(spell.countElement(Element.Earth) * 150 + Math.sqrt(spell.countElement(Element.Ice)) * 15, life);
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
	getSpell().updateRecentAffected();
	if (life <= 0)
	{
	    worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 0, 0);
	    if (!worldObj.isRemote)
	    {
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	    }
	}
    }

    @Override
    public boolean receiveClientEvent(int i, int j)
    {
	if (i == 0 && j == 0)
	{
	    onBlockDestroyed();
	    return true;
	}
	return super.receiveClientEvent(i, j);
    }

    public void onBlockDestroyed()
    {
	if (worldObj.isRemote)
	{
	    for (int a = 0; a < 1 + rnd.nextInt(2); a++)
	    {
		FXESimpleParticle pa = new FXESimpleParticle(worldObj);
		pa.setPosition(xCoord + rnd.nextDouble(), yCoord + rnd.nextDouble(), zCoord + rnd.nextDouble());
		pa.renderType = 0;
		pa.noClip = true;
		pa.motionX = (rnd.nextDouble() - 0.5) * 0.3;
		pa.motionY = (rnd.nextDouble() - 0.5) * 0.3;
		pa.motionZ = (rnd.nextDouble() - 0.5) * 0.3;
		pa.friction = 0.99;
		pa.color = getSpell().elements.get((hashCode() + a) % getSpell().countElements()).getColor();
		pa.alpha = 0.8;
		pa.life = pa.maxLife = 60;
		worldObj.spawnEntityInWorld(pa);
	    }
	}
    }

    public Color getMainColor()
    {
	Color c = Element.Earth.getColor();
	boolean hasEarth = getSpell().hasElement(Element.Earth);
	boolean hasIce = getSpell().hasElement(Element.Ice);
	if (hasEarth) c = Element.Earth.getColor();
	if (hasIce) c = Element.Ice.getColor();
	if (hasEarth && hasIce)
	{
	    c = (hashCode() % 2 == 0 ? Element.Earth : Element.Ice).getColor();
	}
	return c;
    }

    public void entityCollide(Entity e)
    {
	if (getSpell().countElements() > getSpell().countElements(Element.Earth))
	{
	    getSpell().damageEntity(e, (int) ((double) 80 / getSpell().getAtkSpeed()));
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
