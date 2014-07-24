package com.williameze.minegicka3;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import com.williameze.minegicka3.functional.CoreBridge;
import com.williameze.minegicka3.main.entities.fx.FXEProjectileCharge;
import com.williameze.minegicka3.main.entities.fx.FXESimpleParticle;
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
import com.williameze.minegicka3.main.entities.monsters.Entity888;
import com.williameze.minegicka3.main.objects.blocks.BlockCraftStation;
import com.williameze.minegicka3.main.objects.blocks.BlockEnchantStaff;
import com.williameze.minegicka3.main.objects.blocks.BlockShield;
import com.williameze.minegicka3.main.objects.blocks.BlockWall;
import com.williameze.minegicka3.main.objects.blocks.TileEntityCraftStation;
import com.williameze.minegicka3.main.objects.blocks.TileEntityEnchantStaff;
import com.williameze.minegicka3.main.objects.blocks.TileEntityShield;
import com.williameze.minegicka3.main.objects.blocks.TileEntityWall;
import com.williameze.minegicka3.main.objects.items.Hat;
import com.williameze.minegicka3.main.objects.items.HatOfImmunity;
import com.williameze.minegicka3.main.objects.items.HatOfResistance;
import com.williameze.minegicka3.main.objects.items.HatOfRisk;
import com.williameze.minegicka3.main.objects.items.ItemEssence;
import com.williameze.minegicka3.main.objects.items.ItemMagicApple;
import com.williameze.minegicka3.main.objects.items.ItemMagicCookie;
import com.williameze.minegicka3.main.objects.items.ItemMagickPedia;
import com.williameze.minegicka3.main.objects.items.ItemTest;
import com.williameze.minegicka3.main.objects.items.Staff;
import com.williameze.minegicka3.main.objects.items.StaffBlessing;
import com.williameze.minegicka3.main.objects.items.StaffDestruction;
import com.williameze.minegicka3.main.objects.items.StaffHemmy;
import com.williameze.minegicka3.main.objects.items.StaffManipulation;
import com.williameze.minegicka3.main.objects.items.StaffTelekinesis;
import com.williameze.minegicka3.main.packets.PacketHandler;
import com.williameze.minegicka3.main.worldgen.ChestGenHook;
import com.williameze.minegicka3.main.worldgen.Gen;
import com.williameze.minegicka3.main.worldgen.WorldGen;
import com.williameze.minegicka3.mechanics.ClickCraft;
import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.Enchant;
import com.williameze.minegicka3.mechanics.magicks.Magicks;

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
    public static final String VERSION = "0.9.0.0";

    @Instance("minegicka3")
    public static ModBase instance;

    @SidedProxy(clientSide = "com.williameze.minegicka3.ClientProxy", serverSide = "com.williameze.minegicka3.CommonProxy")
    public static CommonProxy proxy;
    public static PacketHandler packetPipeline;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
	ModMetadata mmd = event.getModMetadata();
	{
	    mmd.autogenerated = false;
	    mmd.credits = "You. The one who's playing. I love you!";
	    mmd.authorList = Arrays.asList("Will.Eze");
	    mmd.description = "Minegicka the IIIrd";
	}
	//modifyPotionArray();
	NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
	proxy.load();
	Magicks.load();
	CoreBridge.instance();
	initObjects();
	registerObjects();
	registerRecipes();
	registerEntities();
	ClickCraft.load();
	Enchant.load();
	ChestGenHook.load();
    }

    public void modifyPotionArray()
    {
	Potion[] potionTypes = null;

	for (Field f : Potion.class.getDeclaredFields())
	{
	    f.setAccessible(true);
	    try
	    {
		if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a"))
		{
		    Field modfield = Field.class.getDeclaredField("modifiers");
		    modfield.setAccessible(true);
		    modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);

		    potionTypes = (Potion[]) f.get(null);
		    final Potion[] newPotionTypes = new Potion[256];
		    System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
		    f.set(null, newPotionTypes);
		}
	    }
	    catch (Exception e)
	    {
		System.err.println("Severe error, please report this to the mod author:");
		System.err.println(e);
	    }
	}
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
	proxy.registerRenderHandler();
	packetPipeline = new PacketHandler();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
	proxy.postLoad();
	GameRegistry.registerWorldGenerator(new WorldGen(), 100);
	Gen.load();
    }

    public static Material magical;
    public static CreativeTabCustom modCreativeTab;
    public static Potion coldResistance, lifeBoost, arcaneResistance, lightningResistance;
    public static Block shieldBlock, wallBlock;
    public static Block craftStation, enchantStaff;
    public static Item thingy, thingyGood, thingySuper, stick, stickGood, stickSuper;
    public static Item magicApple, magicGoodApple, magicSuperApple, magicCookie, magicGoodCookie, magicSuperCookie;
    public static Item essenceArcane, essenceCold, essenceEarth, essenceFire, essenceIce, essenceLife, essenceLightning, essenceShield, essenceSteam,
	    essenceWater;
    public static Item matResistance;
    public static Item magickTablet, magickPedia;
    public static Item staff, staffGrand, staffSuper, hemmyStaff, staffBlessing, staffDestruction, staffTelekinesis, staffManipulation;
    public static Item hat, hatImmunity, hatRisk, hatResistance;

    public static Item test;

    public void initObjects()
    {
	String themodid = MODID + "-";
	magical = new Material(MapColor.adobeColor);
	modCreativeTab = new CreativeTabCustom("Minegicka 3");

	coldResistance = new PotionCustom(28, false, 0xeeeeff).setCustomIconIndex(6, 1).setPotionName("potion.coldresistance");
	lifeBoost = new PotionCustom(29, false, 0x00ff00).setCustomIconIndex(7, 0).setPotionName("potion.lifeboost");
	arcaneResistance = new PotionCustom(30, false, 0xee0000).setCustomIconIndex(6, 1).setPotionName("potion.arcaneresistance");
	lightningResistance = new PotionCustom(31, false, 0xff22ff).setCustomIconIndex(6, 1).setPotionName("potion.lightningresistance");

	shieldBlock = new BlockShield().setBlockName(themodid + "ShieldBlock").setBlockTextureName("glass");
	wallBlock = new BlockWall().setBlockName(themodid + "WallBlock").setBlockTextureName("glass");
	craftStation = new BlockCraftStation().setBlockName(themodid + "CraftStation").setBlockTextureName("obsidian").setCreativeTab(modCreativeTab);
	enchantStaff = new BlockEnchantStaff().setBlockName(themodid + "EnchantStaff").setBlockTextureName("obsidian").setCreativeTab(modCreativeTab);

	thingy = new Item().setUnlocalizedName(themodid + "Thingy").setTextureName("apple").setCreativeTab(modCreativeTab);
	thingyGood = new Item().setUnlocalizedName(themodid + "ThingyGood").setTextureName("apple").setCreativeTab(modCreativeTab);
	thingySuper = new Item().setUnlocalizedName(themodid + "ThingySuper").setTextureName("apple").setCreativeTab(modCreativeTab);
	stick = new Item().setUnlocalizedName(themodid + "TheStick").setTextureName("apple").setCreativeTab(modCreativeTab);
	stickGood = new Item().setUnlocalizedName(themodid + "TheStickGood").setTextureName("apple").setCreativeTab(modCreativeTab);
	stickSuper = new Item().setUnlocalizedName(themodid + "TheStickSuper").setTextureName("apple").setCreativeTab(modCreativeTab);
	magicApple = new ItemMagicApple(5).setUnlocalizedName(themodid + "MagicApple").setTextureName("apple").setCreativeTab(modCreativeTab);
	magicGoodApple = new ItemMagicApple(100).setUnlocalizedName(themodid + "MagicGoodApple").setTextureName("apple_golden")
		.setCreativeTab(modCreativeTab);
	magicSuperApple = new ItemMagicApple(2000).setUnlocalizedName(themodid + "MagicSuperApple").setTextureName("apple_golden")
		.setCreativeTab(modCreativeTab);
	magicCookie = new ItemMagicCookie(20).setUnlocalizedName(themodid + "MagicCookie").setTextureName("cookie").setCreativeTab(modCreativeTab);
	magicGoodCookie = new ItemMagicCookie(120).setUnlocalizedName(themodid + "MagicGoodCookie").setTextureName("cookie")
		.setCreativeTab(modCreativeTab);
	magicSuperCookie = new ItemMagicCookie(960).setUnlocalizedName(themodid + "MagicSuperCookie").setTextureName("cookie")
		.setCreativeTab(modCreativeTab);
	essenceArcane = new ItemEssence(Element.Arcane).setUnlocalizedName(themodid + "ArcaneEssence").setCreativeTab(modCreativeTab);
	essenceCold = new ItemEssence(Element.Cold).setUnlocalizedName(themodid + "ColdEssence").setCreativeTab(modCreativeTab);
	essenceEarth = new ItemEssence(Element.Earth).setUnlocalizedName(themodid + "EarthEssence").setCreativeTab(modCreativeTab);
	essenceFire = new ItemEssence(Element.Fire).setUnlocalizedName(themodid + "FireEssence").setCreativeTab(modCreativeTab);
	essenceIce = new ItemEssence(Element.Ice).setUnlocalizedName(themodid + "IceEssence").setCreativeTab(modCreativeTab);
	essenceLife = new ItemEssence(Element.Life).setUnlocalizedName(themodid + "LifeEssence").setCreativeTab(modCreativeTab);
	essenceLightning = new ItemEssence(Element.Lightning).setUnlocalizedName(themodid + "LightningEssence").setCreativeTab(modCreativeTab);
	essenceShield = new ItemEssence(Element.Shield).setUnlocalizedName(themodid + "ShieldEssence").setCreativeTab(modCreativeTab);
	essenceSteam = new ItemEssence(Element.Steam).setUnlocalizedName(themodid + "SteamEssence").setCreativeTab(modCreativeTab);
	essenceWater = new ItemEssence(Element.Water).setUnlocalizedName(themodid + "WaterEssence").setCreativeTab(modCreativeTab);
	magickPedia = new ItemMagickPedia().setUnlocalizedName(themodid + "MagickPedia").setCreativeTab(modCreativeTab).setTextureName("book_normal");
	matResistance = new Item().setUnlocalizedName(themodid + "MatResistance").setTextureName("apple").setCreativeTab(modCreativeTab);

	staff = new Staff().setUnlocalizedName(themodid + "Staff");
	staffGrand = new Staff().setBaseStats(2, 2, 0.5, 1.5).setUnlocalizedName(themodid + "StaffGrand");
	staffSuper = new Staff().setBaseStats(4, 4, 0.25, 2).setUnlocalizedName(themodid + "StaffSuper");
	hemmyStaff = new StaffHemmy().setUnlocalizedName(themodid + "HemmyStaff");
	staffBlessing = new StaffBlessing().setUnlocalizedName(themodid + "StaffBlessing");
	staffDestruction = new StaffDestruction().setUnlocalizedName(themodid + "StaffDestruction");
	staffTelekinesis = new StaffTelekinesis().setUnlocalizedName(themodid + "StaffTelekinesis");
	staffManipulation = new StaffManipulation().setUnlocalizedName(themodid + "StaffManipulation");

	hat = new Hat().setUnlocalizedName(themodid + "Hat");
	hatImmunity = new HatOfImmunity().setUnlocalizedName(themodid + "HatOfImmunity");
	hatRisk = new HatOfRisk().setUnlocalizedName(themodid + "HatOfRisk");
	hatResistance = new HatOfResistance().setUnlocalizedName(themodid + "HatOfResistance");

	test = new ItemTest().setTextureName("apple").setCreativeTab(modCreativeTab).setUnlocalizedName(themodid + "Test");
    }

    public void registerObjects()
    {
	String themodid = MODID + "-";
	modCreativeTab.setTabIconItem(thingySuper);

	GameRegistry.registerBlock(shieldBlock, themodid + "ShieldBlock");
	GameRegistry.registerTileEntity(TileEntityShield.class, themodid + "ShieldBlockTile");
	GameRegistry.registerBlock(wallBlock, themodid + "WallBlock");
	GameRegistry.registerTileEntity(TileEntityWall.class, themodid + "WallBlockTile");
	GameRegistry.registerBlock(craftStation, themodid + "CraftStation");
	GameRegistry.registerTileEntity(TileEntityCraftStation.class, themodid + "CraftStationTile");
	GameRegistry.registerBlock(enchantStaff, themodid + "EnchantStaff");
	GameRegistry.registerTileEntity(TileEntityEnchantStaff.class, themodid + "EnchantStaffTile");

	GameRegistry.registerItem(thingy, themodid + "Thingy");
	GameRegistry.registerItem(thingyGood, themodid + "ThingyGood");
	GameRegistry.registerItem(thingySuper, themodid + "ThingySuper");
	GameRegistry.registerItem(stick, themodid + "TheStick");
	GameRegistry.registerItem(stickGood, themodid + "TheStickGood");
	GameRegistry.registerItem(stickSuper, themodid + "TheStickSuper");
	GameRegistry.registerItem(magicApple, themodid + "MagicApple");
	GameRegistry.registerItem(magicGoodApple, themodid + "MagicGoodApple");
	GameRegistry.registerItem(magicSuperApple, themodid + "MagicSuperApple");
	GameRegistry.registerItem(magicCookie, themodid + "MagicCookie");
	GameRegistry.registerItem(magicGoodCookie, themodid + "MagicGoodCookie");
	GameRegistry.registerItem(magicSuperCookie, themodid + "MagicSuperCookie");
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
	GameRegistry.registerItem(magickPedia, themodid + "MagickPedia");
	GameRegistry.registerItem(matResistance, themodid + "MatResistance");

	GameRegistry.registerItem(staff, themodid + "Staff");
	GameRegistry.registerItem(staffGrand, themodid + "StaffGrand");
	GameRegistry.registerItem(staffSuper, themodid + "StaffSuper");
	GameRegistry.registerItem(hemmyStaff, themodid + "HemmyStaff");
	GameRegistry.registerItem(staffBlessing, themodid + "StaffBlessing");
	GameRegistry.registerItem(staffDestruction, themodid + "StaffDestruction");
	GameRegistry.registerItem(staffTelekinesis, themodid + "StaffTelekinesis");
	GameRegistry.registerItem(staffManipulation, themodid + "StaffManipulation");

	GameRegistry.registerItem(hat, themodid + "Hat");
	GameRegistry.registerItem(hatImmunity, themodid + "HatOfImmunity");
	GameRegistry.registerItem(hatRisk, themodid + "HatOfRisk");
	GameRegistry.registerItem(hatResistance, themodid + "HatOfResistance");

	GameRegistry.registerItem(test, themodid + "Test");
    }

    public void registerRecipes()
    {
	GameRegistry.addRecipe(new ItemStack(craftStation), new Object[] { "DTD", "GEG", "OOO", ('O'), Blocks.obsidian, ('D'), Items.diamond, ('G'),
		Items.gold_ingot, ('T'), thingy, ('E'), Items.emerald });
	GameRegistry.addRecipe(new ItemStack(enchantStaff), new Object[] { "DGD", "GEG", "OOO", ('O'), Blocks.obsidian, ('D'), Blocks.glass, ('G'),
		thingy, ('E'), new ItemStack(Items.dye, 1, 13) });
    }

    public void registerEntities()
    {
	int superSlowUpdateFreq = Integer.MAX_VALUE;
	registerEntity(FXEProjectileCharge.class, "FXEProjectileCharge", 64, superSlowUpdateFreq);
	registerEntity(FXESimpleParticle.class, "FXESimpleParticle", 64, superSlowUpdateFreq);

	registerEntity(EntitySprayCold.class, "SprayCold", 64, superSlowUpdateFreq);
	registerEntity(EntitySprayFire.class, "SprayFire", 64, superSlowUpdateFreq);
	registerEntity(EntitySpraySteam.class, "SpraySteam", 64, superSlowUpdateFreq);
	registerEntity(EntitySprayWater.class, "SprayWater", 64, superSlowUpdateFreq);
	registerEntity(EntityLightning.class, "Lightning", 64, superSlowUpdateFreq);
	registerEntity(EntityBeam.class, "Beam", 64, superSlowUpdateFreq);
	registerEntity(EntityBeamArea.class, "BeamArea", 64, superSlowUpdateFreq);
	registerEntity(EntityBoulder.class, "Boulder", 64, 20);
	registerEntity(EntityIcicle.class, "Icicle", 64, 20);
	registerEntity(EntityEarthRumble.class, "EarthRumble", 64, superSlowUpdateFreq);
	registerEntity(EntityIceShard.class, "IceShard", 64, superSlowUpdateFreq);
	registerEntity(EntityStorm.class, "Storm", 64, superSlowUpdateFreq);
	registerEntity(EntityMine.class, "Mine", 64, superSlowUpdateFreq);
	registerEntity(EntityVortex.class, "Vortex", 64, superSlowUpdateFreq);
	registerEntity(EntityHomingLightning.class, "HomingLightning", 64, superSlowUpdateFreq);

	registerEntity(Entity888.class, "888", 64, 10, true, 0x990000, 0x880099);
    }

    public void registerEntity(Class eClass, String eName, int updateRange, int updateFrequency)
    {
	registerEntity(eClass, eName, updateRange, updateFrequency, false, 0, 0);
    }

    public void registerEntity(Class eClass, String eName, int updateRange, int updateFrequency, boolean withEgg, int primary, int secondary)
    {
	int entityID = EntityRegistry.findGlobalUniqueEntityId();
	EntityRegistry.registerGlobalEntityID(eClass, MODID + "_" + eName, entityID);
	EntityRegistry.registerModEntity(eClass, MODID + "_" + eName, entityID, instance, updateRange, updateFrequency, true);

	if (withEgg)
	{
	    EntityList.entityEggs.put(Integer.valueOf(entityID), new EntityList.EntityEggInfo(entityID, primary, secondary));
	}
    }

}
