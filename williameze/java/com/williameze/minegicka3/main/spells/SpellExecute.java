package com.williameze.minegicka3.main.spells;

import scala.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import com.williameze.minegicka3.core.PlayerData;
import com.williameze.minegicka3.core.PlayersData;
import com.williameze.minegicka3.main.spells.Spell.SpellType;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class SpellExecute
{
    public static Random rnd = new Random();
    public static SpellExecute dummyExecute = new SpellExecute();
    public static SpellExecute sprayExecute = new SpellExecuteSpray();
    public static SpellExecute lightningExecute = new SpellExecuteLightning();
    public static SpellExecute beamExecute = new SpellExecuteBeam();
    public static SpellExecute projectileExecute = new SpellExecuteProjectile();
    public static SpellExecute groundedExecute = new SpellExecuteGrounded();

    public static SpellExecute getSpellExecute(Spell s)
    {
	if (s.spellType == SpellType.Spray) return sprayExecute;
	if (s.spellType == SpellType.Lightning) return lightningExecute;
	if (s.spellType == SpellType.Beam) return beamExecute;
	if (s.spellType == SpellType.Projectile) return projectileExecute;
	if (s.spellType == SpellType.Grounded) return groundedExecute;
	return dummyExecute;
    }

    public void startSpell(Spell s)
    {

    }

    public void updateSpell(Spell s)
    {

    }

    public void stopSpell(Spell s)
    {

    }

    public double consumeMana(Spell s, double m, boolean reallyConsume, boolean mustHaveMoreMana, int showChatMessage)
    {
	Entity e = s.getCaster();
	if (e instanceof EntityPlayer)
	{
	    EntityPlayer p = (EntityPlayer) e;
	    if(p.capabilities.isCreativeMode) return 1;
	    PlayersData psd = PlayersData.getWorldPlayersData(p.worldObj);
	    PlayerData pd = psd.getPlayerData(p);

	    double canConsume = Math.min(m, pd.mana);
	    if (mustHaveMoreMana && pd.mana < m)
	    {
		canConsume = 0;
	    }
	    s.toBeStopped = pd.mana < m ? true : s.toBeStopped;
	    if (canConsume > 0)
	    {
		if (reallyConsume)
		{
		    pd.mana -= canConsume;
		    if (!p.worldObj.isRemote) psd.sendPlayerManaToClient(p, p);
		}
	    }
	    if (s.toBeStopped)
	    {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
		    if (showChatMessage == 1)
		    {
			p.addChatMessage(new ChatComponentText("Mana too low.").setChatStyle(new ChatStyle().setItalic(true)
				.setColor(EnumChatFormatting.RED)));
		    }
		    else if (showChatMessage == 2)
		    {
			p.addChatMessage(new ChatComponentText("Mana too low. Requires " + (int) (Math.round(m * 10) / 10)
				+ " mana.").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.RED)));
		    }
		}
	    }

	    return canConsume / m;
	}
	return 1;
    }
}