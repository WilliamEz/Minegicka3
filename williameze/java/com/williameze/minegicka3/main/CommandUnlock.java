package com.williameze.minegicka3.main;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.core.PlayerData;
import com.williameze.minegicka3.core.PlayersData;
import com.williameze.minegicka3.main.packets.PacketPlayerUnlockAll;

public class CommandUnlock extends CommandBase
{

    @Override
    public String getCommandName()
    {
	return "m3unlock";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
	return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
	return "commands.m3unlock.usage";
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2)
    {
	if (var1 instanceof EntityPlayer)
	{
	    PlayerData pd = PlayersData.getPlayerData_static((EntityPlayer) var1);
	    pd.unlockEverything();
	    ModBase.packetPipeline.sendToServer(new PacketPlayerUnlockAll((EntityPlayer) var1));
	}
    }
}
