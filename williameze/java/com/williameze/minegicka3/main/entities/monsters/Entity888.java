package com.williameze.minegicka3.main.entities.monsters;

import static net.minecraftforge.common.ChestGenHooks.DUNGEON_CHEST;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

import com.williameze.api.HitObject;
import com.williameze.api.HitObject.HitType;
import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;
import com.williameze.api.selectors.BSelectorSolid;
import com.williameze.api.selectors.ESelectorNone;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.entities.DropItemEntry;
import com.williameze.minegicka3.main.entities.IEntityCanUseMagic;
import com.williameze.minegicka3.main.entities.IEntityMagicResistance;
import com.williameze.minegicka3.main.entities.ai.AIUseMagick;
import com.williameze.minegicka3.main.entities.ai.AIUseSpell;
import com.williameze.minegicka3.main.entities.magic.EntitySpray;
import com.williameze.minegicka3.main.objects.items.Staff;
import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.SpellDamageModifier;
import com.williameze.minegicka3.mechanics.magicks.Magicks;
import com.williameze.minegicka3.mechanics.spells.Spell.CastType;
import com.williameze.minegicka3.mechanics.spells.TemplateSpell;

public class Entity888 extends EntityCreature implements IBossDisplayData, IMob, IEntityMagicResistance, IEntityCanUseMagic
{
    public static SpellDamageModifier spellResistance = new SpellDamageModifier(0.5).setModifier(Element.Life, 4);
    public static TemplateSpell tsHeal1 = new TemplateSpell(CastType.Self, Element.Life, 5);
    public static TemplateSpell tsHeal2 = new TemplateSpell(CastType.Area, Element.Shield, Element.Life, 3);
    public static TemplateSpell tsBeam1 = new TemplateSpell(CastType.Single, Element.Arcane, 5);
    public static TemplateSpell tsBeam2 = new TemplateSpell(CastType.Single, Element.Arcane, 3, Element.Fire, Element.Lightning);
    public static TemplateSpell tsSpray1 = new TemplateSpell(CastType.Single, Element.Fire, 3);
    public static TemplateSpell tsSpray2 = new TemplateSpell(CastType.Single, Element.Steam, 4, Element.Water, 4);
    public static TemplateSpell tsAOE1 = new TemplateSpell(CastType.Area, Element.Shield, Element.Lightning, 7);
    public static TemplateSpell tsAOE2 = new TemplateSpell(CastType.Area, Element.Earth, 8);
    public static TemplateSpell tsAOE3 = new TemplateSpell(CastType.Area, Element.Cold, 5);
    public static TemplateSpell tsAOE4 = new TemplateSpell(CastType.Area, Element.Ice, 4);
    public static TemplateSpell tsMagick1 = new TemplateSpell(Magicks.nullify);
    public static TemplateSpell tsMagick2 = new TemplateSpell(Magicks.explosion);

    public List<EntityLivingBase> hasAttackedThis = new ArrayList();

    public int cooldown = 0;
    public boolean isCasting = false;
    public Vector lookVec;

    public Entity888(World par1World)
    {
	super(par1World);
	tasks.addTask(0, new AIUseSpell(this, tsHeal1, 0, 200, 10));
	tasks.addTask(2, new AIUseSpell(this, tsHeal2, 0, 150, 5));
	tasks.addTask(1, new AIUseSpell(this, tsBeam1, 1, 140, 20));
	tasks.addTask(1, new AIUseSpell(this, tsBeam2, 1, 140, 15));
	tasks.addTask(1, new AIUseSpell(this, tsSpray1, 2, 160, 25));
	tasks.addTask(1, new AIUseSpell(this, tsSpray2, 2, 120, 10));
	tasks.addTask(2, new AIUseSpell(this, tsAOE1, 3, 180, 10));
	tasks.addTask(2, new AIUseSpell(this, tsAOE2, 3, 120, 20));
	tasks.addTask(2, new AIUseSpell(this, tsAOE3, 3, 120, 17));
	tasks.addTask(2, new AIUseSpell(this, tsAOE4, 3, 160, 12));
	tasks.addTask(0, new AIUseMagick(this, tsMagick1, 4, 160, 10));
	tasks.addTask(0, new AIUseMagick(this, tsMagick2, 4, 160, 30));
	setSize(8, 8);

	lookVec = Vector.unitX.copy();
    }

    @Override
    public boolean isAIEnabled()
    {
	return true;
    }

    @Override
    protected void entityInit()
    {
	super.entityInit();
	dataWatcher.addObject(24, Byte.valueOf((byte) 0));
    }

