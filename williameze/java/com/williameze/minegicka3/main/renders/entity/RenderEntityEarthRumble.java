package com.williameze.minegicka3.main.renders.entity;

import java.awt.Color;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Box;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.entities.magic.EntityEarthRumble;

public class RenderEntityEarthRumble extends Render
{
    public RenderBlocks rb;
    Box box = Box.create(new Vector(0.5, 0.5, 0.5), 0.5);

    protected void bindEntityTexture(Entity par1Entity)
    {
    }

    protected void bindTexture(ResourceLocation par1ResourceLocation)
    {
    }

    @Override
    public void doRender(Entity var1, double x, double y, double z, float yaw, float partialTick)
    {
	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glTranslated(x, y, z);

	doTheRender((EntityEarthRumble) var1, partialTick);

	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glPopMatrix();
    }

    public void doTheRender(EntityEarthRumble rum, float partialTick)
    {
	GL11.glTranslated(-(rum.prevPosX + (rum.posX - rum.prevPosX) * partialTick), -(rum.prevPosY + (rum.posY - rum.prevPosY) * partialTick),
		-(rum.prevPosZ + (rum.posZ - rum.prevPosZ) * partialTick));
	World world = rum.worldObj;
	double ticked = rum.ticksExisted + partialTick;
	int intervalElapsed = (int) Math.floor(ticked / rum.interval());
	double currentMaxRange = intervalElapsed * rum.interval() / (double) rum.maxTick() * rum.maxRange();
	double currentMaxRangeSqr = currentMaxRange * currentMaxRange;
	double tickForEachBump = 5;

	int minX = (int) Math.floor(rum.posX - currentMaxRange);
	int minY = (int) Math.floor(rum.posY - rum.height / 2);
	int minZ = (int) Math.floor(rum.posZ - currentMaxRange);
	int maxX = (int) Math.floor(rum.posX + currentMaxRange);
	int maxY = (int) Math.floor(rum.posY + rum.height / 2);
	int maxZ = (int) Math.floor(rum.posZ + currentMaxRange);
	for (int x = minX; x <= maxX; x++)
	{
	    for (int y = minY; y <= maxY; y++)
	    {
		for (int z = minZ; z <= maxZ; z++)
		{
		    double distSqr = rum.getDistanceSq(x, rum.posY, z);
		    if (!world.isAirBlock(x, y, z) && !world.getBlock(x, y + 1, z).isOpaqueCube() && distSqr <= currentMaxRangeSqr)
		    {
			double dist = Math.sqrt(distSqr);
			int bumpedWhen = (int) Math.ceil((dist / rum.maxRange()) / (rum.interval() / (double) rum.maxTick())) * rum.interval();
			double timeElapsedSinceBump = ticked - bumpedWhen;
			if (timeElapsedSinceBump <= tickForEachBump)
			{
			    double bumpRate = timeElapsedSinceBump / tickForEachBump * 2D;
			    if (bumpRate > 1) bumpRate = 2 - bumpRate;
			    GL11.glPushMatrix();
			    GL11.glTranslated(x, y + bumpRate, z);
			    Color c = Element.Earth.getColor();
			    int index = (x / 4 + y / 4 + z / 4) % rum.getSpell().elements.size();
			    if (index>=0 && index < rum.getSpell().elements.size()) c = rum.getSpell().elements.get(index).getColor();
			    box.setColor(c.getRGB(), 180);
			    box.render();
			    GL11.glPopMatrix();
			    world.spawnParticle(
				    "blockcrack_" + Block.getIdFromBlock(world.getBlock(x, y, z)) + "_" + world.getBlockMetadata(x, y, z), x, y + 1,
				    z, 0, 0.2, 0);
			}
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
