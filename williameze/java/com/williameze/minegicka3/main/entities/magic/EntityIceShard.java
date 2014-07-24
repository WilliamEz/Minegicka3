package com.williameze.minegicka3.main.entities.magic;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.mechanics.spells.Spell;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityIceShard extends Entity implements IEntityAdditionalSpawnData
{
    public Spell spell = Spell.none;
    public int maxTick;

    public EntityIceShard(World par1World)
    {
	super(par1World);
	renderDistanceWeight = Values.renderDistance;
	setSize(0.1f, 0.1f);
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
    protected void entityInit()
    {
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();
	if (ticksExisted > maxTick) setDead();

	if (worldObj.isRemote)
	{
	    for (int a = 0; a < 4; a++)
	    {
		worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(Blocks.ice) + "_0", posX, posY + height / 2, posZ,
			(rand.nextDouble() - 0.5) * 2, (rand.nextDouble() - 0.5) * 2, (rand.nextDouble() - 0.5) * 2);
	    }
	}
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound p_70039_1_)
    {
	return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
	FuncHelper.writeNBTToByteBuf(buffer, spell.writeToNBT());
	buffer.writeInt(maxTick);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
	spell = Spell.createFromNBT(FuncHelper.readNBTFromByteBuf(additionalData));
	maxTick = additionalData.readInt();
    }

}
