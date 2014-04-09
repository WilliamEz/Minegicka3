package com.williameze.minegicka3.main.renders;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.williameze.minegicka3.bridges.Values;
import com.williameze.minegicka3.main.models.ModelEntitySpray;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderEntitySpray extends Render
{
    public ModelEntitySpray model = new ModelEntitySpray();
    private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);
    private static final Vec3 v1 = Vec3.createVectorHelper(1, 1, 1).normalize();
    private static final Vec3 v2 = Vec3.createVectorHelper(-1, -1, -1).normalize();

    protected void bindEntityTexture(Entity par1Entity)
    {
    }

    protected void bindTexture(ResourceLocation par1ResourceLocation)
    {
    }

    @Override
    public void doRender(Entity var1, double x, double y, double z, float yaw, float partialTick)
    {
	v1.xCoord = 1;
	v2.zCoord = -1;
	GL11.glPushMatrix();
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glDisable(GL11.GL_CULL_FACE);
	GL11.glTranslated(x, y + var1.width / 2, z);

	enableLighting();
	model.render(var1, partialTick);
	disableLighting();
	RenderHelper.enableStandardItemLighting();
	RenderHelper.disableStandardItemLighting();

	GL11.glEnable(GL11.GL_CULL_FACE);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
	GL11.glPopMatrix();
    }

    public static void enableLighting()
    {
	GL11.glEnable(GL11.GL_LIGHTING);
	GL11.glEnable(GL11.GL_LIGHT0);
	GL11.glEnable(GL11.GL_LIGHT1);
	GL11.glEnable(GL11.GL_COLOR_MATERIAL);
	GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
	float f = 0.3F;
	float f1 = 0.25F;
	float f2 = 0.1F;
	GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, setColorBuffer(v1.xCoord, v1.yCoord, v1.zCoord, 0.0D));
	GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, setColorBuffer(f1, f1, f1, 1.0F));
	GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
	GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, setColorBuffer(f2, f2, f2, 1.0F));
	GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, setColorBuffer(v2.xCoord, v2.yCoord, v2.zCoord, 0.0D));
	GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, setColorBuffer(f1, f1, f1, 1.0F));
	GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
	GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, setColorBuffer(f2, f2, f2, 1.0F));
	GL11.glShadeModel(GL11.GL_SMOOTH);
	GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, setColorBuffer(f, f, f, 1.0F));
    }

    public static void disableLighting()
    {
	GL11.glDisable(GL11.GL_LIGHTING);
	GL11.glDisable(GL11.GL_LIGHT0);
	GL11.glDisable(GL11.GL_LIGHT1);
	GL11.glDisable(GL11.GL_COLOR_MATERIAL);
    }

    /**
     * Update and return colorBuffer with the RGBA values passed as arguments
     */
    private static FloatBuffer setColorBuffer(double par0, double par2, double par4, double par6)
    {
	/**
	 * Update and return colorBuffer with the RGBA values passed as
	 * arguments
	 */
	return setColorBuffer((float) par0, (float) par2, (float) par4, (float) par6);
    }

    /**
     * Update and return colorBuffer with the RGBA values passed as arguments
     */
    private static FloatBuffer setColorBuffer(float par0, float par1, float par2, float par3)
    {
	colorBuffer.clear();
	colorBuffer.put(par0).put(par1).put(par2).put(par3);
	colorBuffer.flip();
	/**
	 * Float buffer used to set OpenGL material colors
	 */
	return colorBuffer;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
