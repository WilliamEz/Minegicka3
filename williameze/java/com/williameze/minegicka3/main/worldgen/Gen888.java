package com.williameze.minegicka3.main.worldgen;

import static net.minecraftforge.common.ChestGenHooks.DUNGEON_CHEST;

import java.util.Random;

import com.williameze.minegicka3.main.entities.monsters.Entity888;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DungeonHooks;

public class Gen888 extends Gen
{
    @Override
    public boolean shouldGenAt(World world, int chunkX, int chunkZ, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
	if (world.provider.dimensionId != 0) return false;

	if ((chunkX == 55 || chunkX == -56) && (chunkZ == 55 || chunkZ == -56))
	{
	    return true;
	}

	int xmin = Math.abs(chunkX * 16) - (chunkX < 0 ? 15 : 0);
	int zmin = Math.abs(chunkZ * 16) - (chunkZ < 0 ? 15 : 0);
	if (xmin < 870 || zmin < 870)
	{
	    return false;
	}
	else
	{
	    String xm = xmin + "";
	    String zm = zmin + "";
	    int xminend = Integer.parseInt(xm.substring(Math.max(0, xm.length() - 3), Math.max(0, xm.length() - 1)));
	    int zminend = Integer.parseInt(zm.substring(Math.max(0, zm.length() - 3), Math.max(0, zm.length() - 1)));
	    if (Math.abs(xminend - 888) < 16 && Math.abs(zminend - 888) < 16 && rnd.nextInt(8) == 0)
	    {
		return true;
	    }
	}
	return false;
    }

    @Override
    public void generate(World world, int chunkX, int chunkZ, Random rnd, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
	Block b1 = Blocks.nether_brick;
	int midX = chunkX * 16;
	int midZ = chunkZ * 16;
	for (; midX < chunkX * 16 + 16; midX++)
	{
	    if (String.valueOf(midX).endsWith("888")) break;
	}
	for (; midZ < chunkZ * 16 + 16; midZ++)
	{
	    if (String.valueOf(midZ).endsWith("888")) break;
	}

	int radius = 8;
	int topY = 0;
	for (int x = midX - radius; x <= midX + radius; x++)
	{
	    for (int z = midZ - radius; z <= midZ + radius; z++)
	    {
		if (x == midX - radius || x == midX + radius || z == midZ - radius || z == midZ + radius)
		{
		}
		else continue;

		for (topY = world.getHeight(); topY >= 0; topY--)
		{
		    Material mat = world.getBlock(x, topY, z).getMaterial();
		    if (mat.isSolid() || mat.isLiquid()) break;
		}

		if (topY < 188)
		{
		    for (int y = topY; y <= 188; y++)
		    {
			if (y - topY <= 4)
			{
			    world.setBlock(x, y, z, world.getBlock(x, topY, z));
			}
			else if (Math.abs(x + y + z) % 8 == 0)
			{
			    world.setBlock(x, y, z, Blocks.glowstone);
			}
			else
			{
			    world.setBlock(x, y, z, b1);
			}

			if (x == midX - radius && z == midZ - radius && y > topY + 4 && y < 180 && y % 8 == 0)
			{
			    setBlockRoom(world, midX - radius + 1, y, midZ - radius + 1, midX + radius - 1, y, midZ + radius - 1, b1, -1, false);

			    world.setBlock(midX, y + 1, midZ, Blocks.chest);
			    TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(midX, y + 1, midZ);
			    if (tileChest != null)
			    {
				WeightedRandomChestContent.generateChestContents(rnd, ChestGenHooks.getItems(DUNGEON_CHEST, rnd), tileChest,
					8 + rnd.nextInt(11));
			    }

			    int count = rnd.nextInt(8);
			    for (int a = 0; a < count; a++)
			    {
				int mobX = midX - radius + 1 + rnd.nextInt(radius * 2 - 1);
				int mobZ = midZ - radius + 1 + rnd.nextInt(radius * 2 - 1);
				world.setBlock(mobX, y + 1, mobZ, Blocks.mob_spawner, 0, 2);
				TileEntityMobSpawner tileMob = (TileEntityMobSpawner) world.getTileEntity(mobX, y + 1, mobZ);
				if (tileMob != null)
				{
				    tileMob.func_145881_a().setEntityName(DungeonHooks.getRandomDungeonMob(rnd));
				}
				else
				{
				    world.setBlockToAir(mobX, y + 1, mobZ);
				}
			    }
			}
		    }
		}
	    }
	}

	radius = 18;
	int wall = 2;
	setBlockRoom(world, midX - radius, 188, midZ - radius, midX + radius, 208, midZ + radius, b1, wall, true);
	for (int a = 0; a < radius; a++)
	{
	    int x = midX - radius + wall + rnd.nextInt((radius - wall) * 2 + 1);
	    int z = midZ - radius + wall + rnd.nextInt((radius - wall) * 2 + 1);
	    world.setBlock(x, 188 + wall - 1, z, Blocks.glowstone);
	    world.setBlock(x, 208 - wall + 1, z, Blocks.glowstone);
	}

	Entity888 e888 = new Entity888(world);
	e888.setPosition(midX, 190, midZ);
	world.spawnEntityInWorld(e888);
    }
}
