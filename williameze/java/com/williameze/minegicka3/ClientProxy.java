package com.williameze.minegicka3;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.core.CoreClient;
import com.williameze.minegicka3.main.entities.EntityBeam;
import com.williameze.minegicka3.main.entities.EntityLightning;
import com.williameze.minegicka3.main.entities.EntitySprayCold;
import com.williameze.minegicka3.main.entities.EntitySprayFire;
import com.williameze.minegicka3.main.entities.EntitySpraySteam;
import com.williameze.minegicka3.main.entities.EntitySprayWater;
import com.williameze.minegicka3.main.objects.ItemStaff;
import com.williameze.minegicka3.main.renders.RenderEntityBeam;
import com.williameze.minegicka3.main.renders.RenderEntityLightning;
import com.williameze.minegicka3.main.renders.RenderEntitySpray;
import com.williameze.minegicka3.main.renders.RenderStaff;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy
{
    @Override
    public void load()
    {
        super.load();
	FMLCommonHandler.instance().bus().register(new TickHandlerClient());
	MinecraftForge.EVENT_BUS.register(new TickHandlerClient());
    }
    
    @Override
    public World getClientWorld()
    {
	return Minecraft.getMinecraft().theWorld;
    }
    
    @Override
    public void registerRenderHandler()
    {
	RenderingRegistry.registerEntityRenderingHandler(EntitySprayCold.class, new RenderEntitySpray());
	RenderingRegistry.registerEntityRenderingHandler(EntitySprayFire.class, new RenderEntitySpray());
	RenderingRegistry.registerEntityRenderingHandler(EntitySpraySteam.class, new RenderEntitySpray());
	RenderingRegistry.registerEntityRenderingHandler(EntitySprayWater.class, new RenderEntitySpray());
	RenderingRegistry.registerEntityRenderingHandler(EntityLightning.class, new RenderEntityLightning());
	RenderingRegistry.registerEntityRenderingHandler(EntityBeam.class, new RenderEntityBeam());
    }

    @Override
    public void registerItemRenderer(Item i)
    {
	if(i instanceof ItemStaff)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderStaff());
	}
    }
    
    @Override
    public void initCoreBridge(CoreBridge cb)
    {
        super.initCoreBridge(cb);
        cb.client = CoreClient.instance();
    }
    
    @Override
    public CoreClient getCoreClient()
    {
	return (CoreClient)CoreBridge.instance().client;
    }
}
