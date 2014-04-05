package com.williameze.minegicka3.main.entities;

import io.netty.buffer.ByteBuf;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.IntVector;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.spells.Spell;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntitySpray extends Entity implements IEntityAdditionalSpawnData
{
    private Spell spell;
    public Color color;
    public double gravity;
    public int maxTicks;
    public boolean server;

    public Vector spiralCore;

    public EntitySpray(World par1World)
    {
	super(par1World);
	color = Color.white;
	setSize(0.06F, 0.06F);
	gravity = 0;
	renderDistanceWeight = 32;
	maxTicks = 10;
	spiralCore = null;
	server = false;
    }

    public void setSpell(Spell s)
    {
	spell = s;
	maxTicks = (int) (30D * Math.pow(s.countElements(), 0.45));
    }

    public Spell getSpell()
    {
	return spell;
    }

    public EntitySpray(World world, Spell s)
    {
	this(world);
	spell = s;
    }

    @Override
    public void setPosition(double par1, double par3, double par5)
    {
	this.posX = par1;
	this.posY = par3;
	this.posZ = par5;
	boundingBox.setBounds(posX - width / 2, posY - height / 2, posZ - width / 2, posX + width / 2, posY + height / 2, posZ
		+ width / 2);
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
	// return boundingBox;
	return null;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity e)
    {
	if (e instanceof EntitySpray && spell.equals(((EntitySpray) e).spell)) return null;
	return boundingBox;
	// return null;
    }

    @Override
    public boolean canBeCollidedWith()
    {
	return true;
    }

    @Override
    public boolean canBePushed()
    {
	return false;
    }

    @Override
    protected void fall(float par1)
    {
    }

    @Override
    public boolean attackEntityFrom(DamageSource d, float f)
    {
	if (d.getSourceOfDamage() != null && f > 0)
	{
	    setDead();
	    return true;
	}
	return false;
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();
	this.prevPosX = this.posX;
	this.prevPosY = this.posY;
	this.prevPosZ = this.posZ;

	if (server && !isDead)
	{
	    if (ticksExisted % 4 == 0)
	    {
		List<IntVector> blocks = getBlocksWithinAABB();
		for (IntVector i : blocks)
		{
		    affectBlock(i.x, i.y, i.z);
		}
	    }

	    List<Entity> entities = FuncHelper.getEntitiesWithinBoundingBoxMovement(worldObj, boundingBox, new Vector(motionX,
		    motionY, motionZ), EntityLivingBase.class, null);
	    entities.remove(spell.getCaster());
	    for (Entity e : entities)
	    {
		if (e instanceof EntitySpray)
		{
		    if (!spell.equals(((EntitySpray) e).spell)) collideWithSpray((EntitySpray) e);
		}
		else
		{
		    affectEntity(e);
		}
	    }
	}

	if (spiralCore != null)
	{
	    Vector motion = new Vector(motionX, motionY, motionZ);
	    motion = motion.rotateAround(spiralCore, Math.PI / 20 / spell.countElements());
	    motionX = motion.x;
	    motionY = motion.y;
	    motionZ = motion.z;
	}
	motionY -= gravity;
	moveEntity(motionX, motionY, motionZ);
	motionX *= 0.92;
	motionY *= 0.92;
	motionZ *= 0.92;

	if (isCollided || onGround)
	{
	    ticksExisted += 2;
	    motionX *= 0.7;
	    motionZ *= 0.7;
	}

	if (ticksExisted >= maxTicks) setDead();
	if (Math.abs(motionX) + Math.abs(motionY) + Math.abs(motionZ) <= 0.05) setDead();
    }

    public void affectBlock(int x, int y, int z)
    {

    }

    public void affectEntity(Entity e)
    {
	if (e != null && ticksExisted % 5 == 0)
	{
	    spell.damageEntity(e);
	}
    }

    public void collideWithSpray(EntitySpray e)
    {

    }

    public List<IntVector> getBlocksWithinAABB()
    {
	AxisAlignedBB bb2 = boundingBox.expand(width / 2, height / 2, width / 2);
	int i = MathHelper.floor_double(bb2.minX);
	int j = MathHelper.floor_double(bb2.maxX + 1.0D);
	int k = MathHelper.floor_double(bb2.minY);
	int l = MathHelper.floor_double(bb2.maxY + 1.0D);
	int i1 = MathHelper.floor_double(bb2.minZ);
	int j1 = MathHelper.floor_double(bb2.maxZ + 1.0D);

	if (bb2.minX < 0.0D)
	{
	    --i;
	}

	if (bb2.minY < 0.0D)
	{
	    --k;
	}

	if (bb2.minZ < 0.0D)
	{
	    --i1;
	}
	List<IntVector> list = new ArrayList();

	for (int k1 = i; k1 < j; ++k1)
	{
	    for (int l1 = k; l1 < l; ++l1)
	    {
		for (int i2 = i1; i2 < j1; ++i2)
		{
		    Block block = worldObj.getBlock(k1, l1, i2);

		    if (block.getMaterial() != Material.air)
		    {
			list.add(new IntVector(k1, l1, i2));
		    }
		}
	    }
	}

	return list;
    }

    @Override
    protected void entityInit()
    {
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
	    buffer.writeDouble(spiralCore.x);
	    buffer.writeDouble(spiralCore.y);
	    buffer.writeDouble(spiralCore.z);
	    buffer.writeBoolean(server);
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
	    spiralCore = new Vector(additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble());
	    server = additionalData.readBoolean();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

}
