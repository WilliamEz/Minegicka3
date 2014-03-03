package com.williameze.minegicka3.core.rendering.models;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelStaff
{
    public static ModelStaff defaultModel = new ModelStaff();
    public List<IModelComponent> components = new ArrayList();

    public ModelStaff()
    {
	addDefaultStaffComponents();
    }

    public void addDefaultStaffComponents()
    {
	components.add(new Sphere(0, 14, 0, 5));
	
    }

    public void render(ItemStack staff)
    {
	components.clear();
	addDefaultStaffComponents();
	doRenderComponents(staff);
    }

    public void doRenderComponents(ItemStack staff)
    {
	GL11.glPushMatrix();
	GL11.glScaled(1/16D, 1/16D, 1/16D);
	for (IModelComponent cmp : components)
	{
	    cmp.render();
	}
	GL11.glPopMatrix();
    }
}
