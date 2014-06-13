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
public class ModelStaffDestruction extends ModelStaff
{
    @Override
    public void addComponents()
    {
	components.add(Cylinder.create(new Vector(0, 8, 0), new Vector(0, -6, 0), 0.4685, 16).setColor(Values.yellow));

	components.add(new Sphere(0, 10, 0, 2.75, 2, 4).setColor(new Color(40, 40, 40)));
	components.add(new Sphere(0, 8, 0, 2, 2, 6).setColor(new Color(60, 0, 0)));
	components.add(new Sphere(0, 6.5, 0, 1.5, 2, 8).setColor(new Color(120, 0, 0)));

	components.add(new Sphere(0, -6, 0, 1.5, 16, 32).setColor(new Color(40, 0, 0)));
    }
}
