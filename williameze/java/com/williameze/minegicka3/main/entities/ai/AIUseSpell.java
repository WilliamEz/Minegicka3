package com.williameze.minegicka3.main.entities.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import java.util.Random;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.entities.IEntityCanUseMagic;
import com.williameze.minegicka3.mechanics.spells.Spell;
import com.williameze.minegicka3.mechanics.spells.TemplateSpell;

public class AIUseSpell extends EntityAIBase
{
    public static Random rnd = new Random();

    public IEntityCanUseMagic user;
    public EntityLivingBase userEntity;
    public TemplateSpell spell;

    public boolean allowMultipleSpells;
    public int groupID;
    public int groupCooldown;
    public int chance;

    public Spell loadedSpell;
    public boolean queueToStop;

    public AIUseSpell(IEntityCanUseMagic entity, TemplateSpell template, int group, int groupCD, int chanceToHappen)
    {
	if (entity instanceof EntityLivingBase == false)
	{
	    System.err.println(entity + " is not a living entity.");
	    throw new ClassCastException("Casting " + entity + " to EntityLivingBase");
	}
	setMutexBits(hashCode());
	user = entity;
	userEntity = (EntityLivingBase) entity;
	spell = template;
	groupID = group;
	groupCooldown = groupCD;
	chance = chanceToHappen;
    }

    @Override
    public boolean shouldExecute()
    {
	boolean b1 = allowMultipleSpells || !user.getCasting();
	if (!b1) return false;

	boolean b2 = !userEntity.worldObj.isRemote && loadedSpell == null && user.isCooledDown(groupID) && rnd.nextInt(100) <= chance
		&& user.isSpellApplicableNow(this);
	return b2;
    }

    @Override
    public boolean continueExecuting()
    {
	return !queueToStop && user.canContinueSpell(spell);
    }

    @Override
    public boolean isInterruptible()
    {
	return allowMultipleSpells;
    }

    @Override
    public void startExecuting()
    {
	loadedSpell = spell.toSpell(userEntity, user.getAdditionalTag(spell));
	ModBase.proxy.getCoreServer().spellTriggerReceived(loadedSpell, true);
	user.setCasting(true);
    }

    @Override
    public void updateTask()
    {
	if (loadedSpell != null)
	{
	    if (loadedSpell.toBeInvalidated)
	    {
		queueToStop = true;
	    }
	}
	else queueToStop = true;
    }

    @Override
    public void resetTask()
    {
	if (loadedSpell != null)
	{
	    loadedSpell.stopSpell();
	    loadedSpell = null;
	}
	queueToStop = false;
	user.setSpellGroupCooldown(groupID, groupCooldown);
	user.setUserCooldown();
	user.setCasting(false);
    }
}
