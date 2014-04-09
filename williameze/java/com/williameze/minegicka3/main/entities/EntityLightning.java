package com.williameze.minegicka3.main.entities;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.google.common.collect.Sets;
import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.MathHelper;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.spells.Spell;
import com.williameze.minegicka3.main.spells.Spell.CastType;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityLightning extends Entity implements IEntityAdditionalSpawnData
{
    public static double minCosConeSeek = Math.pow(3, -0.5D) / 2D;
    public Spell spell;
    public Map<Entity, List<Entity>> originAndChainedMap = new HashMap();

    public EntityLightning(World par1World)
    {
	super(par1World);
	renderDistanceWeight = 16;
	setSize(0.01F, 0.01F);
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();
	if (spell == null || spell.toBeStopped)
	{
	    setDead();
	    return;
	}
	if (!spell.toBeStopped)
	{
	    if (worldObj.isRemote)
	    {
		if (!ModBase.proxy.getCoreClient().currentWorldSpells.contains(spell))
		{
		    setDead();
		    return;
		}
	    }
	    else
	    {
		if (!ModBase.proxy.getCoreServer().worldsSpellsList.get(worldObj).contains(spell))
		{
		    setDead();
		    return;
		}
	    }
	}
	Entity e = spell.getCaster();
	if (e == null)
	{
	    setDead();
	    return;
	}
	setPosition(e.posX, e.posY + e.getEyeHeight(), e.posZ);
	if (e.getLookVec() != null)
	{
	    posX += e.getLookVec().xCoord * 0.75;
	    posY += e.getLookVec().yCoord * 0.75;
	    posZ += e.getLookVec().zCoord * 0.75;
	    setPosition(posX, posY, posZ);
	}
	seekAndAffectTargets();
    }

    public double getSeekRadius(int level)
    {
	if (spell == null)
	{
	    return 0;
	}
	int lig = spell.countElement(Element.Lightning);
	if (level >= 2 + lig) return 0;
	return 5D * Math.pow(lig, 0.3) * spell.getStaffTag().getDouble("Power") / Math.pow(level + 1, 0.5);
    }

    public void seekAndAffectTargets()
    {
	originAndChainedMap.clear();
	seekTargets(this, 0, new Vector(spell.getCaster().getLookVec()));
    }

    public void seekTargets(Entity start, int level, Vector toward)
    {
	List<Entity> l = new ArrayList();
	originAndChainedMap.put(start, l);
	double radius = getSeekRadius(level);
	if (radius > 0)
	{
	    List<Entity> all = worldObj.getEntitiesWithinAABBExcludingEntity(start, getBBFromMidPoint(start, radius));
	    for (Entity e : all)
	    {
		if (!originAndChainedMap.containsKey(e) && e instanceof EntityLivingBase && e != spell.getCaster())
		{
		    if (e.getDistanceSqToEntity(start) <= radius * radius)
		    {
			Vector newToward = FuncHelper.vectorToEntity(start, e);
			if (spell.castType == CastType.Area
				|| MathHelper.getCosAngleBetweenVector(toward, newToward) >= minCosConeSeek)
			{
			    l.add(e);
			    spell.damageEntity(e);
			    seekTargets(e, level + 1, newToward);
			}
		    }
		}
	    }
	    originAndChainedMap.put(start, l);
	}
    }

    public AxisAlignedBB getBBFromMidPoint(Entity e, double d)
    {
	double x = e.posX;
	double y = e.posY + e.getEyeHeight() / 2;
	double z = e.posZ;
	AxisAlignedBB aabb = e.getBoundingBox();
	if (aabb != null)
	{
	    x = (aabb.minX + aabb.maxX) / 2;
	    y = (aabb.minY + aabb.maxY) / 2;
	    z = (aabb.minZ + aabb.maxZ) / 2;
	}
	return AxisAlignedBB.getBoundingBox(x - d, y - d, z - d, x + d, y + d, z + d);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
	spell = Spell.createFromNBT(var1.getCompoundTag("Spell"));
	setDead();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
	var1.setTag("Spell", spell.writeToNBT());
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
	try
	{
	    byte[] b = CompressedStreamTools.compress(spell.writeToNBT());
	    buffer.writeInt(b.length);
	    buffer.writeBytes(b);
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
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

}