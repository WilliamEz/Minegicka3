package com.williameze.minegicka3.main.objects.items;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.objects.items.models.hat.BipedModelHat;
import com.williameze.minegicka3.main.objects.items.models.hat.ModelHat;
import com.williameze.minegicka3.mechanics.SpellDamageModifier;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Hat extends ItemArmor
{
    public SpellDamageModifier wearerIncomeDamageModifier;
    
    public Hat()
    {
	super(ArmorMaterial.CLOTH, 0, 0);
	ModBase.proxy.registerItemRenderer(this);
	setMaxStackSize(1);
	setCreativeTab(ModBase.modCreativeTab);
	setTextureName("apple");
    }

    @Override
    public boolean isDamageable()
    {
	return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
	if (armorSlot == 0) return BipedModelHat.getBipedModel(ModelHat.getModelHat(itemStack));
	else return null;
    }
}
