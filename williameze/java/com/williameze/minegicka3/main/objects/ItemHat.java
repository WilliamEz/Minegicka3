package com.williameze.minegicka3.main.objects;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.models.BipedModelHat;
import com.williameze.minegicka3.main.models.ModelHat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemHat extends ItemArmor
{
    public ItemHat()
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
	if (armorSlot == 0) return BipedModelHat.getBipedModel(ModelHat.hatModels.get(itemStack.getItem()));
	else return null;
    }
}
