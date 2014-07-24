package com.williameze.minegicka3.main.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public abstract class Gen
{
    public static List<Gen> genListeners = new ArrayList();

    public static void load()
    {
	genListeners.add(new Gen888());
    }

    public Random rnd = new Random();

    public boolean shouldGenAt(World world, int chunkX, int chunkZ, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
	return false;
    }

    public abstract void generate(World world, int chunkX, int chunkZ, Random rnd, IChunkProvider chunkGenerator, IChunkProvider chunkProvider);

    public static void setBlockRoom(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block block, int wallThickness,
	    boolean emptyInside)
    {
	for (int x = minX; x <= maxX; x++)
	{
	    for (int y = minY; y <= maxY; y++)
	    {
		for (int z = minZ; z <= maxZ; z++)
		{
		    if (wallThickness == -1)
		    {
			world.setBlock(x, y, z, block);
		    }
		    else if (x - minX < wallThickness || maxX - x < wallThickness || y - minY < wallThickness || maxY - y < wallThickness
			    || z - minZ < wallThickness || maxZ - z < wallThickness)
		    {
			world.setBlock(x, y, z, block);
		    }
		    else if (emptyInside)
		    {
			world.setBlockToAir(x, y, z);
		    }
		}
	    }
	}
    }
}
