package com.williameze.minegicka3.main.objects.items;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.williameze.minegicka3.main.entities.monsters.Entity888;
import com.williameze.minegicka3.main.worldgen.Gen;

public class ItemTest extends Item
{
    @Override
    public boolean onItemUse(ItemStack is, EntityPlayer p, World world, int x, int y, int z, int side, float par8, float par9, float par10)
    {
	if (world.isRemote == false)
	{
	    Random rnd = new Random();
	    int radius = 18;
	    int wall = 2;
	    Gen.setBlockRoom(world, x - radius, y - 4, z - radius, x + radius, y + 16, z + radius, Blocks.nether_brick, wall, true);
	    for (int a = 0; a < radius; a++)
	    {
		int x1 = x - radius + wall + rnd.nextInt((radius - wall) * 2 + 1);
		int z1 = z - radius + wall + rnd.nextInt((radius - wall) * 2 + 1);
		world.setBlock(x1, y - 4 + wall - 1, z1, Blocks.glowstone);
		world.setBlock(x1, y + 16 - wall + 1, z1, Blocks.glowstone);
	    }

	    Entity888 e888 = new Entity888(world);
	    e888.setPosition(x, y-2, z);
	    world.spawnEntityInWorld(e888);
	}
	return true;
    }
}
