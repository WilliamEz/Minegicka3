package com.williameze.minegicka3.main.spells;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;

import com.williameze.api.lib.FuncHelper;
import com.williameze.api.math.Vector;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.entities.EntityMine;
import com.williameze.minegicka3.main.entities.EntityStorm;
import com.williameze.minegicka3.main.objects.TileEntityShield;
import com.williameze.minegicka3.main.objects.TileEntityWall;
import com.williameze.minegicka3.main.spells.Spell.CastType;
import com.williameze.minegicka3.main.spells.Spell.SpellType;

public class SpellExecuteGrounded extends SpellExecute
{
    @Override
    public void startSpell(Spell s)
    {
	boolean area = s.castType == CastType.Area;
	Entity caster = s.getCaster();
	if (caster == null) return;

	List<Element> l = new ArrayList();
	l.addAll(s.elements);
	l.removeAll(Collections.singleton(Element.Shield));
	Spell s1 = new Spell(l, s.dimensionID, s.casterUUID, s.castType, s.additionalData);

	int count = s.countElements();
	double manaCost = (count == 1 ? 100 : (count - 1) * (count - 1) * 100) * (area ? 4 : 1) * s.getManaConsumeRate();
	if (consumeMana(s, manaCost, true, true, 3) >= 1)
	{
	    if (caster.worldObj.isRemote) return;
	    Vector look = new Vector(caster.getLookVec());
	    if (s.countElements() == 1)
	    {
		double radius = 6 * s.getPower();
		Vector center = FuncHelper.getCenter(caster);
		int minX = (int) Math.round(center.x - radius);
		int minY = (int) Math.round(center.y - radius);
		int minZ = (int) Math.round(center.z - radius);
		int maxX = (int) Math.round(center.x + radius);
		int maxY = (int) Math.round(center.y + radius);
		int maxZ = (int) Math.round(center.z + radius);
		for (int x = minX; x <= maxX; x++)
		{
		    for (int y = minY; y <= maxY; y++)
		    {
			for (int z = minZ; z <= maxZ; z++)
			{
			    double distSqr = (center.x - x) * (center.x - x) + (center.y - y) * (center.y - y) + (center.z - z)
				    * (center.z - z);
			    int distInt = (int) Math.ceil(Math.sqrt(distSqr));
			    int radiusInt = (int) Math.floor(radius);
			    if (distInt == radiusInt)
			    {
				if (caster.worldObj.isAirBlock(x, y, z) || caster.worldObj.getBlock(x, y, z).getMaterial().isReplaceable())
				{
				    boolean inAngle = area;
				    if (!area)
				    {
					Vector v = new Vector(x + 0.5, y + 0.5, z + 0.5).subtract(center);
					double angle = v.getAngleBetween(look);
					inAngle = angle <= Math.PI / 3D;
				    }
				    if (inAngle)
				    {
					caster.worldObj.setBlock(x, y, z, ModBase.shieldBlock);
					TileEntityShield tile = new TileEntityShield();
					tile.setSpell(s);
					caster.worldObj.setTileEntity(x, y, z, tile);
					caster.worldObj.markBlockForUpdate(x, y, z);
				    }
				}
			    }
			}
		    }
		}
	    }
	    else if (s1.spellType == SpellType.Projectile)
	    {
		double radius = 6 + count * s.getPower();
		Vector center = FuncHelper.getCenter(caster);
		int minX = (int) Math.round(center.x - radius);
		int minY = (int) Math.round(center.y - Math.sqrt(count) - 2);
		int minZ = (int) Math.round(center.z - radius);
		int maxX = (int) Math.round(center.x + radius);
		int maxY = (int) Math.round(center.y + Math.sqrt(count) + 2);
		int maxZ = (int) Math.round(center.z + radius);
		for (int x = minX; x <= maxX; x++)
		{
		    for (int y = minY; y <= maxY; y++)
		    {
			for (int z = minZ; z <= maxZ; z++)
			{
			    double distSqr = (center.x - x) * (center.x - x) + (center.z - z) * (center.z - z);
			    int distInt = (int) Math.ceil(Math.sqrt(distSqr));
			    int radiusInt = (int) Math.floor(radius);
			    if (distInt == radiusInt)
			    {
				if (caster.worldObj.isAirBlock(x, y, z) || caster.worldObj.getBlock(x, y, z).getMaterial().isReplaceable())
				{
				    boolean inAngle = area;
				    if (!area)
				    {
					Vector v = new Vector(x + 0.5, center.y, z + 0.5).subtract(center);
					double angle = v.getAngleBetween(new Vector(look.x, 0, look.z));
					inAngle = angle <= Math.PI / 3D;
				    }
				    if (inAngle)
				    {
					caster.worldObj.setBlock(x, y, z, ModBase.wallBlock);
					TileEntityWall tile = new TileEntityWall();
					tile.setSpell(s1);
					caster.worldObj.setTileEntity(x, y, z, tile);
				    }
				}
			    }
			}
		    }
		}
	    }
	    else
	    {
		look.y = 0;
		look.normalize();
		if (look == null || look.isZeroVector()) look = Vector.unitX.copy();

		double radius = s.countElements() + 3;
		int loops = (int) Math.round(radius * (area ? 4 : 1)) + (area ? 0 : 1);
		double angleDif = Math.PI / 2D * (area ? 4 : 1) / loops;
		double initialFirstRotation = area ? 0 : -(loops / 2D - 0.5) * angleDif;
		Vector vec = look.rotateAround(Vector.unitY, initialFirstRotation);

		if (s1.spellType == SpellType.Spray || s1.spellType == SpellType.Lightning)
		{
		    if (s1.spellType == SpellType.Lightning) radius *= 1.2;
		    for (int a = 0; a < loops; a++)
		    {
			Vector dir = vec.rotateAround(Vector.unitY, angleDif * a);
			EntityStorm storm = new EntityStorm(caster.worldObj);
			storm.spell = new Spell(l, s.dimensionID, storm.getPersistentID(), CastType.Single, s.additionalData);
			storm.maxTick = 75 + s.countElements() * 25;
			storm.setPosition(caster.posX + dir.x * radius, caster.posY + 2 + Math.pow(s.countElements(), 0.85), caster.posZ
				+ dir.z * radius);
			caster.worldObj.spawnEntityInWorld(storm);
			storm.spell.startSpell();
		    }
		}
		if (s1.spellType == SpellType.Beam)
		{
		    for (int a = 0; a < loops; a++)
		    {
			Vector dir = vec.rotateAround(Vector.unitY, angleDif * a);
			EntityMine mine = new EntityMine(caster.worldObj);
			mine.spell = new Spell(l, s.dimensionID, mine.getPersistentID(), CastType.Area, s.additionalData);
			mine.setPosition(caster.posX + dir.x * radius, caster.posY + 2, caster.posZ + dir.z * radius);
			caster.worldObj.spawnEntityInWorld(mine);
		    }
		}
	    }
	}
	s.toBeInvalidated = true;
    }
}