    @Override
    public float getEyeHeight()
    {
	return height / 2F;
    }

    @Override
    protected void applyEntityAttributes()
    {
	super.applyEntityAttributes();
	getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth).setBaseValue(888);
	getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange).setBaseValue(64);
	getAttributeMap().getAttributeInstance(SharedMonsterAttributes.knockbackResistance).setBaseValue(1);
	getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed).setBaseValue(0);
	getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8);
    }

    @Override
    public Vec3 getLook(float par1)
    {
	return getLookVec();
    }

    @Override
    public Vec3 getLookVec()
    {
	return lookVec.vec3();
    }

    public boolean getAwaken()
    {
	return dataWatcher.getWatchableObjectByte(24) == (byte) 1;
    }

    public void setAwaken(boolean b)
    {
	dataWatcher.updateObject(24, Byte.valueOf((byte) (b ? 1 : 0)));
    }

    @Override
    public SpellDamageModifier getSpellEffectiveness()
    {
	return spellResistance;
    }

    @Override
    public int getUserCooldown()
    {
	return cooldown;
    }

    @Override
    public void setUserCooldown()
    {
	cooldown = 100;
    }

    @Override
    public int getSpellGroupCooldown(int i)
    {
	if (!groupCooldown.containsKey((Integer) i))
	{
	    groupCooldown.put((Integer) i, 0);
	    return 0;
	}
	return groupCooldown.get((Integer) i);
    }

    @Override
    public void setSpellGroupCooldown(int i, int cd)
    {
	groupCooldown.put((Integer) i, cd);
    }

    @Override
    public boolean isCooledDown(int groupID)
    {
	return getUserCooldown() <= 0 && getSpellGroupCooldown(groupID) <= 0;
    }

    @Override
    public boolean isSpellApplicableNow(AIUseSpell ai)
    {
	if (!getAwaken() || getAttackTarget() == null) return false;

	if (ai.groupID == 0 && (getHealth() % 60 == 0 || getHealth() < 80)) return true;

	double distSqr = getDistanceSqToEntity(getAttackTarget());

	if (ai.groupID == 1 && distSqr >= 164) return true;
	if (ai.groupID == 2 && distSqr <= 640) return true;
	if (ai.groupID == 3 && distSqr <= 49) return true;

	List<EntityLivingBase> nearbys = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, getBoundingBox().expand(12, 6, 12));
	nearbys.remove(this);
	List targets = new ArrayList();
	for (EntityLivingBase e : nearbys)
	{
	    if (e instanceof EntityPlayer)
	    {
		targets.add(e);
	    }
	    else if (e.getAITarget() == this)
	    {
		targets.add(e);
	    }
	    else if (e instanceof EntityLiving && ((EntityLiving) e).getAttackTarget() == this)
	    {
		targets.add(e);
	    }
	    else if (e instanceof EntityCreature && ((EntityCreature) e).getEntityToAttack() == this)
	    {
		targets.add(e);
	    }
	}
	targets.add(getAttackTarget());
	if (ai.groupID == 3 && (hasAttackedThis.size() >= 5 || targets.size() > 3)) return true;

	return false;
    }

    @Override
    public boolean isMagickApplicableNow(AIUseMagick ai)
    {
	if (ai.magick == tsMagick1)
	{
	    if (ticksExisted % 5 == 0) return true;
	}
	if (ai.magick == tsMagick2 && getAttackTarget() != null)
	{
	    HitObject hit = FuncHelper.rayTrace(worldObj, FuncHelper.getEyePosition(this), FuncHelper.getCenter(getAttackTarget()),
		    new BSelectorSolid(), new ESelectorNone(), null);
	    if (hit.hitType == HitType.Block) return true;
	}
	return false;
    }

    @Override
    public boolean canContinueSpell(TemplateSpell template)
    {
	return !isDead && getAttackTarget() != null;
    }

    @Override
    public void setCasting(boolean b)
    {
	isCasting = b;
    }

    @Override
    public boolean getCasting()
    {
	return isCasting;
    }

    @Override
    public NBTTagCompound getAdditionalTag(TemplateSpell template)
    {
	NBTTagCompound tag = new NBTTagCompound();
	tag.setTag("Staff", Staff.getStaffTag_static(new ItemStack(ModBase.staffGrand)));
	return tag;
    }

    @Override
    public boolean attackEntityFrom(DamageSource dmg, float par2)
    {
	if (dmg.getSourceOfDamage() instanceof EntityLivingBase && !hasAttackedThis.contains(dmg.getSourceOfDamage()))
	{
	    hasAttackedThis.add((EntityLivingBase) dmg.getSourceOfDamage());
	}
	if (!getAwaken() || !dmg.isMagicDamage())
	{
	    return false;
	}
	return super.attackEntityFrom(dmg, par2);
    }

    @Override
    public void onLivingUpdate()
    {
	super.onLivingUpdate();
	if (getAwaken() && getAttackTarget() == null)
	{
	    setAwaken(false);
	}
	if (!getAwaken() && worldObj.getClosestPlayerToEntity(this, 24) != null)
	{
	    setAwaken(true);
	}
	if (cooldown > 0)
	{
	    cooldown--;
	}
	for (Entry<Integer, Integer> entry : groupCooldown.entrySet())
	{
	    groupCooldown.put(entry.getKey(), Math.max(0, entry.getValue() - 1));
	}

	if (getAttackTarget() != null)
	{
	    lookVec = lookVec.rotateWithLimit(FuncHelper.vectorEyeToCenter(this, getAttackTarget()), Math.PI / 90D).normalize();
	    if (getAttackTarget().isDead || getDistanceSqToEntity(getAttackTarget()) > 64 * 64)
	    {
		setAttackTarget(null);
	    }
	}
	if (getAttackTarget() == null || ticksExisted % 100 == 0)
	{
	    if (getAttackTarget() instanceof EntityPlayer == false)
	    {
		EntityPlayer p = worldObj.getClosestPlayerToEntity(this, 24);
		if (p != null)
		{
		    setAttackTarget(p);
		}
	    }
	    else if (!hasAttackedThis.isEmpty())
	    {
		EntityLivingBase e = hasAttackedThis.get(rand.nextInt(hasAttackedThis.size()));
		if (!e.isDead && getDistanceSqToEntity(e) < 4096)
		{
		    setAttackTarget(e);
		}
		else
		{
		    hasAttackedThis.remove(e);
		}
	    }
	}
	if (getAttackTarget() instanceof EntityPlayer && ((EntityPlayer) getAttackTarget()).capabilities.disableDamage)
	{
	    setAttackTarget(null);
	}
    }

    @Override
    public void onDeath(DamageSource dmg)
    {
	super.onDeath(dmg);
	if (!worldObj.isRemote)
	{
	    int x = (int) posX;
	    int y = (int) posY;
	    int z = (int) posZ;
	    worldObj.setBlock(x, y, z, Blocks.chest);
	    TileEntityChest tile = (TileEntityChest) worldObj.getTileEntity(x, y, z);
	    if (tile != null)
	    {
		WeightedRandomChestContent.generateChestContents(rand, ChestGenHooks.getItems(DUNGEON_CHEST, rand), tile, 18);
	    }

	    double velocity = 0.6;
	    double essenceChance = 0.4;
	    new DropItemEntry(new ItemStack(ModBase.thingy), 64, 0.9).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.thingyGood), 16, 0.3).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.thingySuper), 4, 0.1).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.stick), 32, 0.6).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.stickGood), 8, 0.2).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.stickSuper), 2, 0.05).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.magicApple), 16, 0.3).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.magicGoodApple), 4, 0.1).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.magicSuperApple), 1, 0.01).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.magicCookie), 64, 0.6).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.magicGoodCookie), 16, 0.2).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.magicSuperCookie), 4, 0.05).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.essenceArcane), 8, essenceChance).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.essenceCold), 8, essenceChance).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.essenceEarth), 8, essenceChance).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.essenceFire), 8, essenceChance).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.essenceIce), 8, essenceChance).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.essenceLife), 8, essenceChance).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.essenceLightning), 8, essenceChance).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.essenceShield), 8, essenceChance).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.essenceSteam), 8, essenceChance).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.essenceWater), 8, essenceChance).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.hat), 8, 0.05).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	    new DropItemEntry(new ItemStack(ModBase.matResistance), 8, 0.1).spawn(worldObj, posX, posY + height / 2, posZ, velocity);
	}
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
	super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
	super.readFromNBT(tag);
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
    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
	return getBoundingBox();
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
	return boundingBox;
    }

    @Override
    protected boolean canDespawn()
    {
	return false;
    }

    @Override
    protected void despawnEntity()
    {
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
	buffer.writeLong(entityUniqueID.getMostSignificantBits());
	buffer.writeLong(entityUniqueID.getLeastSignificantBits());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
	entityUniqueID = new UUID(additionalData.readLong(), additionalData.readLong());
    }
}
