package com.williameze.api.models;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.Values;

public abstract class FlatOpposing extends ModelObject
{
    public Vector opposeDirection;
    public double orX, orY, orZ;

    public FlatOpposing setOpposing(Vector v)
    {
	opposeDirection = v;
	return this;
    }

    public FlatOpposing setOpposing(double x, double y, double z)
    {
	opposeDirection = new Vector(x, y, z);
	return this;
    }

    @Override
    public void render()
    {
	double xRot = Math.acos(opposeDirection.normalize().y) / Math.PI * 180 - 90;

	Vector ofY = new Vector(opposeDirection.z, opposeDirection.x, 0).normalize();
	double yRot = ofY.isZeroVector() ? 0 : -Math.atan2(ofY.x, ofY.y) / Math.PI * 180D + 90D;

	GL11.glPushMatrix();
	GL11.glTranslated(orX, orY, orZ);
	GL11.glRotated(yRot, 0, 1, 0);
	GL11.glRotated(xRot, 1, 0, 0);
	flatRender();
	glResetColor();
	GL11.glPopMatrix();
    }

    public abstract void flatRender();
}
