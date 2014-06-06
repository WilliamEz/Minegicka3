package com.williameze.minegicka3.main.entities;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.williameze.api.math.Vector;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.spells.Spell;
import com.williameze.minegicka3.main.spells.Spell.SpellType;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityStorm extends Entity implements IEntityAdditionalSpawnData, IEntityNullifiable
{
    public Spell spell = Spell.none;
    public int maxTick;
    public boolean startedSpell = false;

    public EntityStorm(World par1World)
    {
	super(par1World);
	renderDistanceWeight = Values.renderDistance;
	setSize(0.1f, 0.1f);
    }

    @Override
    public void setInPortal()
    {
    }

    @Override
    public boolean isBurning()
    {
	return false;
    }
    
    @Override
    public boolean isInRangeToRenderDist(double par1)
    {
	return par1 < renderDistanceWeight * renderDistanceWeight;
    }

    @Override
    public boolean isEntityInvulnerable()
    {
	return true;
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public boolean isWet()
    {
	return false;
    }

    @Override
    public Vec3 getLookVec()
    {
	return new Vector(0, -1, 0).vec3();
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();
	if (spell.getCaster() == null)
	{
	    spell.setCaster(this);
	}
	if (ticksExisted > maxTick)
	{
	    setDead();
	    spell.toBeInvalidated = true;
	}

	if (!(spell.spellType == SpellType.Spray && (ticksExisted + hashCode()) % 2 == 1)) spell.updateSpell();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
	spell = Spell.createFromNBT(var1.getCompoundTag("Spell"));
	maxTick = var1.getInteger("Max Tick");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
	var1.setTag("Spell", spell.writeToNBT());
	var1.setInteger("Max Tick", maxTick);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
	try
	{
	    buffer.writeInt(maxTick);
	    byte[] b = CompressedStreamTools.compress(spell.writeToNBT());
	    buffer.writeInt(b.length);
	    buffer.writeBytes(b);
	    buffer.writeLong(entityUniqueID.getMostSignificantBits());
	    buffer.writeLong(entityUniqueID.getLeastSignificantBits());
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
	try
	{
	    maxTick = additionalData.readInt();
	    byte[] b = new byte[additionalData.readInt()];
	    additionalData.readBytes(b);
	    NBTTagCompound tag = CompressedStreamTools.decompress(b);
	    spell = Spell.createFromNBT(tag);
	    entityUniqueID = new UUID(additionalData.readLong(), additionalData.readLong());
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

}
