package com.williameze.minegicka3.main.entities;

import io.netty.buffer.ByteBuf;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.IntVector;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.spells.ESelectorDefault;
import com.williameze.minegicka3.main.spells.Spell;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityHomingLightning extends Entity implements IEntityAdditionalSpawnData
{
    private Spell spell = Spell.none;
    public double targetX, targetY, targetZ;

    public EntityHomingLightning(World par1World)
    {
	super(par1World);
	setSize(0.06F, 0.06F);
	noClip = true;
	renderDistanceWeight = Values.renderDistance;
    }

    @Override
    protected void entityInit()
    {
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
    protected boolean canTriggerWalking()
    {
	return false;
    }

    @Override
    protected void fall(float par1)
    {
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();
	this.prevPosX = this.posX;
	this.prevPosY = this.posY;
	this.prevPosZ = this.posZ;

	Vector v = new Vector(targetX - posX, targetY - posY, targetZ - posZ).normalize();
	motionX *= 0.96;
	motionY *= 0.96;
	motionZ *= 0.96;
	motionX += v.x * 0.2;
	motionY += v.y * 0.2;
	motionZ += v.z * 0.2;
	moveEntity(motionX, motionY, motionZ);
	double distSq = getDistanceSq(targetX, targetY, targetZ);
	if (distSq < 4 || distSq < motionX * motionX + motionY * motionY + motionZ * motionZ)
	{
	    EntityLightningBolt lightning = new EntityLightningBolt(worldObj, targetX, targetY, targetZ);
	    worldObj.spawnEntityInWorld(lightning);
	    setDead();
	}
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
	targetX = var1.getDouble("Target X");
	targetY = var1.getDouble("Target Y");
	targetZ = var1.getDouble("Target Z");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
	var1.setDouble("Target X", targetX);
	var1.setDouble("Target Y", targetY);
	var1.setDouble("Target Z", targetZ);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
	buffer.writeDouble(targetX);
	buffer.writeDouble(targetY);
	buffer.writeDouble(targetZ);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
	targetX = additionalData.readDouble();
	targetY = additionalData.readDouble();
	targetZ = additionalData.readDouble();
    }

}
