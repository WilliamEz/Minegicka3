package com.williameze.minegicka3;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.main.ClickCraft;
import com.williameze.minegicka3.main.Element;
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
import com.williameze.minegicka3.main.magicks.Magicks;
import com.williameze.minegicka3.main.objects.BlockCraftStation;
import com.williameze.minegicka3.main.objects.BlockShield;
import com.williameze.minegicka3.main.objects.BlockWall;
import com.williameze.minegicka3.main.objects.ItemEssence;
import com.williameze.minegicka3.main.objects.ItemMagickTablet;
import com.williameze.minegicka3.main.objects.ItemStaff;
import com.williameze.minegicka3.main.objects.TileEntityCraftStation;
import com.williameze.minegicka3.main.objects.TileEntityShield;
import com.williameze.minegicka3.main.objects.TileEntityWall;
import com.williameze.minegicka3.main.packets.PacketPipeline;
import com.williameze.minegicka3.main.packets.PacketPlayerClickCraft;
import com.williameze.minegicka3.main.packets.PacketPlayerData;
import com.williameze.minegicka3.main.packets.PacketPlayerMana;
import com.williameze.minegicka3.main.packets.PacketStartMagick;
import com.williameze.minegicka3.main.packets.PacketStartSpell;
import com.williameze.minegicka3.main.packets.PacketStopSpell;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(name = ModBase.MODNAME, modid = ModBase.MODID, version = ModBase.VERSION, useMetadata = true)
public class ModBase
{
    public static final String PACKAGE = "com.williameze.minegicka3";
    public static final String MODNAME = "Minegicka III";
    public static final String MODID = "minegicka3";
    public static final String VERSION = "0.0";

    @Instance("minegicka3")
    public static ModBase instance;

