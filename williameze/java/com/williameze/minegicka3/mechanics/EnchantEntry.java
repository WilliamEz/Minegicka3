package com.williameze.minegicka3.mechanics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Items;

import com.williameze.api.lib.FuncHelper;

/**
 * Uses add-up rule, not multiplying rule
 * 
 * @author WillEze
 */
public class EnchantEntry
{
    public static enum StatBasic
    {
	Power("Power"), AtkSpeed("Atk Speed"), ConsumeRate("Mana Consume"), RecoverRate("Mana Recover");
	String name;

	private StatBasic(String s)
	{
	    name = s;
	}

	@Override
	public String toString()
	{
	    return name;
	}
    }

    /** Item or block **/
    public Object enchantee;

    public Map<StatBasic, Double> statEnchanting = new HashMap();
    public StatBasic strongStat;
    public StatBasic weakStat;

    public SpellDamageModifier elementEnchanting;
    public Element strongElement;
    public Element weakElement;

    public EnchantEntry()
    {
	statEnchanting.put(StatBasic.Power, 0D);
	statEnchanting.put(StatBasic.AtkSpeed, 0D);
	statEnchanting.put(StatBasic.ConsumeRate, 0D);
	statEnchanting.put(StatBasic.RecoverRate, 0D);
	elementEnchanting = new SpellDamageModifier(1);
    }

    public EnchantEntry(Object item, double power, double spd, double consume, double recharge, String s)
    {
	this();
	enchantee = item;
	statEnchanting.put(StatBasic.Power, power);
	statEnchanting.put(StatBasic.AtkSpeed, spd);
	statEnchanting.put(StatBasic.ConsumeRate, consume);
	statEnchanting.put(StatBasic.RecoverRate, recharge);
	elementEnchanting = new SpellDamageModifier(s);

	analizeEnchantment();
    }

    public void analizeEnchantment()
    {
	double power = statEnchanting.get(StatBasic.Power);
	double spd = statEnchanting.get(StatBasic.AtkSpeed);
	double consume = statEnchanting.get(StatBasic.ConsumeRate);
	double recharge = statEnchanting.get(StatBasic.RecoverRate);
	double statMax = Math.max(power, Math.max(consume, Math.max(recharge, spd)));
	double statMin = Math.min(power, Math.min(consume, Math.min(recharge, spd)));
	if (statMax > 0 || statMin < 0)
	{
	    StatBasic max = null;
	    StatBasic min = null;
	    boolean neutralMax = false;
	    boolean neutralMin = false;
	    for (StatBasic stat : StatBasic.values())
	    {
		if (!neutralMax && statEnchanting.get(stat) == statMax)
		{
		    if (max != null)
		    {
			max = null;
			neutralMax = true;
		    }
		    else
		    {
			max = stat;
		    }
		}
		if (!neutralMin && statEnchanting.get(stat) == statMin)
		{
		    if (min != null)
		    {
			min = null;
			neutralMin = true;
		    }
		    else
		    {
			min = stat;
		    }
		}
	    }
	    if (statMax <= 0) max = null;
	    if (statMin >= 0) min = null;
	    strongStat = max;
	    weakStat = min;
	}

	double emax = 1, emin = 1;
	for (Element e : Element.values())
	{
	    if (elementEnchanting.getModifierFor(e) > emax)
	    {
		strongElement = e;
		emax = elementEnchanting.getModifierFor(e);
	    }
	    else if (emax > 1 && elementEnchanting.getModifierFor(e) == emax)
	    {
		strongElement = null;
	    }
	    if (elementEnchanting.getModifierFor(e) < emin)
	    {
		weakElement = e;
		emin = elementEnchanting.getModifierFor(e);
	    }
	    else if (emin < 1 && elementEnchanting.getModifierFor(e) == emin)
	    {
		weakElement = null;
	    }
	}
    }

    public EnchantEntry(Object item, Object... data)
    {
	this();
	enchantee = item;
	StatBasic theStat = null;
	for (int a = 0; a < data.length; a++)
	{
	    Object o = data[a];
	    if (o instanceof StatBasic)
	    {
		theStat = (StatBasic) o;
		if (a + 1 < data.length && data[a + 1] instanceof Number)
		{
		    statEnchanting.put(theStat, (Double) o);
		}
		if (a + 2 < data.length && data[a + 2] instanceof Boolean)
		{
		    if (data[a + 2] == Boolean.TRUE) strongStat = (StatBasic) theStat;
		    else weakStat = (StatBasic) theStat;
		}
	    }
	    if (o instanceof Element)
	    {
		if (data[a + 1] == Boolean.TRUE) strongElement = (Element) o;
		else weakElement = (Element) o;
	    }
	    if (o instanceof String)
	    {
		elementEnchanting = new SpellDamageModifier((String) o);
	    }
	    if (o instanceof SpellDamageModifier)
	    {
		elementEnchanting = (SpellDamageModifier) o;
	    }
	}
    }

