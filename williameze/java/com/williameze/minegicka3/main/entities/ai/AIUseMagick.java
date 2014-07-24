package com.williameze.minegicka3.main.entities.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import java.util.Random;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.entities.IEntityCanUseMagic;
import com.williameze.minegicka3.mechanics.spells.Spell;
import com.williameze.minegicka3.mechanics.spells.TemplateSpell;

public class AIUseMagick extends EntityAIBase
{
    public static Random rnd = new Random();

    public IEntityCanUseMagic user;
    public EntityLivingBase userEntity;
    public TemplateSpell magick;

    public boolean allowMultipleSpells;
    public int groupID;
    public int groupCooldown;
    public int chance;

    public AIUseMagick(IEntityCanUseMagic entity, TemplateSpell template, int group, int groupCD, int chanceToHappen)
    {
	if (entity instanceof EntityLivingBase == false)
	{
	    System.err.println(entity + " is not a living entity.");
	    throw new ClassCastException("Casting " + entity + " to EntityLivingBase");
	}
	setMutexBits(hashCode());
	user = entity;
	userEntity = (EntityLivingBase) entity;
	magick = template;
	groupID = group;
	groupCooldown = groupCD;
	chance = chanceToHappen;
    }

    @Override
    public boolean shouldExecute()
    {
	boolean b = !userEntity.worldObj.isRemote && user.isCooledDown(groupID) && rnd.nextInt(100) <= chance && user.isMagickApplicableNow(this);
	return b;
    }

    @Override
    public boolean continueExecuting()
    {
	return false;
    }

    @Override
    public boolean isInterruptible()
    {
	return false;
    }

    @Override
    public void startExecuting()
    {
	magick.magick.serverReceivedMagick(userEntity.worldObj, userEntity.posX, userEntity.posY, userEntity.posZ, userEntity,
		user.getAdditionalTag(magick));
	user.setSpellGroupCooldown(groupID, groupCooldown);
	user.setUserCooldown();
    }

    @Override
    public void updateTask()
    {
    }

    @Override
    public void resetTask()
    {
    }
}
