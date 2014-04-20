package com.williameze.api.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.williameze.api.math.IntVector;
import com.williameze.api.math.PositionedVector;
import com.williameze.minegicka3.bridges.Values;

public class NoiseGen2D
{
    public long seed;
    public Random random;
    public double[][] noises;
    public boolean[][] fixedPoints;
    public List<IntVector> fixedPointsList = new ArrayList();

    public int rangeX, rangeY;
    public double maxCap, minCap;
    public double maxR, minR;
    public double transformCap;

    public NoiseGen2D(Long l, int i, int j)
    {
	if (l != null) seed = new Random().nextLong();
	random = new Random(seed);

	rangeX = i;
	rangeY = j;
	noises = new double[rangeX][rangeY];
	fixedPoints = new boolean[rangeX][rangeY];
	setCap(0, 1);
	setRadius(0, 1);
    }

    public NoiseGen2D setCap(double min, double max)
    {
	maxCap = Math.max(max, min);
	minCap = Math.min(max, min);
	return this;
    }

    public NoiseGen2D setNoisetainProps(double minRadius, double maxRadius, double maxHeight)
    {
	maxR = Math.max(minRadius, maxRadius);
	minR = Math.min(minRadius, maxRadius);
	transformCap = maxHeight;
	return this;
    }

    public NoiseGen2D setRadius(double min, double max)
    {
	maxR = Math.max(max, min);
	minR = Math.min(max, min);
	return this;
    }

    public NoiseGen2D setTransformingCap(double h)
    {
	transformCap = h;
	return this;
    }

    public void setFixed(int i, int j, boolean fix)
    {
	if (i >= 0 && j >= 0 && i < rangeX && j < rangeY)
	{
	    fixedPoints[i][j] = fix;
	    IntVector point = new IntVector(i, j, 0);
	    if (fix) fixedPointsList.add(point);
	    else fixedPointsList.remove(point);
	}
    }

    public void reset(double resetToValue)
    {
	resetToValue = Math.max(Math.min(resetToValue, maxCap), minCap);
	noises = new double[rangeX][rangeY];
	fixedPoints = new boolean[rangeX][rangeY];
	fixedPointsList.clear();
	setFixed(0, 0, true);
	noises[0][0] = resetToValue;

	for (int i = 0; i < rangeX; i++)
	{
	    for (int j = 0; j < rangeY; j++)
	    {
		noises[i][j] = resetToValue;
	    }
	}
    }

    public void generate(int loop, boolean reset, double resetToValue)
    {
	if (reset) reset(resetToValue);
	for (int a = 0; a < loop; a++)
	{
	    generate();
	}
    }

    public void generate()
    {
	int maxCount = (int) (rangeX * rangeY / (maxR + minR) / (maxR + minR));
	int count = random.nextInt(maxCount);
	for (int a = 0; a < count; a++)
	{
	    int i = random.nextInt(rangeX);
	    int j = random.nextInt(rangeY);
	    if (fixedPoints[i][j])
	    {
		continue;
	    }
	    double radius = random.nextDouble() * (maxR - minR) + minR;
	    double radiusSqr = radius * radius;
	    double closestFixedDistSqr = getDistToClosestFixedSqr(i, j);
	    if (radiusSqr > closestFixedDistSqr)
	    {
		radiusSqr = closestFixedDistSqr;
	    }
	    double change = (random.nextDouble() - 0.5) * 2 * transformCap;
	    double value = noises[i][j] + change;
	    value = Math.max(Math.min(maxCap, value), minCap);

	    int minX = (int) Math.round(i - radius);
	    int minY = (int) Math.round(j - radius);
	    int maxX = (int) Math.round(i + radius);
	    int maxY = (int) Math.round(j + radius);
	    minX = Math.max(Math.min(rangeX - 1, minX), 0);
	    minY = Math.max(Math.min(rangeY - 1, minY), 0);
	    maxX = Math.max(Math.min(rangeX - 1, maxX), 0);
	    maxY = Math.max(Math.min(rangeY - 1, maxY), 0);
	    for (int i1 = minX; i1 <= maxX; i1++)
	    {
		for (int j1 = minY; j1 <= maxY; j1++)
		{
		    if (fixedPoints[i1][j1]) continue;
		    double distSqr = getDistSqr(i, j, i1, j1);
		    if (distSqr <= radiusSqr)
		    {
			double changeRate = (radiusSqr - distSqr) / radiusSqr;
			double changeInValue = change * changeRate;
			noises[i1][j1] = Math.max(Math.min(maxCap, noises[i1][j1] + changeInValue), minCap);
		    }
		}
	    }
	}
    }

    public double getDistToClosestFixed(int i, int j)
    {
	return Math.sqrt(getDistToClosestFixedSqr(i, j));
    }

    public double getDistToClosestFixedSqr(int i, int j)
    {
	if (fixedPointsList.isEmpty()) return maxR * maxR;
	if (fixedPoints[i][j]) return 0;
	double closestFixDistSqr = -1;
	for (int a = 0; a < fixedPointsList.size(); a++)
	{
	    IntVector fix = fixedPointsList.get(a);
	    double distToFixSqr = getDistSqr(i, j, fix.x, fix.y);
	    if (closestFixDistSqr == -1 || distToFixSqr < closestFixDistSqr) closestFixDistSqr = distToFixSqr;
	}
	return closestFixDistSqr;
    }

    public double getFixedMedianAt(int i, int j)
    {
	if (fixedPointsList.isEmpty()) return 0;
	if (fixedPoints[i][j]) return noises[i][j];
	double closestFixDistSqr = -1;
	double value = 0;
	for (int a = 0; a < fixedPointsList.size(); a++)
	{
	    IntVector fix = fixedPointsList.get(a);
	    double distToFixSqr = getDistSqr(i, j, fix.x, fix.y);
	    if (closestFixDistSqr == -1 || distToFixSqr < closestFixDistSqr) closestFixDistSqr = distToFixSqr;
	    value += noises[fix.x][fix.y] / Math.sqrt(distToFixSqr);
	}
	value = value * Math.sqrt(closestFixDistSqr) / fixedPointsList.size();
	return value;
    }

    public double getDistSqr(int i1, int j1, int i2, int j2)
    {
	return (i1 - i2) * (i1 - i2) + (j1 - j2) * (j1 - j2);
    }

    public double getDist(int i1, int j1, int i2, int j2)
    {
	return Math.sqrt(getDistSqr(i1, j1, i2, j2));
    }

    public void clamp(double clamp)
    {

    }

    public void smooth(double smooth, int loop)
    {
	for (int a = 0; a < loop; a++)
	{
	    for (int i = 0; i < rangeX; i++)
	    {
		for (int j = 0; j < rangeY; j++)
		{
		    if (fixedPoints[i][j]) continue;
		    double med = 0;
		    int count = 0;
		    if (i > 0)
		    {
			count++;
			med += noises[i - 1][j];
		    }
		    if (j > 0)
		    {
			count++;
			med += noises[i][j - 1];
		    }
		    if (i < rangeX - 1)
		    {
			count++;
			med += noises[i + 1][j];
		    }
		    if (j < rangeY - 1)
		    {
			count++;
			med += noises[i][j + 1];
		    }
		    if (count > 0) med = med / count;
		    double current = noises[i][j];
		    noises[i][j] = current + (med - current) * smooth;
		}
	    }
	}
    }
}
