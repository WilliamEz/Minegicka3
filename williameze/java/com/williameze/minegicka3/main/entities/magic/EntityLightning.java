package com.williameze.minegicka3.main.entities.magic;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.google.common.collect.Sets;
import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.MathHelper;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.PlayersData;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.spells.Spell;
import com.williameze.minegicka3.main.spells.Spell.CastType;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityLightning extends Entity implements IEntityAdditionalSpawnData
{
    public static double minCosConeSeek = Math.pow(1, 0.5D) / 2D;
    public Spell spell = Spell.none;
    public Map<Entity, List<Entity>> originAndChainedMap = new HashMap();
    public int maxTick;
    public boolean dieWithSpell;

    public EntityLightning(World par1World)
    {
	super(par1World);
	renderDistanceWeight = Values.renderDistance;
	ignoreFrustumCheck = true;
    }

    @Override
    public void setInPortal()
    {
    }

    @Override
    public boolean isBurning()
    {
	return false;
    }

    @Override
    public void setPosition(double par1, double par3, double par5)
    {
	super.setPosition(par1, par3, par5);
	boundingBox.setBounds(par1 - width / 2, par3 - height / 2, par5 - width / 2, par1 + width / 2, par3 + height / 2, par5 + width / 2);
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
    protected void entityInit()
    {
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();
	if (spell == null || spell.toBeInvalidated || !dieWithSpell && ticksExisted > maxTick)
	{
	    setDead();
	    return;
	}
	if (dieWithSpell)
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
	    return;
	}
	setPosition(e.posX, e.posY + (e instanceof EntityPlayer ? e.getEyeHeight() - 0.15 : 0), e.posZ);
	if (e.getLookVec() != null)
	{
	    posX += e.getLookVec().xCoord * 0.3;
	    posY += e.getLookVec().yCoord * 0.3;
	    posZ += e.getLookVec().zCoord * 0.3;
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
	double radius = 6D * Math.pow(lig, 0.5) / Math.pow(level + 1, 0.5);
	return radius * (spell.castType == CastType.Area ? 1.3 : 1);
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
			if (spell.castType == CastType.Area || MathHelper.getCosAngleBetweenVector(toward, newToward) >= minCosConeSeek)
			{
			    l.add(e);
			    spell.damageEntity(e, 30);
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
	maxTick = var1.getInteger("Max Tick");
	dieWithSpell = var1.getBoolean("Die With Spell");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
	var1.setTag("Spell", spell.writeToNBT());
	var1.setInteger("Max Tick", maxTick);
	var1.setBoolean("Die With Spell", dieWithSpell);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
	try
	{
	    byte[] b = CompressedStreamTools.compress(spell.writeToNBT());
	    buffer.writeInt(b.length);
	    buffer.writeBytes(b);
	    buffer.writeInt(maxTick);
	    buffer.writeBoolean(dieWithSpell);
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
	    maxTick = additionalData.readInt();
	    dieWithSpell = additionalData.readBoolean();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

}
