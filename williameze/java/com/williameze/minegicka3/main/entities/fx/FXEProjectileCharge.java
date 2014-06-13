package com.williameze.minegicka3.main.entities.fx;

import java.awt.Color;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.williameze.api.math.Vector;

public class FXEProjectileCharge extends Entity
{
    public Color color;
    public Vector destination;
    public double pullToDest;
    public double alpha;

    public FXEProjectileCharge(World par1World)
    {
	super(par1World);
	setSize(0.05F, 0.05F);
    }

    @Override
    public boolean isBurning()
    {
	return false;
    }
    
    @Override
    public void setInPortal()
    {
    }
    
    @Override
    public void setPosition(double par1, double par3, double par5)
    {
	super.setPosition(par1, par3, par5);
	boundingBox.setBounds(par1 - width / 2, par3 - height / 2, par5 - width / 2, par1 + width / 2, par3 + height / 2, par5 + width / 2);
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();
	this.prevPosX = this.posX;
	this.prevPosY = this.posY;
	this.prevPosZ = this.posZ;

	if (destination != null)
	{
	    double dist = getDistance(destination.x, destination.y, destination.z);
	    if (dist == 0)
	    {
		setDead();
		return;
	    }
	    Vector v = destination.subtract(new Vector(posX, posY, posZ));
	    v.setToLength(pullToDest / dist);
	    motionX += v.x;
	    motionY += v.y;
	    motionZ += v.z;
	    if (dist * dist <= motionX * motionX + motionY * motionY + motionZ * motionZ)
	    {
		setDead();
		return;
	    }
	}
	moveEntity(motionX, motionY, motionZ);
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
    }

}
