package com.williameze.minegicka3.main.renders;

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
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.entities.EntityBeam;
import com.williameze.minegicka3.main.entities.EntityLightning;

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
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glTranslated(x, y, z);

	doTheRender(b, partialTick);

	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glColor4d(1, 1, 1, 1);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glPopMatrix();
    }

    public void doTheRender(EntityBeam beam, float partialTick)
    {
	Vector towardTarget = beam.towardTarget;
	Vector towardNor = towardTarget.normalize();
	List<Element> l = beam.spell.elements;
	double radius = 0.13 * Math.pow(l.size(), 0.2);
	double radius1 = radius / 2;

	GL11.glRotated((Values.clientTicked + partialTick) * 5 / Math.sqrt(beam.spell.countElements()), towardNor.x, towardNor.y, towardNor.z);

	GL11.glPushMatrix();
	Cylinder cli = new Cylinder(Vector.root, towardTarget, towardTarget, towardTarget.reverse(), radius1, radius1, 8);
	if (l.contains(Element.Arcane)) cli.setColor(Color.red);
	else cli.setColor(Color.green);
	cli.render();
	GL11.glPopMatrix();

	Vector base;
	{
	    Plane p = new Plane(towardNor, 0);
	    base = p.getAssurancePoint();
	    base.setToLength(radius);
	}
	int loops = l.size();
	int towardCuts = (int) Math.round(towardTarget.lengthVector());
	for (int a = 0; a < loops; a++)
	{
	    base = base.rotateAround(towardNor, Math.PI * 2 / loops);
	    Element e = l.get(a % l.size());
	    /**
	     * switch (e) { case Arcane: GL11.glColor4d(1, 0, 0, 0.5); break;
	     * case Cold: GL11.glColor4d(1, 1, 1, 0.5); break; case Steam:
	     * GL11.glColor4d(0.5, 0.5, 0.5, 0.5); break; case Life:
	     * GL11.glColor4d(0, 1, 0, 0.5); break; case Earth:
	     * GL11.glColor4d(0.4, 0.3, 0, 0.5); break; case Fire:
	     * GL11.glColor4d(1, 0.3, 0, 0.5); break; case Ice:
	     * GL11.glColor4d(0, 1, 1, 0.5); break; case Lightning:
	     * GL11.glColor4d(1, 0, 1, 0.5); break; case Shield:
	     * GL11.glColor4d(1, 1, 0, 0.5); break; case Water:
	     * GL11.glColor4d(0, 0, 1, 0.5); break; default: GL11.glColor4d(1,
	     * 0, 0, 0.5); break; }
	     **/
	    // GL11.glLineWidth(2);
	    // GL11.glBegin(GL11.GL_LINE_STRIP);
	    for (int b = 0; b <= towardCuts - 1; b++)
	    {
		Vector thisPoint = towardTarget.multiply((double) b / (double) towardCuts).add(
			base.rotateAround(towardNor, Math.PI / 8D * b));

		Vector thatPoint = towardTarget.multiply((double) (b + 1) / (double) towardCuts).add(
			base.rotateAround(towardNor, Math.PI / 8D * (b + 1)));
		// GL11.glVertex3d(thisPoint.x, thisPoint.y, thisPoint.z);
		Cylinder.create(thisPoint, thatPoint, 0.002, 2).setColor(e.getColor()).render();
	    }
	    // GL11.glEnd();
	    // GL11.glLineWidth(1);
	}
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
