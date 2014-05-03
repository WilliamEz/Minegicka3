package com.williameze.api.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NoiseGen1D
{
    public long seed;
    public Random random;
    private double[] noises;
    public int range;
    public int index;
    public double maxCap, minCap;
    public double maxDifChange, minDifChange;

    public double shifting;

    public NoiseGen1D(long l, int i)
    {
	this((Long) l, i);
    }

    public NoiseGen1D(Long l, int i)
    {
	if (l != null) seed = new Random().nextLong();
	random = new Random(seed);

	range = i;
	noises = new double[range];
	minCap = 0;
	maxCap = 1;
    }

    public NoiseGen1D setCap(double min, double max)
    {
	minCap = min;
	maxCap = max;
	return this;
    }

    public NoiseGen1D setDifs(double base, double minChange)
    {
	maxDifChange = base;
	minDifChange = minChange;
	return this;
    }

    public double noiseAt(double d)
    {
	d = Math.max(0, Math.min(d, noises.length - 1));
	int floorD = (int) Math.floor(d);
	int ceilD = (int) Math.ceil(d);
	return (noises[floorD] + noises[ceilD]) / 2D;
    }

    public void generate()
    {
	generateFromTo(0, range - 1);
	if (index >= range - 1) index = 0;
    }

    public void generateAround(int i, double nextDif)
    {
	if (i >= range) return;
	generateAround(i, noises[i], nextDif);
    }

    public void generateAround(int i, double newValue, double nextDif)
    {
	if (i < 0 || i >= range) return;
	noises[i] = newValue;

	int prevRange = i + 2;
	NoiseGen1D prevReverse = new NoiseGen1D(seed, prevRange).setCap(minCap, maxCap).setDifs(maxDifChange, minDifChange);
	prevReverse.noises[0] = newValue + nextDif;
	prevReverse.noises[1] = newValue;
	prevReverse.generateFrom(1);
	for (int a = 0; a < i; a++)
	{
	    noises[a] = prevReverse.noises[prevReverse.range - a - 1];
	}

	int nextRange = range - i + 2;
	NoiseGen1D nextPreserve = new NoiseGen1D(seed, nextRange).setCap(minCap, maxCap).setDifs(maxDifChange, minDifChange);
	nextPreserve.noises[0] = noises[i - 1];
	nextPreserve.noises[1] = newValue;
	nextPreserve.generateFrom(1);
	for (int a = i + 1; a < range; a++)
	{
	    noises[a] = nextPreserve.noises[a - i + 1];
	}
    }

    public void generateFrom(int i)
    {
	generateFromTo(i, range - 1);
	if (index >= range - 1) index = 0;
    }

    public void generateTo(int i)
    {
	generateFromTo(0, i);
    }

    public void generateFromTo(int i1, int i2)
    {
	if (i2 < i1)
	{
	    int i = i1;
	    i1 = i2;
	    i2 = i;
	}
	i1 = Math.max(0, i1);
	i2 = Math.min(range - 1, i2);
	if (maxCap < minCap)
	{
	    double d = minCap;
	    minCap = maxCap;
	    maxCap = d;
	}
	double[] oldNoises = noises;
	noises = new double[range];
	for (int a = 0; a < Math.min(range, oldNoises.length); a++)
	{
	    noises[a] = oldNoises[a];
	}
	double progression = (random.nextInt(2) * 2 - 1) * minDifChange;
	if (i1 > 0)
	{
	    progression += noises[i1] - noises[i1 - 1];
	}
	for (index = i1; index <= i2; index++)
	{
	    if (index == 0)
	    {
		noises[index] = minCap + random.nextDouble() * (maxCap - minCap);
		continue;
	    }
	    double progressionChange = (random.nextDouble() - 0.5) * 2 * maxDifChange;
	    if (Math.abs(progressionChange) < minDifChange) progressionChange = 0;
	    boolean progressionChanged = false;
	    while (noises[index - 1] + progression + progressionChange > maxCap)
	    {
		progressionChange = (random.nextDouble() - 0.5) * 2 * maxDifChange;
		if (Math.abs(progressionChange) < minDifChange) progressionChange = 0;
		if (progressionChange < 0)
		{
		    progression += progressionChange;
		    progressionChanged = true;
		}
	    }
	    while (noises[index - 1] + progression + progressionChange < minCap)
	    {
		progressionChange = (random.nextDouble() - 0.5) * 2 * maxDifChange;
		if (Math.abs(progressionChange) < minDifChange) progressionChange = 0;
		if (progressionChange > 0)
		{
		    progression += progressionChange;
		    progressionChanged = true;
		}
	    }
	    if (!progressionChanged) progression += progressionChange;
	    double nextNoise = noises[index - 1] + progression;
	    noises[index] = Math.max(Math.min(nextNoise, maxCap), minCap);
	}
    }

    public void clamp(double clamp)
    {
	int maxIndex = -1;
	int minIndex = -1;
	for (int a = 0; a < noises.length; a++)
	{
	    double noise = noises[a];
	    if (a == 0)
	    {
		if (noise > noises[a + 1]) maxIndex = a;
		else if (noise < noises[a + 1]) minIndex = a;
		else
		{
		    maxIndex = a;
		    minIndex = a + 1;
		}
	    }
	    else if (a == noises.length - 1)
	    {
		if (noise > noises[a - 1]) maxIndex = a;
		else if (noise < noises[a - 1]) minIndex = a;
		else
		{
		    maxIndex = a - 1;
		    minIndex = a;
		}
	    }
	    else
	    {
		if (noise >= noises[a - 1] && noise > noises[a + 1])
		{
		    maxIndex = a;
		}
		else if (noise < noises[a - 1] && noise <= noises[a + 1])
		{
		    minIndex = a;
		}
	    }

	    if (maxIndex != -1 && minIndex != -1)
	    {
		double median = (noises[maxIndex] + noises[minIndex]) / 2D;
		for (int b = Math.min(maxIndex, minIndex); b <= Math.max(maxIndex, minIndex); b++)
		{
		    double noiseThere = noises[b];
		    double newNoiseThere = (noiseThere - median) / clamp + median;
		    noises[b] = newNoiseThere;
		}
		maxIndex = minIndex = -1;
	    }
	}
    }

    public void smooth(double smooth, int loop)
    {
	for (int x = 0; x < loop; x++)
	{
	    for (int a = 0; a < noises.length; a++)
	    {
		double prev = a == 0 ? noises[a] : noises[a - 1];
		double next = a == noises.length - 1 ? noises[a] : noises[a + 1];
		double current = noises[a];
		double mid = (prev + next) / 2;
		double change = (mid - current) * smooth;
		current += change;
		noises[a] = current;
	    }
	}
    }

    public void shift(double id)
    {
	shifting += id;
	if (shifting >= 1)
	{
	    int is = (int) Math.floor(shifting);
	    int i = is % range;
	    double[] noisesOld = noises.clone();
	    for (int a = 0; a < range; a++)
	    {
		noises[a] = noisesOld[(a + i >= range ? i - 1 : (a + i < 0 ? range - i : a + i))];
	    }
	    shifting -= i;
	}
    }
}