    public void multiplyStat(StatBasic stat, double multiply)
    {
	double d = statEnchanting.get(stat);
	d *= multiply;
	statEnchanting.put(stat, d);
    }

    public void multiplyElement(Element e, double multiply)
    {
	elementEnchanting.amplifyEffect(e, multiply);
    }

    public EnchantEntry affectBy(EnchantEntry entry)
    {
	EnchantEntry current = copy();
	if (current.strongStat != null)
	{
	    if (current.strongStat == entry.strongStat) current.multiplyStat(current.strongStat, 2);
	    if (current.strongStat == entry.weakStat) current.multiplyStat(current.strongStat, 0);
	}
	if (current.weakStat != null)
	{
	    if (current.weakStat == entry.weakStat) current.multiplyStat(current.weakStat, 2);
	    if (current.weakStat == entry.strongStat) current.multiplyStat(current.weakStat, 0);
	}
	if (current.strongElement != null)
	{
	    if (current.strongElement == entry.strongElement) current.multiplyElement(current.strongElement, 2);
	    if (current.strongElement == entry.weakElement) current.multiplyElement(current.strongElement, 0);
	}
	if (current.weakElement != null)
	{
	    if (current.weakElement == entry.weakElement) current.multiplyElement(current.weakElement, 2);
	    if (current.weakElement == entry.strongElement) current.multiplyElement(current.weakElement, 0);
	}
	return current;
    }

    public List<String> getEffectStrings(boolean positive)
    {
	List<String> list = new ArrayList();

	for (StatBasic stat : StatBasic.values())
	{
	    String sign = positive ^ stat == StatBasic.ConsumeRate ? "+" : "-";
	    if (statEnchanting.get(stat) * (positive ? 1 : -1) > 0)
	    {
		list.add(sign + FuncHelper.formatToPercentage(Math.abs(statEnchanting.get(stat))) + " " + stat.toString());
	    }
	}

	String sign = positive ? "+" : "-";
	for (Element element : Element.values())
	{
	    if ((elementEnchanting.getModifierFor(element) - 1) * (positive ? 1 : -1) > 0)
	    {
		list.add(sign + new DecimalFormat("#.####").format(Math.abs(elementEnchanting.getModifierFor(element) - 1)) + " "
			+ element.toString() + " efficiency");
	    }
	}
	return list;
    }

    public EnchantEntry copy()
    {
	EnchantEntry entry = new EnchantEntry();
	entry.enchantee = enchantee;
	entry.elementEnchanting = new SpellDamageModifier(elementEnchanting.toString());
	entry.strongElement = strongElement;
	entry.weakElement = weakElement;
	entry.statEnchanting = new HashMap();
	entry.statEnchanting.putAll(statEnchanting);
	entry.strongStat = strongStat;
	entry.weakStat = weakStat;
	return entry;
    }

    public String compressToString()
    {
	String s = "";
	for (int a = 0; a < StatBasic.values().length; a++)
	{
	    s += statEnchanting.get(StatBasic.values()[a]) + ";";
	}

	if (strongStat == null) s += "-1";
	else s += strongStat.ordinal();
	s += ";";
	if (weakStat == null) s += "-1";
	else s += weakStat.ordinal();
	s += ";";

	s += elementEnchanting.toString() + ";";

	if (strongElement == null) s += "-1";
	else s += strongElement.ordinal();
	s += ";";
	if (weakElement == null) s += "-1";
	else s += weakElement.ordinal();
	s += ";";

	return s;
    }

    public static EnchantEntry loadFromString(String s)
    {
	if (s != null)
	{
	    EnchantEntry entry = new EnchantEntry();
	    String[] splitted = s.split(";");
	    int basicStats = StatBasic.values().length;
	    for (int a = 0; a < basicStats; a++)
	    {
		entry.statEnchanting.put(StatBasic.values()[a], Double.parseDouble(splitted[a]));
	    }
	    if (Integer.parseInt(splitted[basicStats]) != -1) entry.strongStat = StatBasic.values()[Integer.parseInt(splitted[basicStats])];
	    if (Integer.parseInt(splitted[basicStats + 1]) != -1) entry.weakStat = StatBasic.values()[Integer.parseInt(splitted[basicStats + 1])];
	    entry.elementEnchanting = new SpellDamageModifier(splitted[basicStats + 2]);
	    if (Integer.parseInt(splitted[basicStats + 3]) != -1) entry.strongElement = Element.values()[Integer.parseInt(splitted[basicStats + 3])];
	    if (Integer.parseInt(splitted[basicStats + +4]) != -1) entry.weakElement = Element.values()[Integer.parseInt(splitted[basicStats + 4])];
	}
	return null;
    }
}
