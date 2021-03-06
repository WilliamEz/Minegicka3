package com.williameze.minegicka3.main.entities.magic;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.IntVector;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.entities.IEntityNullifiable;
import com.williameze.minegicka3.main.objects.blocks.TileEntityShield;
import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.SpellDamageModifier;
import com.williameze.minegicka3.mechanics.spells.ESelectorDefault;
import com.williameze.minegicka3.mechanics.spells.Spell;
import com.williameze.minegicka3.mechanics.spells.Spell.CastType;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityBoulder extends Entity implements IEntityAdditionalSpawnData, IProjectile, IEntityNullifiable
{
    public Spell spell = Spell.none;
    public double gravity;
    public double friction;
    public int onGroundTick;
    public int collideLeft;
    public List<List<Vector>> prevPos = new ArrayList();

    public EntityBoulder(World par1World)
    {
	super(par1World);
	renderDistanceWeight = Values.renderDistance;
	setSize(0.5F, 0.5F);
	gravity = 0.01;
	friction = 0.985;
	onGroundTick = 0;
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
    public AxisAlignedBB getBoundingBox()
    {
	return boundingBox;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity e)
    {
	if (e == null || spell.getCaster() == e && spell.castType != CastType.Self || !e.canBeCollidedWith()) return null;
	return boundingBox;
    }

    @Override
    public boolean canBeCollidedWith()
    {
	return true;
    }

    @Override
    public boolean canBePushed()
    {
	return true;
    }

    @Override
    protected void entityInit()
    {
    }

    public Spell getSpell()
    {
	return spell;
    }

    public void setSpell(Spell s)
    {
	spell = s;
	double charged = spell.additionalData.getDouble("Projectile charged");
	if (charged < 0.2) charged = 0.2;
	if (charged > 1) charged = 1;
	int eCount = spell.countElements(Element.Earth, Element.Ice);
	float radius = (float) ((charged + 0.4) * 0.3D * Math.pow(eCount, 0.85));
	setSize(radius * 2, radius * 2);
	collideLeft = eCount + 2;
	friction = 0.99 - eCount * 0.005;
	gravity = 0.03 * Math.pow(eCount, 1.1);
    }

    public boolean isIce()
    {
	return spell.hasElement(Element.Ice);
    }

    @Override
    public void setPosition(double par1, double par3, double par5)
    {
	super.setPosition(par1, par3, par5);
	boundingBox.setBounds(par1 - width / 2, par3 - height / 2, par5 - width / 2, par1 + width / 2, par3 + height / 2, par5 + width / 2);
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();

	prevPosX = posX;
	prevPosY = posY;
	prevPosZ = posZ;
	moveEntity(motionX, motionY, motionZ);
	if (!onGround) motionY -= gravity;
	motionX *= friction;
	motionY *= friction;
	motionZ *= friction;

	if (isCollided)
	{
	    motionX *= 0.7 * friction;
	    motionY *= 0.7 * friction;
	    motionZ *= 0.7 * friction;
	    onGroundTick++;
	}

	if (!isDead)
	{
	    List<IntVector> blocks = getBlocksWithinAABB();
	    for (IntVector i : blocks)
	    {
		collideWithBlock(i.x, i.y, i.z);
	    }
	}
	if (!isDead)
	{
	    List<Entity> entities = FuncHelper.getEntitiesWithinBoundingBoxMovement(worldObj, boundingBox, new Vector(motionX, motionY, motionZ),
		    EntityLivingBase.class, new ESelectorDefault(getSpell()));
	    entities.remove(spell.getCaster());
	    Entity e = FuncHelper.getEntityClosestTo(posX, posY, posZ, entities);
	    collideWithEntity(e);
	}

	if (onGroundTick >= 100 || ticksExisted >= 2000) setDead();

	if (ticksExisted >= 400 && getSpell().countElements() > getSpell().countElements(Element.Earth, Element.Ice))
	{
	    novaHere();
	    setDead();
	}

	if (worldObj.isRemote)
	{
	    addPrevPos();
	    if (onGround && (motionX * motionX + motionY * motionY + motionZ * motionZ) >= 0.1)
	    {
		int j = MathHelper.floor_double(posX);
		int k = MathHelper.floor_double(posY - 0.20000000298023224D - (double) yOffset);
		int l = MathHelper.floor_double(posZ);
		Block block = worldObj.getBlock(j, k, l);
		for (int a = 0; a < 4; a++)
		{
		    worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_" + worldObj.getBlockMetadata(j, k, l), posX
			    + ((double) rand.nextFloat() - 0.5D) * (double) width, boundingBox.minY + 0.1D, posZ + ((double) rand.nextFloat() - 0.5D)
			    * (double) width, -motionX * 4.0D, 1.5D, -motionZ * 4.0D);
		}
	    }
	}
    }

    protected void updateFallState(double par1, boolean par3)
    {
	if (!isInWater())
	{
	    handleWaterMovement();
	}

	if (par3 && fallDistance > 0.0F)
	{
	    int i = MathHelper.floor_double(posX);
	    int j = MathHelper.floor_double(posY - 0.20000000298023224D - (double) yOffset);
	    int k = MathHelper.floor_double(posZ);
	    Block block = worldObj.getBlock(i, j, k);

	    if (block.getMaterial() == Material.air)
	    {
		int l = worldObj.getBlock(i, j - 1, k).getRenderType();

		if (l == 11 || l == 32 || l == 21)
		{
		    block = worldObj.getBlock(i, j - 1, k);
		}
	    }
	    else if (!worldObj.isRemote && fallDistance > 3.0F)
	    {
		worldObj.playAuxSFX(2006, i, j, k, MathHelper.ceiling_float_int(fallDistance - 3.0F));
	    }

	    block.onFallenUpon(worldObj, i, j, k, this, fallDistance);
	}

	super.updateFallState(par1, par3);
    }

    @Override
    protected void fall(float par1)
    {
	if (par1 <= 0) return;
	super.fall(par1);
	float f1 = 0.0F;
	int i = MathHelper.ceiling_float_int(par1 - 3.0F - f1);

	if (i > 0)
	{
	    playSound("game.neutral.hurt.fall.small", 1.0F, 1.0F);
	    int j = MathHelper.floor_double(posX);
	    int k = MathHelper.floor_double(posY - 0.20000000298023224D - (double) yOffset);
	    int l = MathHelper.floor_double(posZ);
	    Block block = worldObj.getBlock(j, k, l);

	    for (int a = 0; a < 8; a++)
	    {
		worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_" + worldObj.getBlockMetadata(j, k, l),
			posX + ((double) rand.nextFloat() - 0.5D) * (double) width, boundingBox.minY + 0.1D, posZ
				+ ((double) rand.nextFloat() - 0.5D) * (double) width, -motionX * 4.0D, 1.5D, -motionZ * 4.0D);
	    }
	    if (block.getMaterial() != Material.air)
	    {
		Block.SoundType soundtype = block.stepSound;
		playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
	    }
	}
    }

    public void addPrevPos()
    {
	if (boundingBox == null) return;
	if (prevPos.size() < 8)
	{
	    prevPos.clear();
	    for (int a = 0; a < 8; a++)
	    {
		prevPos.add(new ArrayList());
	    }
	}
	prevPos.get(0).add(
		new Vector(0.75 * boundingBox.maxX + 0.25 * boundingBox.minX, 0.75 * boundingBox.maxY + 0.25 * boundingBox.minY, 0.75
			* boundingBox.maxZ + 0.25 * boundingBox.minZ));
	prevPos.get(1).add(
		new Vector(0.25 * boundingBox.maxX + 0.75 * boundingBox.minX, 0.75 * boundingBox.maxY + 0.25 * boundingBox.minY, 0.75
			* boundingBox.maxZ + 0.25 * boundingBox.minZ));
	prevPos.get(2).add(
		new Vector(0.25 * boundingBox.maxX + 0.75 * boundingBox.minX, 0.75 * boundingBox.maxY + 0.25 * boundingBox.minY, 0.25
			* boundingBox.maxZ + 0.75 * boundingBox.minZ));
	prevPos.get(3).add(
		new Vector(0.75 * boundingBox.maxX + 0.25 * boundingBox.minX, 0.75 * boundingBox.maxY + 0.25 * boundingBox.minY, 0.25
			* boundingBox.maxZ + 0.75 * boundingBox.minZ));
	prevPos.get(4).add(
		new Vector(0.75 * boundingBox.maxX + 0.25 * boundingBox.minX, 0.25 * boundingBox.maxY + 0.75 * boundingBox.minY, 0.75
			* boundingBox.maxZ + 0.25 * boundingBox.minZ));
	prevPos.get(5).add(
		new Vector(0.25 * boundingBox.maxX + 0.75 * boundingBox.minX, 0.25 * boundingBox.maxY + 0.75 * boundingBox.minY, 0.75
			* boundingBox.maxZ + 0.25 * boundingBox.minZ));
	prevPos.get(6).add(
		new Vector(0.25 * boundingBox.maxX + 0.75 * boundingBox.minX, 0.25 * boundingBox.maxY + 0.75 * boundingBox.minY, 0.25
			* boundingBox.maxZ + 0.75 * boundingBox.minZ));
	prevPos.get(7).add(
		new Vector(0.75 * boundingBox.maxX + 0.25 * boundingBox.minX, 0.25 * boundingBox.maxY + 0.75 * boundingBox.minY, 0.25
			* boundingBox.maxZ + 0.75 * boundingBox.minZ));
    }

    public void collideWithBlock(int x, int y, int z)
    {
	if (isDead || worldObj.isRemote) return;
	if (getSpell().countElements(Element.Earth, Element.Ice) == getSpell().countElements())
	{
	    double strength = getSpell().countElements() * getSpell().additionalData.getDouble("Projectile charged") / 2.5D;
	    Block b = worldObj.getBlock(x, y, z);
	    if (b.getMaterial() == Material.glass)
	    {
		if (strength > b.getBlockHardness(worldObj, x, y, z))
		{
		    b.dropBlockAsItemWithChance(worldObj, x, y, z, 1, 1, 1);
		    worldObj.setBlockToAir(x, y, z);
		}
	    }
	    if (b == ModBase.shieldBlock)
	    {
		TileEntityShield tile = (TileEntityShield) worldObj.getTileEntity(x, y, z);
		tile.damageShield(strength * 15);
	    }
	}
	else
	{
	    novaHere();
	    setDead();
	}
    }

    public void collideWithEntity(Entity e)
    {
	if (isDead || e == null || e.isDead) return;
	if (getSpell().countElements(Element.Earth, Element.Ice) == getSpell().countElements())
	{
	    double mass = width * width * height;
	    double emass = e.width * e.width * e.height;
	    Vector motion = new Vector(motionX, motionY, motionZ);
	    Vector emotion = new Vector(e.motionX, e.motionY, e.motionZ);
	    Vector newMotion = motion.multiply(mass - emass).add(emotion.multiply(2 * emass)).multiply(1D / (mass + emass));
	    Vector neweMotion = emotion.multiply(emass - mass).add(motion.multiply(2 * mass)).multiply(1D / (mass + emass));
	    motionX = motion.x;
	    motionY = motion.y;
	    motionZ = motion.z;
	    e.motionX = emotion.x;
	    e.motionY = emotion.y;
	    e.motionZ = emotion.z;

	    getSpell().damageEntity(e, 2);
	    collideLeft--;
	    if (collideLeft <= 0) setDead();
	}
	else
	{
	    novaHere();
	    setDead();
	}
    }

    public void novaHere()
    {
	if (!worldObj.isRemote && !isDead)
	{
	    Spell s = new Spell(getSpell().elements, getSpell().dimensionID, getPersistentID(), null, CastType.Area, getSpell().additionalData);
	    EntityBeamArea beamA = new EntityBeamArea(worldObj);
	    beamA.spell = s;
	    beamA.setPosition(posX, posY + 1, posZ);
	    beamA.damMod = new SpellDamageModifier(0.1 + getSpell().additionalData.getDouble("Projectile charged"));
	    worldObj.spawnEntityInWorld(beamA);
	}
    }

    public List<IntVector> getBlocksWithinAABB()
    {
	AxisAlignedBB bb2 = boundingBox;
	int i = MathHelper.floor_double(bb2.minX - 0.2D);
	int j = MathHelper.floor_double(bb2.maxX + 0.2D);
	int k = MathHelper.floor_double(bb2.minY - 0.2D);
	int l = MathHelper.floor_double(bb2.maxY + 0.2D);
	int i1 = MathHelper.floor_double(bb2.minZ - 0.2D);
	int j1 = MathHelper.floor_double(bb2.maxZ + 0.2D);

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
	FuncHelper.writeNBTToByteBuf(buffer, spell.writeToNBT());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
	spell = Spell.createFromNBT(FuncHelper.readNBTFromByteBuf(additionalData));
	setSpell(spell);
    }

    @Override
    public void setThrowableHeading(double var1, double var3, double var5, float var7, float var8)
    {
    }

}
