package com.williameze.minegicka3.main.entities;

import io.netty.buffer.ByteBuf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.Values;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityVortex extends Entity implements IEntityAdditionalSpawnData, IEntityNullifiable
{
    public static List<Color> colors = new ArrayList();
    static
    {
	colors.add(new Color(150, 0, 140));
	colors.add(new Color(150, 0, 90));
	colors.add(new Color(160, 18, 142));
	colors.add(new Color(137, 27, 100));
    }
    public List<FXESimpleParticle> fxes = new ArrayList();
    public int life;
    public double power;
    public double range;
    public int interval;

    public EntityVortex(World par1World)
    {
	super(par1World);
    }

    @Override
    public void setInPortal()
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
	life--;
	if (life < 0)
	{
	    setDead();
	    return;
	}
	if (worldObj.isRemote)
	{
	    for (int a = 0; a < range; a++)
	    {
		if (rand.nextInt(a + 1) == 0)
		{
		    Vector v = new Vector(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5);
		    if (v.isZeroVector()) v = new Vector(1, 0, 0);
		    v.setToLength(range);
		    Vector v2 = v.copy().rotateAround(new Vector(1, 1, 1), Math.PI / 2D);
		    // v2 = v2.rotateAround(v, Math.PI / 2);
		    v2.setToLength(0.7);

		    FXESimpleParticle fxe = new FXESimpleParticle(worldObj);
		    fxe.setSize(0.35, 0.35);
		    fxe.setPosition(posX + v.x, posY + v.y, posZ + v.z);
		    fxe.renderType = 0;
		    fxe.noClip = true;
		    fxe.color = colors.get(rand.nextInt(colors.size()));
		    fxe.life = fxe.maxLife = 40;
		    fxe.motionX = v2.x;
		    fxe.motionY = v2.y;
		    fxe.motionZ = v2.z;
		    fxe.alphaDrops = 0;
		    fxe.alpha = 1;

		    fxes.add(fxe);
		    worldObj.spawnEntityInWorld(fxe);
		}
	    }
	    List toRemove = new ArrayList();
	    for (FXESimpleParticle fxe : fxes)
	    {
		Vector v = FuncHelper.vectorToEntity(fxe, this);
		v.setToLength(0.2);
		fxe.motionX += v.x;
		fxe.motionY += v.y;
		fxe.motionZ += v.z;
		if (getDistanceSqToEntity(fxe) <= 0.2)
		{
		    fxe.setDead();
		    toRemove.add(fxe);
		}
	    }
	    fxes.removeAll(toRemove);
	}
	else
	{
	    if (ticksExisted % interval == 0)
	    {
		List<S0EPacketSpawnObject> packets = new ArrayList();
		boolean fallen = false;
		for (int a = 0; a < range && !fallen; a++)
		{
		    int minX = (int) Math.floor(posX - a);
		    int minY = (int) Math.floor(posY - a);
		    int minZ = (int) Math.floor(posZ - a);
		    int maxX = (int) Math.floor(posX + a);
		    int maxY = (int) Math.floor(posY + a);
		    int maxZ = (int) Math.floor(posZ + a);
		    for (int x = minX; x <= maxX; x++)
		    {
			for (int y = minY; y <= maxY; y++)
			{
			    for (int z = minZ; z <= maxZ; z++)
			    {
				if (rand.nextInt(a + 1) == 0 && getDistanceSq(x, y, z) <= a * a)
				{
				    Block b = worldObj.getBlock(x, y, z);
				    if (!b.isAir(worldObj, x, x, z) && b.getMaterial().getMaterialMobility() < 2
					    && worldObj.getTileEntity(x, y, z) == null)
				    {
					int id = b.getIdFromBlock(b);
					int meta = worldObj.getBlockMetadata(x, y, z);
					fallen = true;
					EntityFallingBlock falling = new EntityFallingBlock(worldObj, x + 0.5, y + 0.5, z + 0.5, b, meta);
					if (rand.nextInt(4) == 0)
					{
					    int value = (id & 65535) | (meta << 16);
					    S0EPacketSpawnObject pkt = new S0EPacketSpawnObject(falling, 70, value);
					    packets.add(pkt);
					}
					worldObj.spawnEntityInWorld(falling);
					worldObj.setBlockToAir(x, y, z);
				    }
				}
			    }
			}
		    }
		}
		if (!packets.isEmpty() && 1 + 1 == 3)
		{
		    List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(
			    EntityPlayer.class,
			    AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(Values.renderDistance,
				    Values.renderDistance, Values.renderDistance));
		    for (EntityPlayer p : list)
		    {
			if (p instanceof EntityPlayerMP)
			{
			    for (S0EPacketSpawnObject pkt : packets)
			    {
				((EntityPlayerMP) p).playerNetServerHandler.sendPacket(pkt);
			    }
			}
		    }
		}
	    }
	}
	List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this,
		AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(range, range, range));
	list.removeAll(fxes);
	double rangeSqr = range * range;
	for (Entity ent : list)
	{
	    double distSqr = getDistanceSqToEntity(ent);
	    if (distSqr < rangeSqr)
	    {
		double dist = Math.pow(distSqr, 1 / 4);
		double length = power / 4D / Math.max(dist, 1);
		if (ent instanceof EntityFallingBlock) length /= 12D;
		Vector v = FuncHelper.vectorToEntity(ent, this);
		v.setToLength(length);
		ent.motionX += v.x;
		ent.motionY += v.y;
		ent.motionZ += v.z;
		if (distSqr <= 2)
		{
		    if (ent instanceof EntityPlayer && ((EntityPlayer) ent).capabilities.isCreativeMode)
		    ;
		    else ent.setDead();
		}
	    }
	}
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag)
    {
	life = tag.getInteger("Life");
	power = tag.getDouble("Power");
	range = tag.getDouble("Range");
	interval = tag.getInteger("Interval");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag)
    {
	tag.setInteger("Life", life);
	tag.setDouble("Power", power);
	tag.setDouble("Range", range);
	tag.setInteger("Interval", interval);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
	buffer.writeInt(life);
	buffer.writeDouble(power);
	buffer.writeDouble(range);
	buffer.writeInt(interval);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
	life = buffer.readInt();
	power = buffer.readDouble();
	range = buffer.readDouble();
	interval = buffer.readInt();
    }

}
