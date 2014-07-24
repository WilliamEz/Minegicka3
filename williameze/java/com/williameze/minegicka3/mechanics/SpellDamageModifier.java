package com.williameze.minegicka3.mechanics;

import java.text.DecimalFormat;

public class SpellDamageModifier
{
    public static SpellDamageModifier defau = new SpellDamageModifier();

    /**
     * lightning will be indicated by letter H, shield will be indicated by letter D. The rest are indicated by the first letter.
     **/
    public double arcaneMod, coldMod, earthMod, fireMod, iceMod, lifeMod, lightningMod, shieldMod, steamMod, waterMod;

    public SpellDamageModifier()
    {
	this(1);
	//arcaneMod = coldMod = earthMod = fireMod = iceMod = lifeMod = lightningMod = shieldMod = steamMod = waterMod = 1;
    }

    public SpellDamageModifier(double d)
    {
	arcaneMod = coldMod = earthMod = fireMod = iceMod = lifeMod = lightningMod = shieldMod = steamMod = waterMod = d;
    }

    /**
     * lightning will be indicated by letter H, shield will be indicated by letter D. The rest are indicated by the first letter.
     **/
    public SpellDamageModifier(String s)
    {
	this();
	setModifiers(s);
    }

    public SpellDamageModifier setModifier(Element e, double d)
    {
	switch (e)
	{
	    case Arcane:
		arcaneMod = d;
		break;
	    case Cold:
		coldMod = d;
		break;
	    case Earth:
		earthMod = d;
		break;
	    case Fire:
		fireMod = d;
		break;
	    case Ice:
		iceMod = d;
		break;
	    case Life:
		lifeMod = d;
		break;
	    case Lightning:
		lightningMod = d;
		break;
	    case Shield:
		shieldMod = d;
		break;
	    case Steam:
		steamMod = d;
		break;
	    case Water:
		waterMod = d;
		break;
	    default:
		break;
	}
	return this;
    }

    public SpellDamageModifier multiply(String s)
    {
	return multiply(new SpellDamageModifier(s));
    }

    public SpellDamageModifier multiply(SpellDamageModifier mod)
    {
	SpellDamageModifier md = new SpellDamageModifier(toString());
	md.arcaneMod *= mod.arcaneMod;
	md.coldMod *= mod.coldMod;
	md.earthMod *= mod.earthMod;
	md.fireMod *= mod.fireMod;
	md.iceMod *= mod.iceMod;
	md.lifeMod *= mod.lifeMod;
	md.lightningMod *= mod.lightningMod;
	md.shieldMod *= mod.shieldMod;
	md.steamMod *= mod.steamMod;
	md.waterMod *= mod.waterMod;
	return md;
    }

    public SpellDamageModifier add(SpellDamageModifier mod)
    {
	SpellDamageModifier md = new SpellDamageModifier();
	md.arcaneMod = combineModifier(arcaneMod, mod.arcaneMod);
	md.coldMod = combineModifier(coldMod, mod.coldMod);
	md.earthMod = combineModifier(earthMod, mod.earthMod);
	md.fireMod = combineModifier(fireMod, mod.fireMod);
	md.iceMod = combineModifier(iceMod, mod.iceMod);
	md.lifeMod = combineModifier(lifeMod, mod.lifeMod);
	md.lightningMod = combineModifier(lightningMod, mod.lightningMod);
	md.shieldMod = combineModifier(shieldMod, mod.shieldMod);
	md.steamMod = combineModifier(steamMod, mod.steamMod);
	md.waterMod = combineModifier(waterMod, mod.waterMod);
	return md;
    }

    public double combineModifier(double d1, double d2)
    {
	double dif1 = d1 - 1;
	double dif2 = d2 - 1;
	double difCombined = dif1 + dif2;
	return 1 + difCombined;
    }

    public void amplifyEffect(Element e, double multiply)
    {
	switch (e)
	{
	    case Arcane:
		arcaneMod = amplify(arcaneMod, multiply);
		break;
	    case Cold:
		coldMod = amplify(coldMod, multiply);
		break;
	    case Earth:
		earthMod = amplify(earthMod, multiply);
		break;
	    case Fire:
		fireMod = amplify(fireMod, multiply);
		break;
	    case Ice:
		iceMod = amplify(iceMod, multiply);
		break;
	    case Life:
		lifeMod = amplify(lifeMod, multiply);
		break;
	    case Lightning:
		lightningMod = amplify(lightningMod, multiply);
		break;
	    case Shield:
		shieldMod = amplify(shieldMod, multiply);
		break;
	    case Steam:
		steamMod = amplify(steamMod, multiply);
		break;
	    case Water:
		waterMod = amplify(waterMod, multiply);
		break;
	    default:
		break;
	}
    }

