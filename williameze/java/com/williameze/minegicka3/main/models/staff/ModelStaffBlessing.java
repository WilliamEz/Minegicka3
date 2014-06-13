package com.williameze.minegicka3.main.models.staff;

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
public class ModelStaffBlessing extends ModelStaff
{
    @Override
    public void addComponents()
    {
	components.add(Cylinder.create(new Vector(0, 7, 0), new Vector(0, -6, 0), 0.4685, 16).setColor(Values.yellow));
	components.add(new Sphere(0, -6.25, 0, 0.75, 2, 4).setColor(new Color(100, 255, 100)));

	components.add(new Sphere(0, 10, 0, 3, 16, 32).setColor(new Color(100, 255, 100)));
	components.add(CylinderConjunc.createTorus(new Vector(3, 10, 0), new Vector(0, 10, 0), Vector.unitY, 0, 32, 0.35, 8).setColor(Values.yellow));
	components.add(CylinderConjunc.createSpiral(new Vector(-3, 10, 0), new Vector(0, 10, 0), Vector.unitZ, 0, Math.PI / 16, 16, 0, 0.35, 8)
		.setColor(Values.yellow));
	components.add(CylinderConjunc.createSpiral(new Vector(0, 10, 3), new Vector(0, 10, 0), Vector.unitX, 0, Math.PI / 16, 16, 0, 0.35, 8)
		.setColor(Values.yellow));
	components.add(Cylinder.create(new Vector(0, 13, 0), new Vector(0, 16, 0), 0.35, 16).setColor(Values.yellow));
	components.add(Cylinder.create(new Vector(0, 14.5, -1.5), new Vector(0, 14.5, 1.5), 0.35, 16).setColor(Values.yellow));
    }

}
