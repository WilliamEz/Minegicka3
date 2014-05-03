package com.williameze.minegicka3.main.renders;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.PositionedVector;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.entities.EntityLightning;
import com.williameze.minegicka3.main.spells.Spell.CastType;

public class RenderEntityLightning extends Render
{
    Map<Entry<EntityLightning, Integer>, List<PositionedVector>> lightningsList = new HashMap();

    protected void bindEntityTexture(Entity par1Entity)
    {
    }

    protected void bindTexture(ResourceLocation par1ResourceLocation)
    {
    }

    @Override
    public void doRender(Entity var1, double x, double y, double z, float yaw, float partialTick)
    {
	renderLightning((EntityLightning) var1, x, y, z, yaw, partialTick);
    }

    public void renderLightning(EntityLightning l, double x, double y, double z, float yaw, float partialTick)
    {
	if (l.spell.getCaster() == null) return;
	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_DEPTH_TEST);
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_LIGHTING);
	GL11.glTranslated(x, y, z);

	doTheRender(l, 0, partialTick);
	doTheRender(l, 3, partialTick);

	GL11.glEnable(GL11.GL_LIGHTING);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glPopMatrix();
    }

    public void doTheRender(EntityLightning l, int dif, float partialTick)
    {
	int ticked = (l.ticksExisted + dif) % 4;
	List<PositionedVector> list = lightningsList.get(new SimpleEntry(l, dif));
	if (ticked == 0) list = null;
	if (list == null)
	{
	    list = getLightningsFragments(l, l.originAndChainedMap);
	    lightningsList.put(new SimpleEntry(l, dif), list);
	}
	GL11.glLineWidth(1F + (float) Math.abs(Math.pow(ticked, 1)));
	for (int a = 0; a < list.size(); a++)
	{
	    GL11.glBegin(GL11.GL_LINES);
	    setRandomColor(l, a);
	    PositionedVector pv = list.get(a);
	    GL11.glVertex3d(pv.pos.x, pv.pos.y, pv.pos.z);
	    GL11.glVertex3d(pv.pos.x + pv.dir.x, pv.pos.y + pv.dir.y, pv.pos.z + pv.dir.z);
	    GL11.glColor3d(1, 1, 1);
	    GL11.glEnd();
	}
	GL11.glLineWidth(1);
    }

    public void setRandomColor(EntityLightning l, int a)
    {
	Element e = l.spell.elements.get((int) Math.floor(a / 7D) % l.spell.elements.size());
	if (e == Element.Water) GL11.glColor3d(0.4, 0.4, 1);
	else if (e == Element.Fire) GL11.glColor3d(1, 0.7, 0.4);
	else if (e == Element.Cold) GL11.glColor3d(1, 1, 1);
	else if (e == Element.Steam) GL11.glColor3d(0.4, 0.4, 0.4);
	else GL11.glColor3d(1, 0.85, 1);
    }

    public List<PositionedVector> getLightningsFragments(EntityLightning li, Map<Entity, List<Entity>> m)
    {
	List<PositionedVector> l = new ArrayList();
	Iterator<Entry<Entity, List<Entity>>> ite = m.entrySet().iterator();
	while (ite.hasNext())
	{
	    Entry<Entity, List<Entity>> entry = ite.next();
	    for (Entity e : entry.getValue())
	    {
		l.addAll(getLightningFragments(li, entry.getKey(), e));
	    }
	}
	if (l.isEmpty())
	{
	    if (li.spell.castType == CastType.Single)
	    {
		double d = li.getSeekRadius(0);
		Vector v = new Vector(li.spell.getCaster().getLookVec()).multiply(d).randomizeDirection(0.6);
		subdivideVector(0, 0, 0, v, v.lengthVector() / 8, 8, l);
	    }
	}
	if (li.spell.castType == CastType.Area)
	{
	    double d = li.getSeekRadius(0);
	    Vector v = new Vector(d, 0, 0);
	    for (int a = 0; a < 6; a++)
	    {
		Vector v1 = v.rotateAround(Vector.unitY, a * Math.PI / 3);
		Vector v2 = v.rotateAround(Vector.unitY, (a + 1) * Math.PI / 3);
		subdivideVector(0, 0, 0, v1, v1.lengthVector() / 4, 6, l);
		subdivideVector(v1.x, v1.y, v1.z, v2.subtract(v1), v2.subtract(v1).lengthVector() / 2, 8, l);
	    }
	}
	return l;
    }

    public List<PositionedVector> getLightningFragments(EntityLightning li, Entity from, Entity to)
    {
	List<PositionedVector> l = new ArrayList();
	Vector center = FuncHelper.getCenter(from);
	double x = center.x - li.posX;
	double y = center.y - li.posY;
	double z = center.z - li.posZ;
	Vector v = FuncHelper.vectorToCenterEntity(from, to).randomizeDirection(Math.min(to.width / 2, to.height / 2));
	subdivideVector(x, y, z, v, v.lengthVector() / 8, 8, l);
	return l;
    }

    public void subdivideVector(double x, double y, double z, Vector v, double maxDisturbance, int index, List<PositionedVector> l)
    {
	Random rnd = new Random();
	double branchChance = 0.12;
	double minDisturbance = maxDisturbance / 16;

	if (index < 0)
	{
	    l.add(new PositionedVector(x, y, z, v));
	}
	else
	{
	    double midX = x + v.x / 2;
	    double midY = y + v.y / 2;
	    double midZ = z + v.z / 2;
	    double disturbanceX = 2 * (rnd.nextDouble() - 0.5) * maxDisturbance;
	    double disturbanceY = 2 * (rnd.nextDouble() - 0.5) * maxDisturbance;
	    double disturbanceZ = 2 * (rnd.nextDouble() - 0.5) * maxDisturbance;
	    if (disturbanceX < minDisturbance) disturbanceX = 0;
	    if (disturbanceY < minDisturbance) disturbanceY = 0;
	    if (disturbanceZ < minDisturbance) disturbanceZ = 0;
	    if (disturbanceX == 0 && disturbanceY == 0 && disturbanceZ == 0)
	    {
		l.add(new PositionedVector(x, y, z, v));
	    }
	    else
	    {
		double length = v.lengthVector();
		midX += disturbanceX;
		midY += disturbanceY;
		midZ += disturbanceZ;
		double d = 1D / 8;
		Vector v1 = new Vector(midX - x, midY - y, midZ - z);
		Vector v2 = new Vector(x + v.x - midX, y + v.y - midY, z + v.z - midZ);
		index--;
		subdivideVector(x, y, z, v1, v1.lengthVector() * d, index, l);
		subdivideVector(midX, midY, midZ, v2, v2.lengthVector() * d, index, l);
		if (rnd.nextDouble() <= branchChance * index)
		{
		    Vector v3 = new Vector(rnd.nextDouble() - 0.5, rnd.nextDouble() - 0.5, rnd.nextDouble() - 0.5);
		    Vector v4 = v1.multiply(0.5);
		    if (v4.dotProduct(v3) > 0)
		    {
			v3.setToLengthOf(v4);
			v4 = v3;
			subdivideVector(midX, midY, midZ, v4, v4.lengthVector() * d, index, l);
		    }
		}
	    }
	}
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
