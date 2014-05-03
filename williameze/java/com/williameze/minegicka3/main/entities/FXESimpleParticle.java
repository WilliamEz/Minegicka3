package com.williameze.minegicka3.main.entities;

import java.awt.Color;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.Values;

public class FXESimpleParticle extends Entity
{
    public Color color;
    public double alpha;
    /** 0:cube, 1:octahedron, 2:sphere **/
    public int renderType;
    public double friction;
    public double gravity;
    public int life;
    public int maxLife;

    public FXESimpleParticle(World par1World)
    {
	super(par1World);
	setSize(0.1F, 0.1F);
	gravity = 0;
	friction = 1;
	renderDistanceWeight = Values.renderDistance;
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
	life--;

	this.prevPosX = this.posX;
	this.prevPosY = this.posY;
	this.prevPosZ = this.posZ;

	motionX *= friction;
	motionY -= gravity;
	motionY *= friction;
	motionZ *= friction;
	moveEntity(motionX, motionY, motionZ);

	if (life <= 0) setDead();
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
