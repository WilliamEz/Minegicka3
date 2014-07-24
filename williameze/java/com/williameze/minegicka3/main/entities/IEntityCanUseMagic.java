package com.williameze.minegicka3.main.entities;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;

import com.williameze.minegicka3.main.entities.ai.AIUseMagick;
import com.williameze.minegicka3.main.entities.ai.AIUseSpell;
import com.williameze.minegicka3.mechanics.spells.TemplateSpell;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public interface IEntityCanUseMagic extends IEntityAdditionalSpawnData
{
    public Map<Integer, Integer> groupCooldown = new HashMap();

    public int getSpellGroupCooldown(int i);

    public void setSpellGroupCooldown(int i, int cd);

    public int getUserCooldown();

    public void setUserCooldown();

    public boolean isCooledDown(int groupID);

    public boolean isSpellApplicableNow(AIUseSpell ai);

    public boolean canContinueSpell(TemplateSpell template);

    public boolean isMagickApplicableNow(AIUseMagick ai);
    
    public void setCasting(boolean b);
    
    public boolean getCasting();

    public NBTTagCompound getAdditionalTag(TemplateSpell template);
}
