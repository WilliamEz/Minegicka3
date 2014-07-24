package com.williameze.minegicka3.main.objects.items.models.staff;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.api.models.CylinderConjunc;
import com.williameze.api.models.ModelObject;
import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelStaffGrand extends ModelStaff
{
    @Override
    public void addComponents()
    {
	Color c = Values.yellow;
	Color c1 = Values.cyan;

	components.add(CylinderConjunc.createSpiral(new Vector(0, 8, 0), new Vector(0, 11, 0), Vector.unitX, -Math.PI * 0.5,
		Math.PI * 0.15, 12, 0, 0.4685, 16).setColor(c1));
	components.add(Cylinder.create(new Vector(0, 11, -1.25), new Vector(0, 11, -2.875), 0.0625, 0.4685, 16, 0).setColor(c1));
	components.add(Cylinder.create(new Vector(0, 11, -2.875), new Vector(0, 11, -4.5), 0.4685, 0.0625, 16, 0).setColor(c1));
	components.add(Cylinder.create(new Vector(0, 8, 0), new Vector(0, -6, 0), 0.4685, 16).setColor(c));
	components.add(new Sphere(0, -6.25, 0, 0.75, 2, 4).setColor(c1));
    }
}
