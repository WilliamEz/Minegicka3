package com.williameze.api.lib;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.api.math.Line;
import com.williameze.api.math.Plane;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.ModBase;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class FuncHelper
{
    public static List<Entity> getEntitiesWithinBoundingBoxMovement(World world, AxisAlignedBB aabb0, Vector motion, Class clazz,
	    IEntitySelector ies)
    {
	List<Entity> l = new ArrayList();

	if (motion.lengthSqrVector() > 260)
	{
	    double times = motion.lengthVector() / 16D;
	    Vector motionPer16 = motion.multiply(1 / times);
	    int loopTimes = (int) Math.ceil(times);
	    for (int a = 0; a < loopTimes; a++)
	    {
		AxisAlignedBB aabb = aabb0.getOffsetBoundingBox(motionPer16.x * a, motionPer16.y * a, motionPer16.z * a);
		double motionRate = a == loopTimes - 1 ? times - loopTimes + 1 : 1;
		Vector newMotion = motionPer16.copy();
		if(a==loopTimes-1) newMotion.setToLength(16D*(times-(loopTimes-1)));
		l.addAll(getEntitiesWithinBoundingBoxMovement(world, aabb, newMotion, clazz, ies));
	    }
	}
	else
	{
	    // AxisAlignedBB aabb1 = AxisAlignedBB.getBoundingBox(aabb0.minX +
	    // motion.x, aabb0.minY + motion.y, aabb0.minZ
	    // + motion.z, aabb0.maxX + motion.x, aabb0.maxY + motion.y,
	    // aabb0.maxZ + motion.z);
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
		    if (eaabb == null) eaabb = e.boundingBox;
		    try
		    {
			if (eaabb == null) eaabb = e.getCollisionBox(null);
		    }
		    catch (NullPointerException nullp)
		    {
		    }
		    if (eaabb == null) continue;
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

    public static Vector vectorToEntity(Entity e1, Entity e2)
    {
	return new Vector(e2.posX - e1.posX, e2.posY - e1.posY, e2.posZ - e1.posZ);
    }

    public static Vector vectorToCenterEntity(Entity e1, Entity e2)
    {
	return getCenter(e2).subtract(getCenter(e1));
    }

    public static Vector getCenter(Entity e1)
    {
	AxisAlignedBB aabb1 = e1.getBoundingBox();
	if (aabb1 == null) aabb1 = e1.boundingBox;
	if (aabb1 == null) aabb1 = AxisAlignedBB.getBoundingBox(e1.posX - e1.width / 2, e1.posY, e1.posZ + e1.width / 2, e1.posX
		+ e1.width / 2, e1.posY + e1.height, e1.posZ + e1.width / 2);

	return new Vector((aabb1.maxX + aabb1.minX) / 2, (aabb1.maxY + aabb1.minY) / 2, (aabb1.maxZ + aabb1.minZ) / 2);
    }

    public static Entity getEntityClosestTo(double x, double y, double z, List<Entity> l)
    {
	double dsqr = -1;
	Entity e1 = null;
	for (Entity e : l)
	{
	    double de = e.getDistanceSq(x, y, z);
	    if (dsqr == -1 || de < dsqr)
	    {
		e1 = e;
	    }
	}
	return e1;
    }
}
