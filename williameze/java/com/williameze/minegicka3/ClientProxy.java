package com.williameze.minegicka3;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.transformers.ForgeAccessTransformer;

import com.williameze.minegicka3.core.rendering.RenderStaff;
import com.williameze.minegicka3.objects.ItemStaff;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderHandler()
    {
    }

    @Override
    public void registerItemRenderer(Item i)
    {
	if(i instanceof ItemStaff)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderStaff());
	}
    }
}
