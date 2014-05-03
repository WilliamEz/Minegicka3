package com.williameze.minegicka3.main.entities;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.IntVector;
import com.williameze.api.math.Line;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.SpellDamageModifier;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.objects.TileEntityShield;
import com.williameze.minegicka3.main.spells.ESelectorBeamArea;
import com.williameze.minegicka3.main.spells.Spell;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityBeamArea extends Entity implements IEntityAdditionalSpawnData
{
    public Spell spell = Spell.none;
    public boolean searched;
    public List<Entity> targets = new ArrayList();
    public SpellDamageModifier damMod = SpellDamageModifier.defau;

    public EntityBeamArea(World par1World)
    {
	super(par1World);
	renderDistanceWeight = Values.renderDistance;
	setSize(4F, 20F);
	searched = false;
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
    public void moveEntity(double par1, double par3, double par5)
    {
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();
	motionX = motionY = motionZ = 0;
	if (spell == null || ticksExisted > maxTick())
	{
	    setDead();
	    return;
	}

	Entity e = spell.getCaster();
	if (!searched)
	{
	    searched = true;
	    targets.addAll(worldObj.selectEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ)
		    .expand(maxRange(), 2, maxRange()), new ESelectorBeamArea(spell)));
	}
	if (!targets.isEmpty())
	{
	    List<Entity> toRemove = new ArrayList();
	    double maxAtkDistSqr = Math.pow((ticksExisted + 1D) / maxTick() * maxRange(), 2);
	    for (Entity ent : targets)
	    {
		if (ent.getDistanceSqToEntity(this) <= maxAtkDistSqr)
		{
		    explosionReachEntity(ent);
		    toRemove.add(ent);
		}
	    }
	    targets.removeAll(toRemove);
	}
    }

    public void explosionReachEntity(Entity e)
    {
	Vector pos = new Vector(posX, posY, posZ);
	AxisAlignedBB aabb = e.boundingBox;
	if (aabb == null)
	{
	    if (reachAntiBeamBlockOnTheWay(pos, new Vector(e.posX, e.posY, e.posZ).subtract(pos))) return;
	}
	else
	{
	    if (reachAntiBeamBlockOnTheWay(pos, FuncHelper.vectorToCenterEntity(this, e)))
	    {
		if (reachAntiBeamBlockOnTheWay(pos, new Vector(aabb.minX, aabb.minY, aabb.minZ).subtract(pos)))
		{
		    if (reachAntiBeamBlockOnTheWay(pos, new Vector(aabb.minX, aabb.minY, aabb.maxZ).subtract(pos)))
		    {
			if (reachAntiBeamBlockOnTheWay(pos, new Vector(aabb.minX, aabb.maxY, aabb.minZ).subtract(pos)))
			{
			    if (reachAntiBeamBlockOnTheWay(pos, new Vector(aabb.minX, aabb.maxY, aabb.maxZ).subtract(pos)))
			    {
				if (reachAntiBeamBlockOnTheWay(pos, new Vector(aabb.maxX, aabb.minY, aabb.minZ).subtract(pos)))
				{
				    if (reachAntiBeamBlockOnTheWay(pos, new Vector(aabb.maxX, aabb.minY, aabb.maxZ).subtract(pos)))
				    {
					if (reachAntiBeamBlockOnTheWay(pos, new Vector(aabb.maxX, aabb.maxY, aabb.minZ).subtract(pos)))
					{
					    if (reachAntiBeamBlockOnTheWay(pos, new Vector(aabb.maxX, aabb.maxY, aabb.maxZ).subtract(pos)))
					    {
						return;
					    }
					}
				    }
				}
			    }
			}
		    }
		}
	    }
	}
	spell.damageEntity(e, 0, damMod);
    }

    public boolean reachAntiBeamBlockOnTheWay(Vector pos, Vector toward)
    {
	double lengthSqr = toward.lengthSqrVector();
	double maxLength = 6;
	if (lengthSqr > maxLength * maxLength + maxLength / 2)
	{
	    double times = Math.sqrt(lengthSqr) / maxLength;
	    int loopTimes = (int) Math.ceil(times);
	    Vector motionPer = toward.multiply(1 / times);
	    for (int a = 0; a < loopTimes; a++)
	    {
		Vector mot = motionPer.copy();
		if (a == loopTimes - 1)
		{
		    double newLength = maxLength * (times - (loopTimes - 1));
		    mot.setToLength(newLength);
		}
		boolean has = reachAntiBeamBlockOnTheWay(pos.add(motionPer.multiply(a)), mot);
		if (has) return true;
	    }
	    return false;
	}
	else
	{
	    int minX = (int) Math.floor(pos.x);
	    int minY = (int) Math.floor(pos.y);
	    int minZ = (int) Math.floor(pos.z);
	    int maxX = (int) Math.floor(pos.x + toward.x);
	    int maxY = (int) Math.floor(pos.y + toward.x);
	    int maxZ = (int) Math.floor(pos.z + toward.x);

	    for (int x = minX; x <= maxX; x++)
	    {
		for (int y = minY; y <= maxY; y++)
		{
		    for (int z = minZ; z <= maxZ; z++)
		    {
			Block b = worldObj.getBlock(x, y, z);
			Material m = b.getMaterial();
			if (b == ModBase.shieldBlock || m.isSolid() && !m.isReplaceable() && !m.isLiquid() && b.isCollidable())
			{
			    AxisAlignedBB aabb = b.getCollisionBoundingBoxFromPool(worldObj, x, y, z);
			    if (FuncHelper.doesLineIntersectAABB(new Line(pos, toward), aabb))
			    {
				return true;
			    }
			}
		    }
		}
	    }
	    return false;
	}
    }

    public double maxRange()
    {
	return (spell.elements.size() * 4D + 12D) * (spell.hasElement(Element.Arcane) ? damMod.arcaneMod : damMod.lifeMod);
    }

    public int maxTick()
    {
	return (int) Math.max(Math.round(maxRange() / 16 * 10), 10);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
	setDead();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
	try
	{
	    byte[] b = CompressedStreamTools.compress(spell.writeToNBT());
	    buffer.writeInt(b.length);
	    buffer.writeBytes(b);
	    byte[] b1 = damMod.toString().getBytes();
	    buffer.writeInt(b1.length);
	    buffer.writeBytes(b1);
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

	    byte[] b1 = new byte[additionalData.readInt()];
	    additionalData.readBytes(b1);
	    damMod = new SpellDamageModifier(new String(b1));
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

}
