package com.williameze.minegicka3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.core.CoreClient;
import com.williameze.minegicka3.main.entities.EntityBeam;
import com.williameze.minegicka3.main.entities.EntityBeamArea;
import com.williameze.minegicka3.main.entities.EntityBoulder;
import com.williameze.minegicka3.main.entities.EntityEarthRumble;
import com.williameze.minegicka3.main.entities.EntityIceShard;
import com.williameze.minegicka3.main.entities.EntityIcicle;
import com.williameze.minegicka3.main.entities.EntityLightning;
import com.williameze.minegicka3.main.entities.EntityMine;
import com.williameze.minegicka3.main.entities.EntitySprayCold;
import com.williameze.minegicka3.main.entities.EntitySprayFire;
import com.williameze.minegicka3.main.entities.EntitySpraySteam;
import com.williameze.minegicka3.main.entities.EntitySprayWater;
import com.williameze.minegicka3.main.entities.EntityStorm;
import com.williameze.minegicka3.main.entities.FXEProjectileCharge;
import com.williameze.minegicka3.main.entities.FXESimpleParticle;
import com.williameze.minegicka3.main.models.ModelEntityBoulder;
import com.williameze.minegicka3.main.objects.ItemStaff;
import com.williameze.minegicka3.main.objects.TileEntityShield;
import com.williameze.minegicka3.main.objects.TileEntityWall;
import com.williameze.minegicka3.main.renders.FXERenderProjectileCharge;
import com.williameze.minegicka3.main.renders.FXERenderSimpleParticle;
import com.williameze.minegicka3.main.renders.RenderEntityBeam;
import com.williameze.minegicka3.main.renders.RenderEntityBeamArea;
import com.williameze.minegicka3.main.renders.RenderEntityBoulder;
import com.williameze.minegicka3.main.renders.RenderEntityEarthRumble;
import com.williameze.minegicka3.main.renders.RenderEntityIceShard;
import com.williameze.minegicka3.main.renders.RenderEntityIcicle;
import com.williameze.minegicka3.main.renders.RenderEntityLightning;
import com.williameze.minegicka3.main.renders.RenderEntityMine;
import com.williameze.minegicka3.main.renders.RenderEntityNothingAtAll;
import com.williameze.minegicka3.main.renders.RenderEntitySpray;
import com.williameze.minegicka3.main.renders.RenderStaff;
import com.williameze.minegicka3.main.renders.RenderTileEntityShield;
import com.williameze.minegicka3.main.renders.RenderTileEntityWall;

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
    public void postLoad()
    {
	super.postLoad();
	ModKeybinding.load();
	ModelEntityBoulder.load();
	RenderEntityBeamArea.load();
	RenderEntityIceShard.load();
    }

    @Override
    public EntityPlayer getClientPlayer()
    {
	return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public World getClientWorld()
    {
	return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public void registerRenderHandler()
    {
	RenderingRegistry.registerEntityRenderingHandler(FXEProjectileCharge.class, new FXERenderProjectileCharge());
	RenderingRegistry.registerEntityRenderingHandler(FXESimpleParticle.class, new FXERenderSimpleParticle());

	RenderingRegistry.registerEntityRenderingHandler(EntitySprayCold.class, new RenderEntitySpray());
	RenderingRegistry.registerEntityRenderingHandler(EntitySprayFire.class, new RenderEntitySpray());
	RenderingRegistry.registerEntityRenderingHandler(EntitySpraySteam.class, new RenderEntitySpray());
	RenderingRegistry.registerEntityRenderingHandler(EntitySprayWater.class, new RenderEntitySpray());
	RenderingRegistry.registerEntityRenderingHandler(EntityLightning.class, new RenderEntityLightning());
	RenderingRegistry.registerEntityRenderingHandler(EntityBeam.class, new RenderEntityBeam());
	RenderingRegistry.registerEntityRenderingHandler(EntityBeamArea.class, new RenderEntityBeamArea());
	RenderingRegistry.registerEntityRenderingHandler(EntityBoulder.class, new RenderEntityBoulder());
	RenderingRegistry.registerEntityRenderingHandler(EntityIcicle.class, new RenderEntityIcicle());
	RenderingRegistry.registerEntityRenderingHandler(EntityEarthRumble.class, new RenderEntityEarthRumble());
	RenderingRegistry.registerEntityRenderingHandler(EntityIceShard.class, new RenderEntityIceShard());
	RenderingRegistry.registerEntityRenderingHandler(EntityStorm.class, new RenderEntityNothingAtAll());
	RenderingRegistry.registerEntityRenderingHandler(EntityMine.class, new RenderEntityMine());

	registerTileRenderer(TileEntityShield.class, new RenderTileEntityShield());
	registerTileRenderer(TileEntityWall.class, new RenderTileEntityWall());
    }

    public void registerTileRenderer(Class<? extends TileEntity> c, TileEntitySpecialRenderer re)
    {
	TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(c, re);
    }

    @Override
    public void registerItemRenderer(Item i)
    {
	if (i instanceof ItemStaff)
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
	return (CoreClient) CoreBridge.instance().client;
    }
}