    @SidedProxy(clientSide = "com.williameze.minegicka3.ClientProxy", serverSide = "com.williameze.minegicka3.CommonProxy")
    public static CommonProxy proxy;
    public static PacketPipeline packetPipeline = new PacketPipeline();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
	ModMetadata mmd = event.getModMetadata();
	{
	    mmd.autogenerated = false;
	    mmd.credits = "You. The one who's playing. Credit to you. I love you!";
	    mmd.authorList = Arrays.asList("Will.Eze");
	    mmd.description = "Minegicka the IIIrd";
	}
	NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
	proxy.load();
	Magicks.load();
	CoreBridge.instance();
	initObjects();
	registerObjects();
	registerRecipes();
	registerEntities();
	ClickCraft.load();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
	proxy.registerRenderHandler();
	packetPipeline.initialise();
	registerPackets();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
	packetPipeline.postInitialise();
	proxy.postLoad();
    }

    public static Material magical;
    public static CreativeTabCustom modCreativeTab;
    public static Block shieldBlock, wallBlock;
    public static Block craftStation;
    public static Item thingy, stick;
    public static Item essenceArcane, essenceCold, essenceEarth, essenceFire, essenceIce, essenceLife, essenceLightning, essenceShield,
	    essenceSteam, essenceWater;
    public static Item magickTablet;
    public static Item staff, hemmyStaff;

    public void initObjects()
    {
	String themodid = MODID + ":";
	magical = new Material(MapColor.adobeColor);
	modCreativeTab = new CreativeTabCustom("Minegicka 3");

	shieldBlock = new BlockShield().setBlockName(themodid + "ShieldBlock").setBlockTextureName("glass");
	wallBlock = new BlockWall().setBlockName(themodid + "WallBlock").setBlockTextureName("glass");
	craftStation = new BlockCraftStation().setBlockName(themodid + "CraftStation").setBlockTextureName("obsidian")
		.setCreativeTab(modCreativeTab);

	thingy = new Item().setUnlocalizedName(themodid + "Thingy").setCreativeTab(modCreativeTab);
	stick = new Item().setUnlocalizedName(themodid + "TheStick").setCreativeTab(modCreativeTab);
	essenceArcane = new ItemEssence(Element.Arcane).setUnlocalizedName(themodid + "ArcaneEssence").setCreativeTab(modCreativeTab);
	essenceCold = new ItemEssence(Element.Cold).setUnlocalizedName(themodid + "ColdEssence").setCreativeTab(modCreativeTab);
	essenceEarth = new ItemEssence(Element.Earth).setUnlocalizedName(themodid + "EarthEssence").setCreativeTab(modCreativeTab);
	essenceFire = new ItemEssence(Element.Fire).setUnlocalizedName(themodid + "FireEssence").setCreativeTab(modCreativeTab);
	essenceIce = new ItemEssence(Element.Ice).setUnlocalizedName(themodid + "IceEssence").setCreativeTab(modCreativeTab);
	essenceLife = new ItemEssence(Element.Life).setUnlocalizedName(themodid + "LifeEssence").setCreativeTab(modCreativeTab);
	essenceLightning = new ItemEssence(Element.Lightning).setUnlocalizedName(themodid + "LightningEssence").setCreativeTab(
		modCreativeTab);
	essenceShield = new ItemEssence(Element.Shield).setUnlocalizedName(themodid + "ShieldEssence").setCreativeTab(modCreativeTab);
	essenceSteam = new ItemEssence(Element.Steam).setUnlocalizedName(themodid + "SteamEssence").setCreativeTab(modCreativeTab);
	essenceWater = new ItemEssence(Element.Water).setUnlocalizedName(themodid + "WaterEssence").setCreativeTab(modCreativeTab);

	staff = new ItemStaff().setUnlocalizedName(themodid + "Staff").setCreativeTab(modCreativeTab);
	hemmyStaff = new ItemStaff().setBaseStats(1, 3, 1, 3).setUnlocalizedName(themodid + "HemmyStaff").setCreativeTab(modCreativeTab);

	magickTablet = new ItemMagickTablet().setUnlocalizedName(themodid + "MagickTablet").setCreativeTab(modCreativeTab);
    }

    public void registerObjects()
    {
	String themodid = MODID + ":";
	modCreativeTab.setTabIconItem(staff);

	GameRegistry.registerBlock(shieldBlock, themodid + "ShieldBlock");
	GameRegistry.registerTileEntity(TileEntityShield.class, themodid + "ShieldBlockTile");
	GameRegistry.registerBlock(wallBlock, themodid + "WallBlock");
	GameRegistry.registerTileEntity(TileEntityWall.class, themodid + "WallBlockTile");
	GameRegistry.registerBlock(craftStation, themodid + "CraftStation");
	GameRegistry.registerTileEntity(TileEntityCraftStation.class, themodid + "CraftStationTile");

	GameRegistry.registerItem(thingy, themodid + "Thingy");
	GameRegistry.registerItem(stick, themodid + "TheEssence");
	GameRegistry.registerItem(essenceArcane, themodid + "ArcaneEssence");
	GameRegistry.registerItem(essenceCold, themodid + "ColdEssence");
	GameRegistry.registerItem(essenceEarth, themodid + "EarthEssence");
	GameRegistry.registerItem(essenceFire, themodid + "FireEssence");
	GameRegistry.registerItem(essenceIce, themodid + "IceEssence");
	GameRegistry.registerItem(essenceLife, themodid + "LifeEssence");
	GameRegistry.registerItem(essenceLightning, themodid + "LightningEssence");
	GameRegistry.registerItem(essenceShield, themodid + "ShieldEssence");
	GameRegistry.registerItem(essenceSteam, themodid + "SteamEssence");
	GameRegistry.registerItem(essenceWater, themodid + "WaterEssence");
	GameRegistry.registerItem(magickTablet, themodid + "MagickTablet");

	GameRegistry.registerItem(staff, themodid + "Staff");
	GameRegistry.registerItem(hemmyStaff, themodid + "HemmyStaff");

    }

    public void registerRecipes()
    {
	GameRegistry.addRecipe(new ItemStack(craftStation), new Object[] { "DTD", "GEG", "OOO", ('O'), Blocks.obsidian, ('D'),
		Items.diamond, ('G'), Items.gold_ingot, ('T'), thingy, ('E'), Items.emerald });
    }

    public void registerEntities()
    {
	registerEntity(FXEProjectileCharge.class, "FXEProjectileCharge", 64, Integer.MAX_VALUE);
	registerEntity(FXESimpleParticle.class, "FXESimpleParticle", 64, Integer.MAX_VALUE);

	registerEntity(EntitySprayCold.class, "SprayCold", 64, Integer.MAX_VALUE);
	registerEntity(EntitySprayFire.class, "SprayFire", 64, Integer.MAX_VALUE);
	registerEntity(EntitySpraySteam.class, "SpraySteam", 64, Integer.MAX_VALUE);
	registerEntity(EntitySprayWater.class, "SprayWater", 64, Integer.MAX_VALUE);
	registerEntity(EntityLightning.class, "Lightning", 64, Integer.MAX_VALUE);
	registerEntity(EntityBeam.class, "Beam", 64, Integer.MAX_VALUE);
	registerEntity(EntityBeamArea.class, "BeamArea", 64, Integer.MAX_VALUE);
	registerEntity(EntityBoulder.class, "Boulder", 64, Integer.MAX_VALUE);
	registerEntity(EntityIcicle.class, "Icicle", 64, Integer.MAX_VALUE);
	registerEntity(EntityEarthRumble.class, "EarthRumble", 64, Integer.MAX_VALUE);
	registerEntity(EntityIceShard.class, "IceShard", 64, Integer.MAX_VALUE);
	registerEntity(EntityStorm.class, "Storm", 64, Integer.MAX_VALUE);
	registerEntity(EntityMine.class, "Mine", 64, Integer.MAX_VALUE);
    }

    public void registerEntity(Class eClass, String eName, int updateRange, int updateFrequency)
    {
	int entityID = EntityRegistry.findGlobalUniqueEntityId();
	EntityRegistry.registerGlobalEntityID(eClass, MODID + "_" + eName, entityID);
	EntityRegistry.registerModEntity(eClass, MODID + "_" + eName, entityID, instance, updateRange, updateFrequency, true);

	/*
	 * long seed = eName.hashCode(); Random rand = new Random(seed); int
	 * primaryColor = rand.nextInt() * 16777215; int secondaryColor =
	 * rand.nextInt() * 16777215;
	 * EntityList.entityEggs.put(Integer.valueOf(entityID), new
	 * EntityList.EntityEggInfo(entityID, primaryColor, secondaryColor));
	 */
    }

    public void registerPackets()
    {
	packetPipeline.registerPacket(PacketStartSpell.class);
	packetPipeline.registerPacket(PacketStopSpell.class);
	packetPipeline.registerPacket(PacketPlayerData.class);
	packetPipeline.registerPacket(PacketPlayerMana.class);
	packetPipeline.registerPacket(PacketStartMagick.class);
	packetPipeline.registerPacket(PacketPlayerClickCraft.class);
    }
}
