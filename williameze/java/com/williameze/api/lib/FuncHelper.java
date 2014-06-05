package com.williameze.api.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.api.HitObject;
import com.williameze.api.HitObject.HitType;
import com.williameze.api.math.IntVector;
import com.williameze.api.math.Line;
import com.williameze.api.math.Plane;
import com.williameze.api.math.Vector;
import com.williameze.api.selectors.BlockSelector;

public class FuncHelper
{
    public static HitObject rayTrace(World world, Vector start, Vector end, BlockSelector blockSelector, IEntitySelector selector,
	    List<Entity> excluding)
    {
	Vector path = end.subtract(start);
	if (path.lengthSqrVector() > 68)
	{
	    Vector cutPath = path.copy();
	    cutPath.setToLength(8);
	    double loops = Math.sqrt(path.lengthSqrVector() / cutPath.lengthSqrVector());
	    for (int a = 0; a < loops; a++)
	    {
		double passed = Math.max(0, a);
		double now = Math.min(loops - a, 1);
		Vector newStart = start.add(cutPath.multiply(passed));
		HitObject hit = rayTrace(world, newStart, newStart.add(cutPath.multiply(now)), blockSelector, selector, excluding);
		if (hit != null && hit.hitType != HitType.Nothing) return hit;
	    }
	    return new HitObject(end);
	}

	Map<Vector, IntVector> intersectBlocks = new HashMap();
	Map<Vector, Entity> intersectEntities = new HashMap();

	int startX = (int) Math.floor(start.x);
	int startY = (int) Math.floor(start.y);
	int startZ = (int) Math.floor(start.z);
	int endX = (int) Math.floor(end.x);
	int endY = (int) Math.floor(end.y);
	int endZ = (int) Math.floor(end.z);
	int minX = Math.min(startX, endX);
	int minY = Math.min(startY, endY);
	int minZ = Math.min(startZ, endZ);
	int maxX = Math.max(startX, endX);
	int maxY = Math.max(startY, endY);
	int maxZ = Math.max(startZ, endZ);
	for (int x = minX; x <= maxX; x++)
	{
	    for (int y = minY; y <= maxY; y++)
	    {
		for (int z = minZ; z <= maxZ; z++)
		{
		    Block b = world.getBlock(x, y, z);
		    if (!b.isAir(world, x, y, z) && (blockSelector == null || blockSelector.acceptBlock(world, x, y, z)))
		    {
			AxisAlignedBB aabb = b.getCollisionBoundingBoxFromPool(world, x, y, z);
			Vector intersect = getIntersectionPoint(start, path, aabb);
			if (intersect != null) intersectBlocks.put(intersect, new IntVector(x, y, z));
		    }
		}
	    }
	}
	List<Entity> entities = world.selectEntitiesWithinAABB(
		Entity.class,
		AxisAlignedBB.getBoundingBox(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.min(start.z, end.z),
			Math.max(start.x, end.x), Math.max(start.y, end.y), Math.max(start.z, end.z)), selector);
	if (excluding != null) entities.removeAll(excluding);
	for (Entity e : entities)
	{
	    AxisAlignedBB aabb = e.getBoundingBox();
	    if (aabb == null) aabb = e.boundingBox;
	    if (aabb != null)
	    {
		Vector intersect = getIntersectionPoint(start, path, aabb);
		if (intersect != null) intersectEntities.put(intersect, e);
	    }
	}

	Vector hitBlock = null;
	IntVector closestBlock = null;
	double distSqrBL = -1;
	for (Vector v : intersectBlocks.keySet())
	{
	    double sqr = v.subtract(start).lengthSqrVector();
	    if (distSqrBL == -1 || sqr <= distSqrBL)
	    {
		distSqrBL = sqr;
		hitBlock = v;
		closestBlock = intersectBlocks.get(v);
	    }
	}
	Vector hitEntity = null;
	Entity closestEntity = null;
	double distSqrE = -1;
	for (Vector v : intersectEntities.keySet())
	{
	    double sqr = v.subtract(start).lengthSqrVector();
	    if (distSqrE == -1 || sqr <= distSqrE)
	    {
		distSqrE = sqr;
		hitEntity = v;
		closestEntity = intersectEntities.get(v);
	    }
	}
	HitObject hit = new HitObject(end);
	if (distSqrE >= 0 && distSqrBL >= 0)
	{
	    if (distSqrE <= distSqrBL)
	    {
		hit = new HitObject(closestEntity, hitEntity);
		hit.blockX = closestBlock.x;
		hit.blockY = closestBlock.y;
		hit.blockZ = closestBlock.z;
	    }
	    else
	    {
		hit = new HitObject(closestBlock.x, closestBlock.y, closestBlock.z, hitBlock);
		hit.hitEntity = closestEntity;
	    }
	}
	else
	{
	    if (distSqrE >= 0)
	    {
		hit = new HitObject(closestEntity, hitEntity);
	    }
	    if (distSqrBL >= 0)
	    {
		hit = new HitObject(closestBlock.x, closestBlock.y, closestBlock.z, hitBlock);
	    }
	}
	return hit;
    }

