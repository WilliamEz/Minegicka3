package com.williameze.minegicka3;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.williameze.api.models.Sphere;
import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.core.CoreClient;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.entities.EntityBeam;
import com.williameze.minegicka3.main.entities.EntityBeamArea;
import com.williameze.minegicka3.main.entities.EntityBoulder;
import com.williameze.minegicka3.main.entities.EntityEarthRumble;
import com.williameze.minegicka3.main.entities.EntityHomingLightning;
import com.williameze.minegicka3.main.entities.EntityIceShard;
import com.williameze.minegicka3.main.entities.EntityIcicle;
import com.williameze.minegicka3.main.entities.EntityLightning;
import com.williameze.minegicka3.main.entities.EntityMine;
import com.williameze.minegicka3.main.entities.EntitySprayCold;
import com.williameze.minegicka3.main.entities.EntitySprayFire;
import com.williameze.minegicka3.main.entities.EntitySpraySteam;
import com.williameze.minegicka3.main.entities.EntitySprayWater;
import com.williameze.minegicka3.main.entities.EntityStorm;
import com.williameze.minegicka3.main.entities.EntityVortex;
import com.williameze.minegicka3.main.entities.FXEProjectileCharge;
import com.williameze.minegicka3.main.entities.FXESimpleParticle;
import com.williameze.minegicka3.main.guis.GuiCraftStation;
import com.williameze.minegicka3.main.models.ModelEntityBoulder;
import com.williameze.minegicka3.main.models.ModelHat;
import com.williameze.minegicka3.main.models.ModelStaff;
import com.williameze.minegicka3.main.objects.ItemEssence;
import com.williameze.minegicka3.main.objects.ItemMagickTablet;
import com.williameze.minegicka3.main.objects.ItemStaff;
import com.williameze.minegicka3.main.objects.TileEntityCraftStation;
import com.williameze.minegicka3.main.objects.TileEntityShield;
import com.williameze.minegicka3.main.objects.TileEntityWall;
import com.williameze.minegicka3.main.renders.BlockCustomRenderer;
import com.williameze.minegicka3.main.renders.RenderEntityBeam;
import com.williameze.minegicka3.main.renders.RenderEntityBeamArea;
import com.williameze.minegicka3.main.renders.RenderEntityBoulder;
import com.williameze.minegicka3.main.renders.RenderEntityEarthRumble;
import com.williameze.minegicka3.main.renders.RenderEntityHomingLightning;
import com.williameze.minegicka3.main.renders.RenderEntityIceShard;
import com.williameze.minegicka3.main.renders.RenderEntityIcicle;
import com.williameze.minegicka3.main.renders.RenderEntityLightning;
import com.williameze.minegicka3.main.renders.RenderEntityMine;
import com.williameze.minegicka3.main.renders.RenderEntityNothingAtAll;
import com.williameze.minegicka3.main.renders.RenderEntitySpray;
import com.williameze.minegicka3.main.renders.RenderEntityVortex;
import com.williameze.minegicka3.main.renders.RenderFXEProjectileCharge;
import com.williameze.minegicka3.main.renders.RenderFXESimpleParticle;
import com.williameze.minegicka3.main.renders.RenderItemGeneral;
import com.williameze.minegicka3.main.renders.RenderItemMagickTablet;
import com.williameze.minegicka3.main.renders.RenderItemStaff;
import com.williameze.minegicka3.main.renders.RenderItemStick;
import com.williameze.minegicka3.main.renders.RenderTileEntityCraftStation;
import com.williameze.minegicka3.main.renders.RenderTileEntityShield;
import com.williameze.minegicka3.main.renders.RenderTileEntityWall;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

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
    public void sidedOnlyLoad()
    {
    }

    @Override
    public void postLoad()
    {
	super.postLoad();
	ModelStaff.load();
	ModelHat.load();
	ModelEntityBoulder.load();
	RenderEntityBeamArea.load();
	RenderEntityIceShard.load();
	RenderTileEntityWall.load();
	ModKeybinding.load();
    }

    public void registerCommand(ICommand cm)
    {
	ClientCommandHandler.instance.registerCommand(cm);
    }

    @Override
    public EntityPlayer getClientPlayer()
    {
	return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public World getWorldForDimension(int dim)
    {
	if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
	{
	    return super.getWorldForDimension(dim);
	}
	else
	{
	    return dim == getClientWorld().provider.dimensionId ? getClientWorld() : null;
	}
    }

    @Override
    public World getClientWorld()
    {
	return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public void registerRenderHandler()
    {
	RenderingRegistry.registerEntityRenderingHandler(FXEProjectileCharge.class, new RenderFXEProjectileCharge());
	RenderingRegistry.registerEntityRenderingHandler(FXESimpleParticle.class, new RenderFXESimpleParticle());

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
	RenderingRegistry.registerEntityRenderingHandler(EntityVortex.class, new RenderEntityVortex());
	RenderingRegistry.registerEntityRenderingHandler(EntityHomingLightning.class, new RenderEntityHomingLightning());

	registerTileRenderer(TileEntityShield.class, new RenderTileEntityShield());
	registerTileRenderer(TileEntityWall.class, new RenderTileEntityWall());
	registerTileRenderer(TileEntityCraftStation.class, new RenderTileEntityCraftStation());

	RenderingRegistry.registerBlockHandler(new BlockCustomRenderer());

	registerItemRenderer(ModBase.thingy);
	registerItemRenderer(ModBase.thingyGood);
	registerItemRenderer(ModBase.thingySuper);
	registerItemRenderer(ModBase.stick);
	registerItemRenderer(ModBase.stickGood);
	registerItemRenderer(ModBase.stickSuper);
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
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemStaff());
	}
	if (i instanceof ItemMagickTablet)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemMagickTablet());
	}
	if (i == ModBase.stick || i == ModBase.stickGood || i == ModBase.stickSuper)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemStick());
	}
	if (i == ModBase.thingy)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemGeneral(new Sphere(0, 0, 0, 0.4, 2, 4).setColor(Values.yellow)));
	}
	if (i == ModBase.thingyGood)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemGeneral(new Sphere(0, 0, 0, 0.4, 2, 4).setColor(Values.cyan)));
	}
	if (i == ModBase.thingySuper)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemGeneral(new Sphere(0, 0, 0, 0.4, 2, 4).setColor(Values.purple)));
	}
	if (i instanceof ItemEssence)
	{
	    MinecraftForgeClient.registerItemRenderer(i,
		    new RenderItemGeneral(new Sphere(0, 0, 0, 0.4, 2, 4).setColor(((ItemEssence) i).unlocking.getColor())));
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

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
	if (ID == 0)
	{
	    return new GuiCraftStation(player);
	}

	return null;
    }
}
