package com.williameze.minegicka3.core;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.williameze.minegicka3.ModKeybinding;
import com.williameze.minegicka3.bridges.DrawHelper;
import com.williameze.minegicka3.bridges.Values;
import com.williameze.minegicka3.objects.ItemStaff;

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
    public Minecraft mc = Minecraft.getMinecraft();
    public List<Element> queuedElements = new ArrayList();
    public List<Entry<Element, int[]>> removingElements = new ArrayList();

    public boolean isWizard()
    {
	return mc != null && mc.thePlayer != null;
    }

    public boolean isWizardnessApplicable()
    {
	return isWizard() && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemStaff;
    }

    public void onClientTick(ClientTickEvent event)
    {
	if (isWizardnessApplicable())
	{
	    for (ModKeybinding mkb : ModKeybinding.elementKeys)
	    {
		if (mkb.isPressed()) playerQueueElement(mkb.element);
	    }
	}
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

    public void playerQueueElement(Element e)
    {
	for (int a = 0; a < queuedElements.size(); a++)
	{
	    Element e1 = queuedElements.get(a);
	    Element e2 = e.breakDown(e1);
	    if (e2 != null)
	    {
		replaceElement(a, Element.Water);
		return;
	    }
	}
	for (int a = 0; a < queuedElements.size(); a++)
	{
	    Element e1 = queuedElements.get(a);
	    if (e.isOpposite(e1))
	    {
		replaceElement(a, null);
		return;
	    }
	}
	for (int a = 0; a < queuedElements.size(); a++)
	{
	    Element e1 = queuedElements.get(a);
	    Element e2 = e.combineWith(e1);
	    if (e2 != null)
	    {
		replaceElement(a, e2);
		return;
	    }
	}

	queuedElements.add(e);
    }

    public void replaceElement(int index, Element replacement)
    {
	Element current = queuedElements.get(index);

	removingElements.add(new SimpleEntry(current, new int[] { index, 100 }));

	if (replacement != null) queuedElements.set(index, replacement);
	else queuedElements.remove(index);
    }

    public int alphaCounterTick = 0;
    public int maxACT = 100;

    @SideOnly(Side.CLIENT)
    public void onRenderGameOverlayTick(RenderGameOverlayEvent event)
    {
	ScaledResolution scaledResolution = event.resolution;

	/** Render hud **/
	if (isWizard() && event instanceof RenderGameOverlayEvent.Post && event.type == ElementType.ALL)
	{
	    alphaCounterTick += 20 * ((isWizardnessApplicable() && alphaCounterTick < maxACT) ? 1 : (!isWizardnessApplicable() && alphaCounterTick > 0) ? -1 : 0);
	    alphaCounterTick = Math.max(0, Math.min(alphaCounterTick, maxACT));
	    double fullTranslucent = (double) alphaCounterTick / (double) maxACT;
	    double partialTranslucent = 0.25F + 0.75F * fullTranslucent;
	    double partialTranslucent1 = 0.4F + 0.6F * fullTranslucent;

	    int i = scaledResolution.getScaledWidth();
	    int j = scaledResolution.getScaledHeight();

	    boolean positionTop = (Values.gui_Position.toString().contains("TOP"));
	    // positionTop = true;
	    if (!positionTop && mc.currentScreen instanceof GuiChat) return;
	    boolean positionRight = (Values.gui_Position.toString().contains("RIGHT"));
	    double marginX = 2;
	    double marginY = 2;

	    double hotkeyElementSize = 20;
	    double hotkeyElementGapX = 0;
	    double hotkeyElementGapY = 0;
	    double hotkeyWidth = hotkeyElementSize * 4 + hotkeyElementGapX * 3;
	    double hotkeyHeight = hotkeyElementSize * 2 + hotkeyElementGapY;

	    double manaRate = 1;//Math.abs(Math.cos(System.currentTimeMillis() % 1000 / 1000D * Math.PI));
	    double manaBarWidth = hotkeyWidth;
	    double manaBarHeight = 5;

	    int queuedElementsPerRow = 5;
	    int queuedElementsRows = (int) Math.ceil((double) queuedElements.size() / (double) queuedElementsPerRow);
	    double queuedElementSize = 15;
	    double queuedElementGapX = 2;
	    double queuedElementGapY = 0;
	    double queuedWidth = queuedElementSize * queuedElementsPerRow + queuedElementGapX * (Math.max(0, queuedElementsPerRow - 1));
	    double queuedHeight = queuedElementSize * queuedElementsRows + queuedElementGapY * (Math.max(0, queuedElementsRows - 1));

	    double guiWidth = Math.max(hotkeyWidth, Math.max(queuedWidth, manaBarWidth));
	    double guiHeight = hotkeyHeight + queuedHeight + manaBarHeight;
	    double guiStartX = positionRight ? i - guiWidth - marginX : marginX;
	    double guiStartY = positionTop ? marginY : j - guiHeight - marginY;

	    GL11.glPushMatrix();
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glTranslated(guiStartX, guiStartY, 0);

	    Tessellator tess = Tessellator.instance;

	    if (!positionTop) GL11.glTranslated(0, guiHeight, 0);
	    {
		if (!positionTop) GL11.glTranslated(0, -hotkeyHeight, 0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glTranslated((guiWidth - hotkeyWidth) / 2, 0, 0);
		GL11.glColor4d(1, 1, 1, partialTranslucent);
		drawHotkeyElements(hotkeyElementSize, hotkeyElementSize, hotkeyElementGapX, hotkeyElementGapY);
		GL11.glTranslated(-(guiWidth - hotkeyWidth) / 2, 0, 0);
		if (positionTop) GL11.glTranslated(0, hotkeyHeight, 0);
	    }
	    {
		if (!positionTop) GL11.glTranslated(0, -manaBarHeight, 0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glTranslated((guiWidth - manaBarWidth) / 2, 0, 0);
		DrawHelper.drawRect(0, 0, manaBarWidth, manaBarHeight, 0.2, 0.2, 0.2, partialTranslucent1);
		DrawHelper.drawRect(0.5, 0.5, manaBarWidth - 0.5, manaBarHeight - 0.5, 0, 0, 0, partialTranslucent1);
		DrawHelper.drawRect(manaBarWidth / 2 - (manaBarWidth - 2) / 2 * manaRate, 1, manaBarWidth / 2 + (manaBarWidth - 2) / 2 * manaRate, manaBarHeight - 1,
			0.2 + 0.5 * (1 - manaRate), 0, manaRate, partialTranslucent1);
		GL11.glTranslated(-(guiWidth - manaBarWidth) / 2, 0, 0);
		if (positionTop) GL11.glTranslated(0, manaBarHeight, 0);
	    }
	    {
		if (!positionTop) GL11.glTranslated(0, -queuedHeight, 0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glTranslated((guiWidth - queuedWidth) / 2, 0, 0);
		GL11.glColor4d(1, 1, 1, fullTranslucent);
		drawQueuedElement(queuedElementSize, queuedElementSize, queuedElementGapX, queuedElementGapY, queuedElementsPerRow);
		GL11.glTranslated(-(guiWidth - queuedWidth) / 2, 0, 0);
	    }
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glPopMatrix();
	}
    }

    @SideOnly(Side.CLIENT)
    public void drawHotkeyElements(double unitWidth, double unitHeight, double gapX, double gapY)
    {
	mc.renderEngine.bindTexture(Values.elementsTexture);
	List<Element> l = Arrays.asList(Element.Water, Element.Life, Element.Shield, Element.Cold, Element.Lightning, Element.Arcane, Element.Earth, Element.Fire);
	double x = 0;
	double y = 0;
	for (int a = 0; a < l.size(); a++)
	{
	    Element e = l.get(a);
	    DrawHelper.drawElementIcon(null, e, false, x, y, unitWidth, unitHeight, 0.9);
	    x += unitWidth + gapX;
	    if ((a + 1) % 4 == 0)
	    {
		x = 0;
		y += unitHeight + gapY;
	    }
	}

	if (isWizardnessApplicable())
	{
	    x = 0;
	    y = 0;
	    for (int a = 0; a < l.size(); a++)
	    {
		Element e = l.get(a);
		String hotkey = Keyboard.getKeyName(ModKeybinding.elementToKeyMap.get(e).getKeyCode());
		mc.fontRenderer.drawStringWithShadow(hotkey, (int) (x + unitWidth - mc.fontRenderer.getStringWidth(hotkey)), (int) (y + unitHeight - 7), 0xffffffff);
		x += unitWidth + gapX;
		if ((a + 1) % 4 == 0)
		{
		    x = 0;
		    y += unitHeight + gapY;
		}
	    }
	}
    }

    @SideOnly(Side.CLIENT)
    public void drawQueuedElement(double unitWidth, double unitHeight, double gapX, double gapY, int perRow)
    {
	mc.renderEngine.bindTexture(Values.elementsTexture);
	int rows = (int) Math.ceil((double) queuedElements.size() / (double) perRow);

	double x = 0; // : unitWidth * perRow + gapX * (Math.max(0, perRow -
		      // 1));
	double y = 0; // : unitHeight * (rows - 1) + gapY * (Math.max(0, rows -
		      // 2));

	for (int a = 0; a < queuedElements.size(); a++)
	{
	    DrawHelper.drawElementIcon(null, queuedElements.get(a), false, x, y, unitWidth, unitHeight, 0.95);
	    x += unitWidth + gapX;
	    if ((a + 1) % perRow == 0)
	    {
		x = 0; // : unitWidth * perRow + gapX * (Math.max(0, perRow -
		       // 1));
		y += unitHeight + gapY;
	    }
	}
    }
}