    public static List<Entity> getEntitiesWithinBoundingBoxMovement(World world, AxisAlignedBB aabb0, Vector motion, Class clazz,
	    IEntitySelector ies)
    {
	List<Entity> l = new ArrayList();

	double maxMotionLength = 16D;
	if (motion.lengthSqrVector() > maxMotionLength * maxMotionLength + 8)
	{
	    double times = motion.lengthVector() / maxMotionLength;
	    int loopTimes = (int) Math.ceil(times);
	    Vector motionPer16 = motion.multiply(1D / loopTimes);
	    for (int a = 0; a < loopTimes; a++)
	    {
		AxisAlignedBB aabb = aabb0.getOffsetBoundingBox(motionPer16.x * a, motionPer16.y * a, motionPer16.z * a);
		double motionRate = a == loopTimes - 1 ? times - loopTimes + 1 : 1;
		Vector newMotion = motionPer16.copy();
		if (a == loopTimes - 1) newMotion.setToLength(maxMotionLength * (times - (loopTimes - 1)));
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

    public static Vector getIntersectionPoint(Vector origin, Vector direction, AxisAlignedBB aabb)
    {
	if (aabb == null) return null;
	Line l = new Line(origin, direction);
	List<Vector> facesIntersect = new ArrayList();
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
	if (in1 != null && isVectorWithinBound(in1, v1, v4) && in1.subtract(origin).dotProduct(direction) > 0) facesIntersect.add(in1);

	Plane p2 = new Plane(v1, v5, v7);
	Vector in2 = p2.intersectWith(l);
	if (in2 != null && isVectorWithinBound(in2, v1, v7) && in2.subtract(origin).dotProduct(direction) > 0) facesIntersect.add(in2);

	Plane p3 = new Plane(v1, v5, v6);
	Vector in3 = p3.intersectWith(l);
	if (in3 != null && isVectorWithinBound(in3, v1, v6) && in3.subtract(origin).dotProduct(direction) > 0) facesIntersect.add(in3);

	Plane p4 = new Plane(v8, v3, v7);
	Vector in4 = p4.intersectWith(l);
	if (in4 != null && isVectorWithinBound(in4, v8, v3) && in4.subtract(origin).dotProduct(direction) > 0) facesIntersect.add(in4);

	Plane p5 = new Plane(v8, v2, v6);
	Vector in5 = p5.intersectWith(l);
	if (in5 != null && isVectorWithinBound(in5, v8, v2) && in5.subtract(origin).dotProduct(direction) > 0) facesIntersect.add(in5);

	Plane p6 = new Plane(v8, v5, v6);
	Vector in6 = p6.intersectWith(l);
	if (in6 != null && isVectorWithinBound(in6, v8, v5) && in6.subtract(origin).dotProduct(direction) > 0) facesIntersect.add(in6);

	double distSqr = -1;
	Vector selected = null;
	for (Vector v : facesIntersect)
	{
	    double sqr = v.subtract(origin).lengthSqrVector();
	    if (distSqr == -1 || sqr < distSqr)
	    {
		distSqr = sqr;
		selected = v;
	    }
	}

	return selected;
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
	if (aabb1 == null) aabb1 = AxisAlignedBB.getBoundingBox(e1.posX - e1.width / 2, e1.posY, e1.posZ + e1.width / 2, e1.posX + e1.width
		/ 2, e1.posY + e1.height, e1.posZ + e1.width / 2);

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
