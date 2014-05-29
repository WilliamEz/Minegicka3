package com.williameze.minegicka3.main.entities;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.spells.Spell;
import com.williameze.minegicka3.main.spells.Spell.CastType;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityMine extends Entity implements IEntityAdditionalSpawnData, IEntityNullifiable
{
    public Spell spell = Spell.none;
    public boolean startedSpell = false;

    public EntityMine(World par1World)
    {
	super(par1World);
	renderDistanceWeight = Values.renderDistance;
	setSize(0.5f, 0.5f);
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
    public boolean canBeCollidedWith()
    {
	return false;
    }

    @Override
    public boolean canBePushed()
    {
	return false;
    }

    @Override
    public boolean isWet()
    {
	return false;
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();
	motionY -= 0.02;
	moveEntity(motionX, motionY, motionZ);
	if (!worldObj.isRemote)
	{
	    List<Entity> l = worldObj.getEntitiesWithinAABB(Entity.class, boundingBox);
	    l.remove(this);
	    for (Entity e : l)
	    {
		if (e instanceof EntityFX == false)
		{
		    novaHere();
		    return;
		}
	    }
	}
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
	if (par2 > 0)
	{
	    novaHere();
	}
	return true;
    }

    public void novaHere()
    {
	if (!worldObj.isRemote && !isDead)
	{
	    Spell s = new Spell(spell.elements, spell.dimensionID, getPersistentID(), CastType.Area, spell.additionalData);
	    EntityBeamArea beamA = new EntityBeamArea(worldObj);
	    beamA.spell = s;
	    beamA.setPosition(posX, posY + 1, posZ);
	    worldObj.spawnEntityInWorld(beamA);
	    setDead();
	}
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
	spell = Spell.createFromNBT(var1.getCompoundTag("Spell"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
	var1.setTag("Spell", spell.writeToNBT());
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
	try
	{
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
