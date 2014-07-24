package com.williameze.minegicka3;

import net.minecraft.command.ICommand;
import net.minecraft.command.ServerCommand;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.williameze.minegicka3.functional.CoreBridge;
import com.williameze.minegicka3.functional.CoreClient;
import com.williameze.minegicka3.functional.CoreServer;
import com.williameze.minegicka3.main.worldgen.WorldGen;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public class CommonProxy implements IGuiHandler
{
    public void load()
    {
	FMLCommonHandler.instance().bus().register(new TickHandler());
	FMLCommonHandler.instance().bus().register(new EventsHandler());
	MinecraftForge.EVENT_BUS.register(new TickHandler());
	MinecraftForge.EVENT_BUS.register(new EventsHandler());
    }

    public void sidedOnlyLoad()
    {
    }

    public void postLoad()
    {

    }

    public void registerCommand(ICommand cm)
    {
	((ServerCommandManager) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).registerCommand(cm);
    }

    public EntityPlayer getClientPlayer()
    {
	return null;
    }

    public World getWorldForDimension(int dim)
    {
	return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dim);
    }

    public World getClientWorld()
    {
	return null;
    }

    public void registerRenderHandler()
    {
    }

    public void registerItemRenderer(Item i)
    {

    }

    public void initCoreBridge(CoreBridge cb)
    {
	cb.server = CoreServer.instance();
    }

    public CoreClient getCoreClient()
    {
	return null;
    }

    public CoreServer getCoreServer()
    {
	return (CoreServer) CoreBridge.instance().server;
    }

    public void displayGuiScreen(Object o)
    {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
	return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
	return null;
    }
}
