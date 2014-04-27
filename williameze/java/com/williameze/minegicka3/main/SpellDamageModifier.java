package com.williameze.minegicka3.main;

public class SpellDamageModifier
{
    public static SpellDamageModifier defau = new SpellDamageModifier();

    /**
     * lightning will be indicated by letter H, shield will be indicated by
     * letter D. The rest are indicated by the first letter.
     **/
    public double arcaneMod, coldMod, earthMod, fireMod, iceMod, lifeMod, lightningMod, shieldMod, steamMod, waterMod;

    public SpellDamageModifier()
    {
	arcaneMod = coldMod = earthMod = fireMod = iceMod = lifeMod = lightningMod = shieldMod = steamMod = waterMod = 1;
    }

    public SpellDamageModifier(double d)
    {
	arcaneMod = coldMod = earthMod = fireMod = iceMod = lifeMod = lightningMod = shieldMod = steamMod = waterMod = d;
    }

    /**
     * lightning will be indicated by letter H, shield will be indicated by
     * letter D. The rest are indicated by the first letter.
     **/
    public SpellDamageModifier(String s)
    {
	this();
	setModifiers(s);
    }

    /**
     * lightning will be indicated by letter H, shield will be indicated by
     * letter D. The rest are indicated by the first letter.
     **/
    public void setModifiers(String s)
    {
	s = s.toLowerCase();
	s = s.replace(',', '.');
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
	    if (Character.isDigit(ch) || ch == '.')
	    {
		currentValue += String.valueOf(ch);
	    }
	}
    }

    @Override
    public int hashCode()
    {
	return (int) (arcaneMod + coldMod + earthMod + fireMod + iceMod + lifeMod + lightningMod + steamMod + shieldMod + waterMod);
    }

    @Override
    public String toString()
    {
	return arcaneMod + "a" + coldMod + "c" + shieldMod + "d" + earthMod + "e" + fireMod + "f" + lightningMod + "h" + iceMod + "i" + lifeMod + "l" + steamMod + "s"
		+ waterMod + "w";
    }
}
