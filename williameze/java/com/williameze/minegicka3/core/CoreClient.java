package com.williameze.minegicka3.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import com.williameze.minegicka3.bridges.DrawHelper;
import com.williameze.minegicka3.bridges.Values;

import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CoreClient
{
    /** Instantiate the CoreClient **/
    private CoreClient()
    {
    }

    private static CoreClient instance;

    public static CoreClient instance()
    {
	return instance == null ? (instance = new CoreClient()) : instance;
    }

    /** Minecraft instance **/
    public static Minecraft mc = Minecraft.getMinecraft();

    public void onClientTick(ClientTickEvent event)
    {
    }

    public void onClientWorldTick(WorldTickEvent event)
    {
    }

    public void onClientPlayerTick(PlayerTickEvent event)
    {
    }

    public void onClientRenderTick(RenderTickEvent event)
    {
    }

    @SideOnly(Side.CLIENT)
    public void onRenderGameOverlayTick(RenderGameOverlayEvent event)
    {
	ScaledResolution scaledResolution;

	if (event instanceof RenderGameOverlayEvent.Post && event.type == ElementType.ALL)
	{
	    scaledResolution = event.resolution;
	    int i = scaledResolution.getScaledWidth();
	    int j = scaledResolution.getScaledHeight();
	    mc.getTextureManager().bindTexture(Values.gui_Main);

	    int guiWidth = 170;
	    int guiHeight = 70;
	    int guiStartX = (Values.gui_Position.toString().contains("RIGHT")) ? i - guiWidth : 0;
	    int guiStartY = (Values.gui_Position.toString().contains("TOP")) ? 0 : j - guiHeight;

	    GL11.glPushMatrix();
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glTranslated(guiStartX, guiStartY, 0);
	    Tessellator tess = Tessellator.instance;
	    tess.startDrawingQuads();
	    DrawHelper.tessAddQuad(tess, 0, 0, guiWidth, guiHeight, 0, 0, 1, 1);
	    tess.draw();
	    GL11.glPopMatrix();
	}
    }
}
