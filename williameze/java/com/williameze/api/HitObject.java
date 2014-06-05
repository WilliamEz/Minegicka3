package com.williameze.api;

import net.minecraft.entity.Entity;

import com.williameze.api.math.Vector;

public class HitObject
{
    public static enum HitType
    {
	Block, Entity, Nothing;
    }

    public HitType hitType = HitType.Nothing;
    public Vector hitPosition;
    public int blockX, blockY, blockZ;
    public int sideHit;
    public Entity hitEntity;

    public HitObject()
    {
    }

    public HitObject(Vector dest)
    {
	this();
	hitPosition = dest;
    }

    public HitObject(Entity e, Vector hit)
    {
	this();
	hitType = HitType.Entity;
	hitEntity = e;
	hitPosition = hit;
    }

    public HitObject(int x, int y, int z, Vector hit)
    {
	this();
	hitType = HitType.Block;
	blockX = x;
	blockY = y;
	blockZ = z;
	hitPosition = hit;
	calculateBlockSideHit();
    }

    public void calculateBlockSideHit()
    {
	if (hitPosition == null) return;
	double difX = hitPosition.x - (blockX + 0.5);
	double difY = hitPosition.y - (blockY + 0.5);
	double difZ = hitPosition.z - (blockZ + 0.5);
	if (Math.abs(difX) >= Math.abs(difY) && Math.abs(difX) >= Math.abs(difZ))
	{
	    if (difX >= 0) sideHit = 5;
	    else sideHit = 4;
	}
	else if (Math.abs(difY) >= Math.abs(difX) && Math.abs(difY) >= Math.abs(difZ))
	{
	    if (difY >= 0) sideHit = 1;
	    else sideHit = 0;
	}
	else
	{
	    if (difZ >= 0) sideHit = 3;
	    else sideHit = 2;
	}
    }
}
