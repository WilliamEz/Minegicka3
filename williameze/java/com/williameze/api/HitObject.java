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
    }
}
