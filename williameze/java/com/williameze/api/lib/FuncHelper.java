package com.williameze.api.lib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.api.math.Line;
import com.williameze.api.math.Plane;
import com.williameze.api.math.Vector;

public class FuncHelper
{
    public static List<Entity> getEntitiesWithinBoundingBoxMovement(World world, AxisAlignedBB aabb0, Vector motion, Class clazz,
	    IEntitySelector ies)
    {
	List<Entity> l = new ArrayList();
	AxisAlignedBB aabb1 = aabb0.getOffsetBoundingBox(motion.x, motion.y, motion.z);
	AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(Math.min(aabb0.minX, aabb1.minX), Math.min(aabb0.minY, aabb1.minY),
		Math.min(aabb0.minZ, aabb1.minZ), Math.max(aabb0.maxX, aabb1.maxX), Math.max(aabb0.maxY, aabb1.maxY),
		Math.max(aabb0.maxZ, aabb1.maxZ));
	l.addAll(world.selectEntitiesWithinAABB(clazz, aabb0, ies));
	l.addAll(world.selectEntitiesWithinAABB(clazz, aabb1, ies));
	List<Entity> l1 = world.selectEntitiesWithinAABB(clazz, aabb, ies);
	for (Entity e : l1)
	{
	    if (e != null && !l.contains(e))
	    {
		AxisAlignedBB eaabb = e.getBoundingBox();
		if (aabb == null) eaabb = e.boundingBox;
		try
		{
		    if (aabb == null) eaabb = e.getCollisionBox(null);
		}
		catch (NullPointerException nullp)
		{
		}
		if (aabb == null) continue;
		if (doesLineIntersectAABB(new Line(new Vector(aabb0.minX, aabb0.minY, aabb0.minZ), motion), eaabb))
		{
		    l.add(e);
		    continue;
		}
		if (doesLineIntersectAABB(new Line(new Vector(aabb0.minX, aabb0.minY, aabb0.maxZ), motion), eaabb))
		{
		    l.add(e);
		    continue;
		}
		if (doesLineIntersectAABB(new Line(new Vector(aabb0.minX, aabb0.maxY, aabb0.minZ), motion), eaabb))
		{
		    l.add(e);
		    continue;
		}
		if (doesLineIntersectAABB(new Line(new Vector(aabb0.minX, aabb0.maxY, aabb0.maxZ), motion), eaabb))
		{
		    l.add(e);
		    continue;
		}
		if (doesLineIntersectAABB(new Line(new Vector(aabb0.maxX, aabb0.minY, aabb0.minZ), motion), eaabb))
		{
		    l.add(e);
		    continue;
		}
		if (doesLineIntersectAABB(new Line(new Vector(aabb0.maxX, aabb0.minY, aabb0.maxZ), motion), eaabb))
		{
		    l.add(e);
		    continue;
		}
		if (doesLineIntersectAABB(new Line(new Vector(aabb0.maxX, aabb0.maxY, aabb0.minZ), motion), eaabb))
		{
		    l.add(e);
		    continue;
		}
		if (doesLineIntersectAABB(new Line(new Vector(aabb0.maxX, aabb0.maxY, aabb0.maxZ), motion), eaabb))
		{
		    l.add(e);
		    continue;
		}
	    }
	}
	return l;
    }

    public static boolean doesLineIntersectAABB(Line l, AxisAlignedBB aabb)
    {
	if (l == null || aabb == null) return false;
	Vector v1 = new Vector(aabb.minX, aabb.minY, aabb.minZ);
	Vector v2 = new Vector(aabb.minX, aabb.minY, aabb.maxZ);
	Vector v3 = new Vector(aabb.minX, aabb.maxY, aabb.minZ);
	Vector v4 = new Vector(aabb.minX, aabb.maxY, aabb.maxZ);
	Vector v5 = new Vector(aabb.maxX, aabb.minY, aabb.minZ);
	Vector v6 = new Vector(aabb.maxX, aabb.minY, aabb.maxZ);
	Vector v7 = new Vector(aabb.maxX, aabb.maxY, aabb.minZ);
	Vector v8 = new Vector(aabb.maxX, aabb.maxY, aabb.maxZ);

	Plane p1 = new Plane(v1, v2, v4);
	Vector in1 = p1.intersectWith(l);
	if (in1 != null && isVectorWithinBound(in1, v1, v4)) return true;

	Plane p2 = new Plane(v1, v5, v7);
	Vector in2 = p2.intersectWith(l);
	if (in2 != null && isVectorWithinBound(in2, v1, v7)) return true;

	Plane p3 = new Plane(v1, v5, v6);
	Vector in3 = p3.intersectWith(l);
	if (in3 != null && isVectorWithinBound(in3, v1, v6)) return true;

	Plane p4 = new Plane(v8, v3, v7);
	Vector in4 = p4.intersectWith(l);
	if (in4 != null && isVectorWithinBound(in4, v8, v3)) return true;

	Plane p5 = new Plane(v8, v2, v6);
	Vector in5 = p5.intersectWith(l);
	if (in5 != null && isVectorWithinBound(in5, v8, v2)) return true;

	Plane p6 = new Plane(v8, v5, v6);
	Vector in6 = p6.intersectWith(l);
	if (in6 != null && isVectorWithinBound(in6, v8, v5)) return true;

	return false;
    }

    public static boolean isVectorWithinBound(Vector tar, Vector b1, Vector b2)
    {
	return tar.x >= Math.min(b1.x, b2.x) && tar.x <= Math.max(b1.x, b2.x) && tar.y >= Math.min(b1.y, b2.y)
		&& tar.y <= Math.max(b1.y, b2.y) && tar.z >= Math.min(b1.z, b2.z) && tar.z <= Math.max(b1.z, b2.z);
    }
}
