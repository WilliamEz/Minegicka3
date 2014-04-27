package com.williameze.minegicka3.main.entities;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.google.common.collect.Sets;
import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.MathHelper;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.SpellDamageModifier;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.spells.DefaultSpellSelector;
import com.williameze.minegicka3.main.spells.Spell;
import com.williameze.minegicka3.main.spells.Spell.CastType;

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
	ticksExisted++;
	if (spell == null || ticksExisted > maxTick())
	{
	    setDead();
	    return;
	}

	Entity e = spell.getCaster();
	if (!searched)
	{
	    searched = true;
	    targets.addAll(worldObj.selectEntitiesWithinAABB(EntityLivingBase.class,
		    AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(maxRange(), 2, maxRange()), new DefaultSpellSelector(spell)));
	}
	if (!targets.isEmpty())
	{
	    List<Entity> toRemove = new ArrayList();
	    double maxAtkDistSqr = Math.pow((ticksExisted + 1D) / maxTick() * maxRange(), 2);
	    for (Entity ent : targets)
	    {
		if (ent.getDistanceSqToEntity(this) <= maxAtkDistSqr)
		{
		    spell.damageEntity(ent, 0, damMod);
		    toRemove.add(ent);
		}
	    }
	    targets.removeAll(toRemove);
	}
    }

    public double maxRange()
    {
	return (spell.elements.size() * 4D + 12D);
    }

    public int maxTick()
    {
	return (int) Math.round(maxRange() / 16 * 20);
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
