package com.williameze.minegicka3.main.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.api.math.Vector;

public class EntityIcicle extends EntityProjectile
{
    public double headingX, headingY, headingZ = 1;

    public EntityIcicle(World par1World)
    {
	super(par1World);
	setSize(0.4F, 0.4F);
	gravity = 0.0;
	friction = 0.99;
	onGround = false;
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
	return null;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity e)
    {
	return null;
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
    public void onUpdate()
    {
	super.onUpdate();
	if (onGround)
	{
	    motionX = motionY = motionZ = 0;
	}
	if (motionX != 0 || motionY != 0 || motionZ != 0)
	{
	    Vector motion = new Vector(motionX, motionY, motionZ).normalize();
	    headingX = motion.x;
	    headingY = motion.y;
	    headingZ = motion.z;
	}
    }

    @Override
    public void collideWithBlock(int x, int y, int z)
    {
	super.collideWithBlock(x, y, z);
    }

    @Override
    public void collideWithEntity(Entity e)
    {
	super.collideWithEntity(e);
	if (isDead || e == null || e.isDead || e instanceof EntityLivingBase == false) return;
	getSpell().damageEntity(e, 1);
	setDead();
    }

}
