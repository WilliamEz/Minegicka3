package com.williameze.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.api.lib.NoiseGen1D;
import com.williameze.api.lib.NoiseGen2D;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.models.ModelEntityBoulder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TestOverlay
{
    public static KeyBinding keyToggleTestOverlay = new KeyBinding("Test Overlay", Keyboard.KEY_COMMA, ModBase.MODNAME);
    public static boolean enabled = false;
    public static NoiseGen1D noise1d;
    public static NoiseGen2D noise2d;

    public static void render()
    {

	Minecraft mc = Minecraft.getMinecraft();
	ScaledResolution scaledResolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	int width = scaledResolution.getScaledWidth();
	int height = scaledResolution.getScaledHeight();
	DrawHelper.drawRect(0, 0, width, height, 1, 1, 1, 1);

	renderTestNoiseGen2D();
    }

    public static void renderTestNoiseGen1D()
    {
	Minecraft mc = Minecraft.getMinecraft();
	ScaledResolution scaledResolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	int width = scaledResolution.getScaledWidth();
	int height = scaledResolution.getScaledHeight();

	GL11.glPushMatrix();
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glShadeModel(GL11.GL_SMOOTH);
	GL11.glEnable(GL11.GL_LINE_SMOOTH);
	GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glTranslated(12, height / 2, 10);
	GL11.glScaled(1, -1, 1);

	if (noise1d == null)
	{
	    noise1d = new NoiseGen1D(null, 400);
	    noise1d.setCap(20, 120);
	    noise1d.setDifs(10, 3);
	    noise1d.generate();
	    noise1d.smooth(1, 48);
	}
	if (Values.clientTicked % 100 == 0)
	{
	    noise1d.generate();
	    noise1d.smooth(1, 16);
	}

	GL11.glLineWidth(20);
	GL11.glBegin(GL11.GL_QUAD_STRIP);
	for (int i = 0; i < noise1d.range; i++)
	{
	    double noiseAt = noise1d.noises[i];
	    double[] color = getColor(noise1d, noiseAt);
	    GL11.glColor3d(color[0], color[1], color[2]);
	    GL11.glVertex3d(i, noiseAt, 0);

	    double[] color1 = getColor(noise1d, 0);
	    GL11.glColor3d(color1[0], color1[1], color1[2]);
	    GL11.glVertex3d(i, 0, 0);
	}
	for (int i = noise1d.range - 1; i >= 0; i--)
	{
	    double noiseAt = noise1d.noises[i];
	    double[] color = getColor(noise1d, noiseAt);
	    GL11.glColor3d(color[0], color[1], color[2]);
	    GL11.glVertex3d(i, -noiseAt, 0);

	    double[] color1 = getColor(noise1d, 0);
	    GL11.glColor3d(color1[0], color1[1], color1[2]);
	    GL11.glVertex3d(i, 0, 0);
	}
	GL11.glEnd();
	GL11.glLineWidth(1);
	GL11.glBegin(GL11.GL_LINE_LOOP);
	for (int i = 0; i < noise1d.range; i++)
	{
	    double noiseAt = noise1d.noises[i];
	    GL11.glColor3d(0, 0, 0);
	    GL11.glVertex3d(i, noiseAt, 0);
	}
	for (int i = noise1d.range - 1; i >= 0; i--)
	{
	    double noiseAt = noise1d.noises[i];
	    GL11.glColor3d(0, 0, 0);
	    GL11.glVertex3d(i, -noiseAt, 0);
	}
	GL11.glEnd();
	GL11.glDisable(GL11.GL_LINE_SMOOTH);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_BLEND);
	GL11.glPopMatrix();
    }

    public static void renderTestNoiseGen2D()
    {
	Minecraft mc = Minecraft.getMinecraft();
	ScaledResolution scaledResolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	int width = scaledResolution.getScaledWidth();
	int height = scaledResolution.getScaledHeight();

	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
	GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glEnable(GL11.GL_DEPTH_TEST);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glShadeModel(GL11.GL_SMOOTH);
	GL11.glTranslated(-25, height / 2, 100);
	GL11.glScaled(1, -1, 1);
	GL11.glRotated(45, 1, 1, 0);
	GL11.glScaled(4, 1, 4);

	if (noise2d == null)
	{
	    noise2d = new NoiseGen2D(null, width / 4, height / 4);
	    noise2d.setCap(0, 100);
	    noise2d.setNoisetainProps(0, 10, 10);
	    noise2d.generate(1, true, 50);
	}
	if (Values.clientTicked % 100 == 0)
	{
	    noise2d.setNoisetainProps(0, 10, 40);
	    noise2d.generate(16, true, 50);
	    noise2d.smooth(1, 32);
	}

	GL11.glBegin(GL11.GL_TRIANGLES);
	for (int i = 0; i < noise2d.rangeX; i++)
	{
	    for (int j = 0; j < noise2d.rangeY; j++)
	    {
		boolean increI = i < noise2d.rangeX - 1;
		boolean increJ = j < noise2d.rangeY - 1;

		double noiseAt = noise2d.noises[i][j];
		double[] colorAt = getColor(noise2d, noiseAt);
		GL11.glColor3d(colorAt[0], colorAt[1], colorAt[2]);
		GL11.glVertex3d(i, noiseAt, j);

		if (increI) i++;
		{
		    double noiseAtI = noise2d.noises[i][j];
		    double[] colorAtI = getColor(noise2d, noiseAtI);
		    GL11.glColor3d(colorAtI[0], colorAtI[1], colorAtI[2]);
		    GL11.glVertex3d(i, noiseAtI, j);
		}
		if (increI) i--;
		if (increJ) j++;
		{
		    double noiseAtJ = noise2d.noises[i][j];
		    double[] colorAtJ = getColor(noise2d, noiseAtJ);
		    GL11.glColor3d(colorAtJ[0], colorAtJ[1], colorAtJ[2]);
		    GL11.glVertex3d(i, noiseAtJ, j);
		}
		if (increJ) j--;

		if (increI) i++;
		{
		    double noiseAtI = noise2d.noises[i][j];
		    double[] colorAtI = getColor(noise2d, noiseAtI);
		    GL11.glColor3d(colorAtI[0], colorAtI[1], colorAtI[2]);
		    GL11.glVertex3d(i, noiseAtI, j);
		}

		if (increI) i--;
		if (increJ) j++;
		{
		    double noiseAtJ = noise2d.noises[i][j];
		    double[] colorAtJ = getColor(noise2d, noiseAtJ);
		    GL11.glColor3d(colorAtJ[0], colorAtJ[1], colorAtJ[2]);
		    GL11.glVertex3d(i, noiseAtJ, j);
		}

		if (increJ) j--;
		if (increI && increJ)
		{
		    i++;
		    j++;
		}

		{
		    double noiseAtB = noise2d.noises[i][j];
		    double[] colorAtB = getColor(noise2d, noiseAtB);
		    GL11.glColor3d(colorAtB[0], colorAtB[1], colorAtB[2]);
		    GL11.glVertex3d(i, noiseAtB, j);
		}

		if (increI && increJ)
		{
		    i--;
		    j--;
		}
	    }
	}
	GL11.glEnd();

	GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_BLEND);
	GL11.glPopMatrix();
    }

    public static double[] getColor(NoiseGen1D ng, double noiseAt)
    {
	double colorRate = (noiseAt - ng.minCap) / (ng.maxCap - ng.minCap);
	double red = 0.5D + colorRate / 2;
	double green = 1 - Math.abs(colorRate - 0.5) * 2;
	double blue = 1 - colorRate * 2;

	return new double[] { red, green, blue };
    }

    public static double[] getColor(NoiseGen2D ng, double noiseAt)
    {
	double colorRate = (noiseAt - ng.minCap) / (ng.maxCap - ng.minCap);
	double red = 0.5D + colorRate / 2;
	double green = 1 - Math.abs(colorRate - 0.5) * 2;
	double blue = 1 - colorRate * 2;

	return new double[] { red, green, blue };
    }
}
