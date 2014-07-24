package com.williameze.minegicka3.main.objects.items.models.staff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.williameze.api.models.ModelObject;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.objects.items.Staff;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelStaff
{
    public static ModelStaffDefault defaultStaffModel = new ModelStaffDefault();
    public static Map<Staff, ModelStaff> staffModels = new HashMap();

    public static ModelStaff getModel(ItemStack is)
    {
	ModelStaff md = staffModels.get(is.getItem());
	if (md != null)
	{
	    ModelStaff detailed = md.getDetailedModel(is);
	    if (detailed != null) return detailed;
	    return md;
	}
	return defaultStaffModel;
    }

    public static void load()
    {
	addStaffModel(ModBase.hemmyStaff, new ModelStaffHemmy());
	addStaffModel(ModBase.staffGrand, new ModelStaffGrand());
	addStaffModel(ModBase.staffSuper, new ModelStaffSuper());
	addStaffModel(ModBase.staffBlessing, new ModelStaffBlessing());
	addStaffModel(ModBase.staffDestruction, new ModelStaffDestruction());
	addStaffModel(ModBase.staffTelekinesis, new ModelStaffTelekinesis());
	addStaffModel(ModBase.staffManipulation, new ModelStaffManipulation());
    }

    public static void addStaffModel(Item i, ModelStaff model)
    {
	if (i instanceof Staff)
	{
	    staffModels.put((Staff) i, model);
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
	preRender(staff);
	GL11.glPushMatrix();
	GL11.glScaled(1 / 16D, 1 / 16D, 1 / 16D);
	renderComponents(staff);
	GL11.glPopMatrix();
    }

    public void preRender(ItemStack staff)
    {
    }

    public void renderComponents(ItemStack staff)
    {
	renderList(staff, components);
    }

    public void renderList(ItemStack staff, List<ModelObject> l)
    {
	for (ModelObject o : l)
	{
	    GL11.glPushMatrix();
	    componentPreRender(staff, o);
	    o.render();
	    componentPostRender(staff, o);
	    GL11.glPopMatrix();
	}
    }

    public void componentPreRender(ItemStack staff, ModelObject o)
    {

    }

    public void componentPostRender(ItemStack staff, ModelObject o)
    {

    }
}
