package com.williameze.minegicka3.main.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.williameze.api.models.ModelObject;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.objects.ItemStaff;

public class ModelStaff
{
    public static ModelStaffDefault defaultStaffModel = new ModelStaffDefault();
    public static Map<ItemStaff, ModelStaff> staffModels = new HashMap();
    
    public static ModelStaff getModel(ItemStack is)
    {
	ModelStaff md = staffModels.get(is.getItem());
	if(md!=null)
	{
	    ModelStaff detailed = md.getDetailedModel(is);
	    if(detailed!=null) return detailed;
	    return md;
	}
	return defaultStaffModel;
    }
    
    public static void load()
    {
	addStaffModel(ModBase.staffGrand, new ModelStaffGrand());
	addStaffModel(ModBase.staffSuper, new ModelStaffSuper());
    }
    
    public static void addStaffModel(Item i, ModelStaff model)
    {
	if(i instanceof ItemStaff)
	{
	    staffModels.put((ItemStaff) i, model);
	}
    }
    
    public List<ModelObject> components = new ArrayList();

    public ModelStaff()
    {
	components = new ArrayList();
	addComponents();
    }
    
    public ModelStaff getDetailedModel(ItemStack is)
    {
	return this;
    }

    public void addComponents()
    {

    }

    public void render(ItemStack staff)
    {
	doRenderParameters(staff);
    }

    public void doRenderParameters(ItemStack staff)
    {
	GL11.glPushMatrix();
	GL11.glScaled(1 / 16D, 1 / 16D, 1 / 16D);
	doRenderComponents(staff);
	GL11.glPopMatrix();
    }

    public void doRenderComponents(ItemStack staff)
    {
	renderList(staff, components);
    }

    public void renderList(ItemStack staff, List<ModelObject> l)
    {
	for (ModelObject o : l)
	{
	    onComponentPreRender(staff, o);
	    o.render();
	    onComponentPostRender(staff, o);
	}
    }

    public void onComponentPreRender(ItemStack staff, ModelObject o)
    {

    }

    public void onComponentPostRender(ItemStack staff, ModelObject o)
    {

    }
}
