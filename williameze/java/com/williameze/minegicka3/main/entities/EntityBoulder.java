package com.williameze.minegicka3.main.entities;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.IntVector;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.CommonProxy;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.SpellDamageModifier;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.spells.DefaultSpellSelector;
import com.williameze.minegicka3.main.spells.Spell;
import com.williameze.minegicka3.main.spells.Spell.CastType;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityBoulder extends Entity implements IEntityAdditionalSpawnData
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
	if (e == null || spell.getCaster() == e || !e.canBeCollidedWith()) return null;
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
	float radius = (float) Math.sqrt(Math.sqrt((double) eCount) * charged * 0.3D);
	setSize(radius * 2, radius * 2);
	collideLeft = eCount + 2;
	friction = 0.99 - eCount * 0.005;
	gravity = 0.01 * eCount;
    }

    public boolean isIce()
    {
	return spell.hasElement(Element.Ice);
    }

    @Override
    public void onUpdate()
    {
	super.onUpdate();
	ticksExisted++;

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
	    motionX *= 0.9 * friction;
	    motionY *= 0.9 * friction;
	    motionZ *= 0.9 * friction;
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
	    List<Entity> entities = FuncHelper.getEntitiesWithinBoundingBoxMovement(worldObj, boundingBox, new Vector(motionX, motionY,
		    motionZ), EntityLivingBase.class, new DefaultSpellSelector(getSpell()));
	    entities.remove(spell.getCaster());
	    Entity e = FuncHelper.getEntityClosestTo(posX, posY, posZ, entities);
	    collideWithEntity(e);
	}

	if (onGroundTick >= 200 || ticksExisted >= 2000) setDead();

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
			    + ((double) rand.nextFloat() - 0.5D) * (double) width, boundingBox.minY + 0.1D,
			    posZ + ((double) rand.nextFloat() - 0.5D) * (double) width, -motionX * 4.0D, 1.5D, -motionZ * 4.0D);
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
		worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_" + worldObj.getBlockMetadata(j, k, l), posX
			+ ((double) rand.nextFloat() - 0.5D) * (double) width, boundingBox.minY + 0.1D, posZ
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
	if (isDead) return;
	if (getSpell().countElements(Element.Earth, Element.Ice) == getSpell().countElements())
	{

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
	    List<Element> le = new ArrayList();
	    for (Element e : getSpell().elements)
	    {
		if (e != Element.Earth && e != Element.Ice) le.add(e);
	    }
	    Spell s = new Spell(le, getSpell().dimensionID, getPersistentID(), CastType.Area, getSpell().additionalData);
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
