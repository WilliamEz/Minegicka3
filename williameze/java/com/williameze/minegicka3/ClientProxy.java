package com.williameze.minegicka3;

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
import com.williameze.minegicka3.functional.CoreBridge;
import com.williameze.minegicka3.functional.CoreClient;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.entities.fx.FXEProjectileCharge;
import com.williameze.minegicka3.main.entities.fx.FXESimpleParticle;
import com.williameze.minegicka3.main.entities.fx.render.RenderFXEProjectileCharge;
import com.williameze.minegicka3.main.entities.fx.render.RenderFXESimpleParticle;
import com.williameze.minegicka3.main.entities.magic.EntityBeam;
import com.williameze.minegicka3.main.entities.magic.EntityBeamArea;
import com.williameze.minegicka3.main.entities.magic.EntityBoulder;
import com.williameze.minegicka3.main.entities.magic.EntityEarthRumble;
import com.williameze.minegicka3.main.entities.magic.EntityHomingLightning;
import com.williameze.minegicka3.main.entities.magic.EntityIceShard;
import com.williameze.minegicka3.main.entities.magic.EntityIcicle;
import com.williameze.minegicka3.main.entities.magic.EntityLightning;
import com.williameze.minegicka3.main.entities.magic.EntityMine;
import com.williameze.minegicka3.main.entities.magic.EntitySprayCold;
import com.williameze.minegicka3.main.entities.magic.EntitySprayFire;
import com.williameze.minegicka3.main.entities.magic.EntitySpraySteam;
import com.williameze.minegicka3.main.entities.magic.EntitySprayWater;
import com.williameze.minegicka3.main.entities.magic.EntityStorm;
import com.williameze.minegicka3.main.entities.magic.EntityVortex;
import com.williameze.minegicka3.main.entities.magic.model.ModelEntityBoulder;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntityBeam;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntityBeamArea;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntityBoulder;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntityEarthRumble;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntityHomingLightning;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntityIceShard;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntityIcicle;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntityLightning;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntityMine;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntityNothingAtAll;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntitySpray;
import com.williameze.minegicka3.main.entities.magic.render.RenderEntityVortex;
import com.williameze.minegicka3.main.entities.monsters.Entity888;
import com.williameze.minegicka3.main.entities.monsters.model.ModelEntity888;
import com.williameze.minegicka3.main.entities.monsters.render.RenderEntity888;
import com.williameze.minegicka3.main.guis.GuiCraftStation;
import com.williameze.minegicka3.main.guis.GuiEnchantStaff;
import com.williameze.minegicka3.main.guis.GuiMagickPedia;
import com.williameze.minegicka3.main.objects.blocks.TileEntityCraftStation;
import com.williameze.minegicka3.main.objects.blocks.TileEntityEnchantStaff;
import com.williameze.minegicka3.main.objects.blocks.TileEntityShield;
import com.williameze.minegicka3.main.objects.blocks.TileEntityWall;
import com.williameze.minegicka3.main.objects.blocks.renders.BlockCustomRenderer;
import com.williameze.minegicka3.main.objects.blocks.renders.RenderTileCraftStation;
import com.williameze.minegicka3.main.objects.blocks.renders.RenderTileEnchantStaff;
import com.williameze.minegicka3.main.objects.blocks.renders.RenderTileShield;
import com.williameze.minegicka3.main.objects.blocks.renders.RenderTileWall;
import com.williameze.minegicka3.main.objects.items.Hat;
import com.williameze.minegicka3.main.objects.items.ItemEssence;
import com.williameze.minegicka3.main.objects.items.ItemMagickTablet;
import com.williameze.minegicka3.main.objects.items.Staff;
import com.williameze.minegicka3.main.objects.items.models.hat.ModelHat;
import com.williameze.minegicka3.main.objects.items.models.staff.ModelStaff;
import com.williameze.minegicka3.main.objects.items.renders.RenderItemGeneral;
import com.williameze.minegicka3.main.objects.items.renders.RenderItemHat;
import com.williameze.minegicka3.main.objects.items.renders.RenderItemMagickTablet;
import com.williameze.minegicka3.main.objects.items.renders.RenderItemStaff;
import com.williameze.minegicka3.main.objects.items.renders.RenderItemStick;

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
	RenderTileWall.load();
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
	registerTileRenderer(TileEntityShield.class, new RenderTileShield());
	registerTileRenderer(TileEntityWall.class, new RenderTileWall());
	registerTileRenderer(TileEntityCraftStation.class, new RenderTileCraftStation());
	registerTileRenderer(TileEntityEnchantStaff.class, new RenderTileEnchantStaff());

	RenderingRegistry.registerBlockHandler(new BlockCustomRenderer());

	registerItemRenderer(ModBase.thingy);
	registerItemRenderer(ModBase.thingyGood);
	registerItemRenderer(ModBase.thingySuper);
	registerItemRenderer(ModBase.stick);
	registerItemRenderer(ModBase.stickGood);
	registerItemRenderer(ModBase.stickSuper);
	registerItemRenderer(ModBase.matResistance);

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

	RenderingRegistry.registerEntityRenderingHandler(Entity888.class, new RenderEntity888());
    }

    public void registerTileRenderer(Class<? extends TileEntity> c, TileEntitySpecialRenderer re)
    {
	TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(c, re);
    }

    @Override
    public void registerItemRenderer(Item i)
    {
	if (i instanceof Staff)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemStaff());
	}
	else if (i instanceof ItemMagickTablet)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemMagickTablet());
	}
	else if (i instanceof ItemEssence)
	{
	    MinecraftForgeClient.registerItemRenderer(i,
		    new RenderItemGeneral(new Sphere(0, 0, 0, 0.4, 2, 4).setColor(((ItemEssence) i).unlocking.getColor())));
	}
	else if (i instanceof Hat)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemHat());
	}
	else if (i == ModBase.stick || i == ModBase.stickGood || i == ModBase.stickSuper)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemStick());
	}
	else if (i == ModBase.thingy)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemGeneral(new Sphere(0, 0, 0, 0.4, 2, 4).setColor(Values.yellow)));
	}
	else if (i == ModBase.thingyGood)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemGeneral(new Sphere(0, 0, 0, 0.4, 2, 4).setColor(Values.cyan)));
	}
	else if (i == ModBase.thingySuper)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemGeneral(new Sphere(0, 0, 0, 0.4, 2, 4).setColor(Values.purple)));
	}
	else if (i == ModBase.matResistance)
	{
	    MinecraftForgeClient.registerItemRenderer(i, new RenderItemGeneral(new Sphere(0, 0, 0, 0.4, 2, 4).setColor(Values.purple)));
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
	if (ID == 1)
	{
	    return new GuiMagickPedia(player);
	}
	if (ID == 2)
	{
	    return new GuiEnchantStaff(player);
	}
	return null;
    }
}
