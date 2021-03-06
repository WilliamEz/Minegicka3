package com.williameze.minegicka3.mechanics.magicks;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.objects.items.Staff;
import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.spells.Spell;
import com.williameze.minegicka3.mechanics.spells.Spell.CastType;

public class MagickWaterShock extends Magick
{

    public MagickWaterShock()
    {
	super("Water Shock", "HD");
    }

    @Override
    public List<String> getDescription()
    {
        return Arrays.asList(new String[]{
        	"Electrically shocks nearby wet entities.",
        	"Radius of effect: 16 x staff's power (blocks)",
        	"Shock damage: 1 x staff's power"
        });
    }

    @Override
    public Object[] getAdditionalUnlockMaterials()
    {
	return new Object[] { ModBase.essenceLightning, 4 };
    }

    @Override
    public double getBaseManaCost()
    {
	return 100;
    }

    @Override
    public void doTheMagick(World world, double x, double y, double z, Entity caster, NBTTagCompound additionalData)
    {
	double[] props = getStaffMainProperties(additionalData);
	double radius = 16 * Math.min(props[0], 10);
	List<Entity> l = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(radius, radius * 2, radius));
	l.remove(caster);
	if (!l.isEmpty())
	{
	    Spell s = new Spell(Arrays.asList(Element.Lightning), world.provider.dimensionId, caster.getPersistentID(),
		    caster instanceof EntityPlayer ? ((EntityPlayer) caster).getGameProfile().getName() : null, CastType.Single, null);
	    for (int a = 0; a < l.size(); a++)
	    {
		if (l.get(a).isWet()) s.damageEntity(l.get(a), 1);
	    }
	}
    }
}
