package com.williameze.minegicka3.main.entities.magic;

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
import com.williameze.minegicka3.main.entities.IEntityNullifiable;
import com.williameze.minegicka3.main.spells.ESelectorDefault;
import com.williameze.minegicka3.main.spells.Spell;
import com.williameze.minegicka3.main.spells.Spell.CastType;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityEarthRumble extends Entity implements IEntityAdditionalSpawnData, IEntityNullifiable
{
    private Spell spell = Spell.none;
    public boolean searched;
    public List<Entity> targets = new ArrayList();

    public EntityEarthRumble(World par1World)
    {
	super(par1World);
	renderDistanceWeight = Values.renderDistance;
	setSize(4, 4);
	searched = false;
	ignoreFrustumCheck = true;
    }

    public Spell getSpell()
    {
	return spell;

    }

    public void setSpell(Spell s)
    {
	spell = s;
	width = (s.countElements() * 4 + 4) * 2;
	height = s.countElements() * 2 + 1;
	boundingBox.setBounds(posX - width / 2, posY - height / 2, posZ - width / 2, posX + width / 2, posY + height / 2, posZ + width / 2);
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
	    targets.addAll(worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, boundingBox, new ESelectorDefault(spell)));
	}
	if (ticksExisted % interval() == 0)
	{
	    if (!targets.isEmpty())
	    {
		List<Entity> toRemove = new ArrayList();
		for (Entity ent : targets)
		{
		    if (ent.onGround == false)
		    {
			toRemove.add(ent);
			continue;
		    }
		    if (ent.getDistanceSq(posX, ent.posY, posZ) <= Math.pow(ticksExisted / (double) maxTick() * maxRange(), 2))
		    {
			spell.damageEntity(ent, 0);
			ent.motionY += spell.countElements() * spell.getPower() * 0.15;
			toRemove.add(ent);
		    }
		}
		targets.removeAll(toRemove);
	    }
	}
    }

    public double maxRange()
    {
	return width / 2;
    }

    public int interval()
    {
	return 8;
    }

    public int maxTick()
    {
	return interval() * Math.max(6 - getSpell().countElements(), 4) + interval() - 1;
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
	    setSpell(spell);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

}
