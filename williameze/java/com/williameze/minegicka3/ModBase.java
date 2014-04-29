package com.williameze.minegicka3;

import java.util.Arrays;

import net.minecraft.item.Item;

import com.williameze.minegicka3.core.CoreBridge;
import com.williameze.minegicka3.main.entities.EntityBeam;
import com.williameze.minegicka3.main.entities.EntityBeamArea;
import com.williameze.minegicka3.main.entities.EntityBoulder;
import com.williameze.minegicka3.main.entities.EntityEarthRumble;
import com.williameze.minegicka3.main.entities.EntityIcicle;
import com.williameze.minegicka3.main.entities.EntityLightning;
import com.williameze.minegicka3.main.entities.EntitySprayCold;
import com.williameze.minegicka3.main.entities.EntitySprayFire;
import com.williameze.minegicka3.main.entities.EntitySpraySteam;
import com.williameze.minegicka3.main.entities.EntitySprayWater;
import com.williameze.minegicka3.main.objects.ItemStaff;
import com.williameze.minegicka3.main.packets.PacketPipeline;
import com.williameze.minegicka3.main.packets.PacketPlayerData;
import com.williameze.minegicka3.main.packets.PacketPlayerMana;
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

    public static Item staff, hemmyStaff;
    public static CreativeTabCustom modCreativeTab;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
	ModMetadata mmd = event.getModMetadata();
	{
	    mmd.autogenerated = false;
	    mmd.credits = "You. The one who's playing. Credit to you.";
	    mmd.authorList = Arrays.asList("WilliamEze");
	    mmd.description = "Minegicka the IIIrd";
	}
	proxy.load();
	CoreBridge.instance();
	initBlocksAndItems();
	initRecipes();
	registerEntities();
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

    public void initBlocksAndItems()
    {
	String themodid = MODID + ":";

	staff = new ItemStaff().setUnlocalizedName(themodid + "Staff");
	GameRegistry.registerItem(staff, themodid + "Staff");
	hemmyStaff = new ItemStaff().setBaseStats(1, 3, 1, 3).setUnlocalizedName(themodid + "HemmyStaff");
	GameRegistry.registerItem(hemmyStaff, themodid + "HemmyStaff");

	modCreativeTab = new CreativeTabCustom("Minegicka 3").setTabIconItem(staff);
	staff.setCreativeTab(modCreativeTab);
	hemmyStaff.setCreativeTab(modCreativeTab);
    }

    public void initRecipes()
    {
    }

    public void registerEntities()
    {
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
    }
}
