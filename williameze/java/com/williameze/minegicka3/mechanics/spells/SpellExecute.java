package com.williameze.minegicka3.mechanics.spells;

import java.util.Random;

import com.williameze.minegicka3.mechanics.spells.Spell.SpellType;

public class SpellExecute
{
    public static Random rnd = new Random();
    public static SpellExecute dummyExecute = new SpellExecute();
    public static SpellExecute sprayExecute = new SpellExecuteSpray();
    public static SpellExecute lightningExecute = new SpellExecuteLightning();
    public static SpellExecute beamExecute = new SpellExecuteBeam();
    public static SpellExecute projectileExecute = new SpellExecuteProjectile();
    public static SpellExecute groundedExecute = new SpellExecuteGrounded();

    public SpellExecute()
    {
    }

    public static void load()
    {
	dummyExecute = new SpellExecute();
	sprayExecute = new SpellExecuteSpray();
	lightningExecute = new SpellExecuteLightning();
	beamExecute = new SpellExecuteBeam();
	projectileExecute = new SpellExecuteProjectile();
	groundedExecute = new SpellExecuteGrounded();
    }

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
	return s.consumeMana(m, reallyConsume, mustHaveMoreMana, showChatMessage);
    }
}