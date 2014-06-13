package com.williameze.minegicka3.main.entities.magic;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.spells.ESelectorDefault;
import com.williameze.minegicka3.main.spells.Spell;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityIcicle extends Entity implements IEntityAdditionalSpawnData
{
    public Spell spell = Spell.none;
    public double gravity;
    public double friction;
    public int onGroundTick;
    public double headingX, headingY, headingZ = 1;
    public List<Vector> prevPos = new ArrayList();

    public EntityIcicle(World par1World)
    {
	super(par1World);
	renderDistanceWeight = Values.renderDistance;
	setSize(0.2F, 0.2F);
	gravity = 0.01;
	friction = 0.98;
	onGround = false;
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
    protected void entityInit()
    {
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
	return null;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity e)
    {
	return null;
    }

    @Override
    public boolean canBeCollidedWith()
    {
	return false;
    }

    @Override
    public boolean canBePushed()
    {
	return false;
    }

    @Override
    protected boolean canTriggerWalking()
    {
	return false;
    }

    public void setSpell(Spell s)
    {
	spell = s;
    }

    public Spell getSpell()
    {
	return spell;
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();
	prevPosX = posX;
	prevPosY = posY;
	prevPosZ = posZ;
	if (!isCollided) motionY -= gravity;
	motionX *= friction;
	motionY *= friction;
	motionZ *= friction;

	if (!isDead)
	{
	    List<Entity> entities = FuncHelper.getEntitiesWithinBoundingBoxMovement(worldObj, boundingBox, new Vector(motionX, motionY, motionZ), EntityLivingBase.class,
		    new ESelectorDefault(getSpell()));
	    entities.remove(spell.getCaster());
	    Entity e = FuncHelper.getEntityClosestTo(posX, posY, posZ, entities);
	    collideWithEntity(e);
	}
	moveEntity(motionX, motionY, motionZ);
	if (isCollided || onGround)
	{
	    motionX = motionY = motionZ = 0;
	    onGroundTick++;
	}

	if (onGroundTick >= 200 || ticksExisted >= 2000) setDead();

	if (motionX != 0 || motionY != 0 || motionZ != 0)
	{
	    Vector motion = new Vector(motionX, motionY, motionZ).normalize();
	    headingX = motion.x;
	    headingY = motion.y;
	    headingZ = motion.z;
	}
	if (worldObj.isRemote)
	{
	    prevPos.add(new Vector(posX, posY, posZ));
	}
    }

    public void collideWithEntity(Entity e)
    {
	if (isDead || e == null || e.isDead) return;
	getSpell().damageEntity(e, 0);
	setDead();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
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
	    setSpell(Spell.createFromNBT(tag));
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }
}
