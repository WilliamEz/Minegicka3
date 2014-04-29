package com.williameze.minegicka3.main.entities;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.spells.DefaultSpellSelector;
import com.williameze.minegicka3.main.spells.Spell;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityBeam extends Entity implements IEntityAdditionalSpawnData
{
    public Spell spell = Spell.none;
    public Vector towardTarget;
    public int lastAffectedBlock;

    public EntityBeam(World par1World)
    {
	super(par1World);
	lastAffectedBlock = 0;
	renderDistanceWeight = Values.renderDistance;
	setSize(0.1F, 0.1F);
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
	if (spell == null || spell.toBeInvalidated)
	{
	    setDead();
	    return;
	}
	if (!spell.toBeInvalidated)
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
	setPosition(e.posX, e.posY + e.getEyeHeight() - 0.25, e.posZ);
	if (e.getLookVec() != null)
	{
	    Vector v = new Vector(e.getLookVec());
	    v = v.multiply(0.3);
	    posX += v.x;
	    posY += v.y;
	    posZ += v.z;
	    setPosition(posX, posY, posZ);
	}

	lastAffectedBlock--;
	if (lastAffectedBlock < 0) lastAffectedBlock = 0;
	target();
    }

    public double getBeamMaxLength()
    {
	return 64 + 32 * spell.countElements(Element.Arcane, Element.Life);
    }

    public void target()
    {
	Entity caster = spell.getCaster();
	double lengthSqrToTarget = getBeamMaxLength() * getBeamMaxLength();
	Vector vec = new Vector(caster.getLookVec());

	Vector from = new Vector(posX, posY, posZ);
	Vector to = from.add(vec.multiply(getBeamMaxLength()));
	MovingObjectPosition mop = worldObj.func_147447_a(from.vec3(), to.vec3(), false, true, false);
	double mopdsq = lengthSqrToTarget;
	if (mop != null) mopdsq = getDistanceSq(mop.blockX + 0.5, mop.blockY + 0.5, mop.blockZ + 0.5);

	List<Entity> l = FuncHelper.getEntitiesWithinBoundingBoxMovement(worldObj, boundingBox, vec.multiply(mopdsq),
		EntityLivingBase.class, new DefaultSpellSelector(spell));
	Entity pointingAt = FuncHelper.getEntityClosestTo(posX, posY, posZ, l);

	if (pointingAt != null)
	{
	    affectEntity(pointingAt);
	    lengthSqrToTarget = pointingAt.getDistanceSqToEntity(this);
	}
	else if (mop != null)
	{
	    if (lastAffectedBlock == 0) affectBlock(mop, mop.blockX, mop.blockY, mop.blockZ);
	    lengthSqrToTarget = mopdsq;
	}
	towardTarget = vec.multiply(Math.sqrt(lengthSqrToTarget)).add(posX - caster.posX,
		posY - caster.posY - caster.getEyeHeight() + 0.25, posZ - caster.posZ);
    }

    public void affectBlock(MovingObjectPosition mop, int x, int y, int z)
    {
	if (worldObj.isRemote) return;
	if (mop != null)
	{
	    switch (mop.sideHit)
	    {
		case 0:
		    y--;
		    break;
		case 1:
		    y++;
		    break;
		case 2:
		    z--;
		    break;
		case 3:
		    z++;
		    break;
		case 4:
		    x--;
		    break;
		case 5:
		    x++;
		    break;
		default:
		    break;
	    }
	}
	int fire = spell.countElement(Element.Fire);
	int water = spell.countElement(Element.Water);
	int cold = spell.countElement(Element.Cold);
	int steam = spell.countElement(Element.Steam);
	Block b = worldObj.getBlock(x, y, z);
	if (worldObj.isAirBlock(x, y, z) || b.getMaterial().isReplaceable())
	{
	    if (fire > 0 && Blocks.fire.canPlaceBlockAt(worldObj, x, y, z))
	    {
		worldObj.setBlock(x, y, z, Blocks.fire);
		lastAffectedBlock = 40 / fire;
		return;
	    }
	    if (cold > 0 && Blocks.snow_layer.canPlaceBlockAt(worldObj, x, y, z))
	    {
		worldObj.setBlock(x, y, z, Blocks.snow_layer);
		lastAffectedBlock = 40 / cold;
		return;
	    }
	}
	if (fire > 0 && b == Blocks.snow_layer)
	{
	    worldObj.setBlock(x, y, z, Blocks.air);
	    lastAffectedBlock = 40 / fire;
	    return;
	}
	if (b.getMaterial() == Material.fire && cold + water + steam > 0)
	{
	    worldObj.setBlock(x, y, z, Blocks.air);
	    lastAffectedBlock = 40 / (cold + water + steam);
	    return;
	}
	if (b == Blocks.dirt && spell.hasElement(Element.Life))
	{
	    worldObj.setBlock(x, y, z, Blocks.grass);
	    lastAffectedBlock = 40 / spell.countElement(Element.Life);
	    return;
	}
	if (b == Blocks.grass && spell.hasElement(Element.Arcane))
	{
	    worldObj.setBlockToAir(x, y, z);
	    lastAffectedBlock = 40 / spell.countElement(Element.Arcane);
	    return;
	}
    }

    public void affectEntity(Entity e)
    {
	spell.damageEntity(e, 25);
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
