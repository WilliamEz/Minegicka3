package com.williameze.minegicka3.main.entities.magic.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Plane;
import com.williameze.api.math.PositionedVector;
import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.entities.magic.EntityBeam;
import com.williameze.minegicka3.main.entities.magic.EntityLightning;
import com.williameze.minegicka3.mechanics.Element;

public class RenderEntityBeam extends Render
{
    protected void bindEntityTexture(Entity par1Entity)
    {
    }

    protected void bindTexture(ResourceLocation par1ResourceLocation)
    {
    }

    @Override
    public void doRender(Entity var1, double x, double y, double z, float yaw, float partialTick)
    {
	renderBeam((EntityBeam) var1, x, y, z, yaw, partialTick);
    }

    public void renderBeam(EntityBeam b, double x, double y, double z, float yaw, float partialTick)
    {
	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_DEPTH_TEST);
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glTranslated(x, y, z);

	doTheRender(b, partialTick);

	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glColor4d(1, 1, 1, 1);
	GL11.glPopMatrix();
    }

    public void doTheRender(EntityBeam beam, float partialTick)
    {
	Vector towardTarget = beam.towardTarget;
	Vector towardNor = towardTarget.normalize();
	List<Element> l = beam.spell.elements;
	double radiusInner = 0.06 * Math.pow(l.size(), 0.15);
	double radiusOuter = radiusInner * 1.5;

	GL11.glRotated((Values.clientTicked + partialTick) * 40 / Math.sqrt(beam.spell.countElements()), towardNor.x, towardNor.y, towardNor.z);

	GL11.glPushMatrix();
	Cylinder cli = new Cylinder(Vector.root, towardTarget, towardTarget, towardTarget.reverse(), radiusInner, radiusInner, 8);
	if (l.contains(Element.Arcane)) cli.setColor(Color.red);
	else cli.setColor(Color.green);
	cli.render();
	GL11.glPopMatrix();

	Vector base;
	{
	    Plane p = new Plane(towardNor, 0);
	    base = p.getAssurancePoint();
	    base.setToLength(radiusOuter);
	}
	int loops = l.size();
	int towardCuts = (int) Math.round(Math.pow(towardTarget.lengthVector(), 0.6) * 8);
	for (int a = 0; a < loops; a++)
	{
	    base = base.rotateAround(towardNor, Math.PI * 2 / loops);
	    Element e = l.get(a % l.size());
	    for (int b = 0; b <= towardCuts - 1; b++)
	    {
		Vector thisPoint = towardTarget.multiply((double) b / (double) towardCuts).add(base.rotateAround(towardNor, Math.PI / 8D * b));
		Vector thatPoint = towardTarget.multiply((double) (b + 1) / (double) towardCuts).add(
			base.rotateAround(towardNor, Math.PI / 8D * (b + 1)));
		Cylinder.create(thisPoint, thatPoint, 0.01, 4).setColor(e.getColor()).render();
	    }
	}
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