    public void amplifyEffects(double multiply)
    {
	arcaneMod = amplify(arcaneMod, multiply);
	coldMod = amplify(coldMod, multiply);
	earthMod = amplify(earthMod, multiply);
	fireMod = amplify(fireMod, multiply);
	iceMod = amplify(iceMod, multiply);
	lifeMod = amplify(lifeMod, multiply);
	lightningMod = amplify(lightningMod, multiply);
	shieldMod = amplify(shieldMod, multiply);
	steamMod = amplify(steamMod, multiply);
	waterMod = amplify(waterMod, multiply);
    }

    public double amplify(double d, double multiply)
    {
	double dif = Math.abs(d - 1);
	dif *= multiply;
	if (d < 1) d = 1 - dif;
	else d = 1 + dif;
	return d;
    }

    public void clampUnderZero()
    {
	if (arcaneMod < 0) arcaneMod = 0;
	if (coldMod < 0) coldMod = 0;
	if (earthMod < 0) earthMod = 0;
	if (fireMod < 0) fireMod = 0;
	if (iceMod < 0) iceMod = 0;
	if (lifeMod < 0) lifeMod = 0;
	if (lightningMod < 0) lightningMod = 0;
	if (shieldMod < 0) shieldMod = 0;
	if (steamMod < 0) steamMod = 0;
	if (waterMod < 0) waterMod = 0;
    }

    /**
     * lightning will be indicated by letter H, shield will be indicated by letter D. The rest are indicated by the first letter.
     **/
    public SpellDamageModifier setModifiers(String s)
    {
	if (s == null)
	{
	    return this;
	}
	s = s.toLowerCase();
	s = s.replaceAll(",", ".");
	String currentValue = "";
	for (int a = 0; a < s.length(); a++)
	{
	    char ch = s.charAt(a);
	    if (Character.getType(ch) == Character.LOWERCASE_LETTER)
	    {
		if (a > 0 && currentValue.length() > 0)
		{
		    double value = Double.parseDouble(currentValue);
		    switch (ch)
		    {
			case 'a':
			    arcaneMod = value;
			    break;
			case 'c':
			    coldMod = value;
			    break;
			case 'd':
			    shieldMod = value;
			    break;
			case 'e':
			    earthMod = value;
			    break;
			case 'f':
			    fireMod = value;
			    break;
			case 'h':
			    lightningMod = value;
			    break;
			case 'i':
			    iceMod = value;
			    break;
			case 'l':
			    lifeMod = value;
			    break;
			case 's':
			    steamMod = value;
			    break;
			case 'w':
			    waterMod = value;
			    break;
			default:
			    break;
		    }
		}
		currentValue = "";
	    }
	    if (Character.isDigit(ch) || ch == '.' || ch == '-')
	    {
		currentValue += String.valueOf(ch);
	    }
	}
	return this;
    }

    public boolean isDefaultModifier()
    {
	return equals(defau);
    }

    public double getModifierFor(Element e)
    {
	switch (e)
	{
	    case Arcane:
		return arcaneMod;
	    case Cold:
		return coldMod;
	    case Earth:
		return earthMod;
	    case Fire:
		return fireMod;
	    case Ice:
		return iceMod;
	    case Life:
		return lifeMod;
	    case Lightning:
		return lightningMod;
	    case Shield:
		return shieldMod;
	    case Steam:
		return steamMod;
	    case Water:
		return waterMod;
	    default:
		return 1;
	}
    }

    @Override
    public boolean equals(Object obj)
    {
	if (obj instanceof SpellDamageModifier)
	{
	    SpellDamageModifier mod = (SpellDamageModifier) obj;
	    if (mod.arcaneMod == arcaneMod && mod.coldMod == coldMod && mod.earthMod == earthMod && mod.fireMod == fireMod && mod.iceMod == iceMod
		    && mod.lifeMod == lifeMod && mod.lightningMod == lightningMod && mod.shieldMod == shieldMod && mod.steamMod == steamMod
		    && mod.waterMod == waterMod) return true;
	}
	return false;
    }

    @Override
    public String toString()
    {
	String s = "";
	if (arcaneMod != 1) s += arcaneMod + "a";
	if (coldMod != 1) s += coldMod + "c";
	if (shieldMod != 1) s += shieldMod + "d";
	if (earthMod != 1) s += earthMod + "e";
	if (fireMod != 1) s += fireMod + "f";
	if (lightningMod != 1) s += lightningMod + "h";
	if (iceMod != 1) s += iceMod + "i";
	if (lifeMod != 1) s += lifeMod + "l";
	if (steamMod != 1) s += steamMod + "s";
	if (waterMod != 1) s += waterMod + "w";
	return s;
    }

    public SpellDamageModifier copy()
    {
	return new SpellDamageModifier(toString());
    }
}
