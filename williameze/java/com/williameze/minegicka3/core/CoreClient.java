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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.minegicka3.ClientProxy;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.ModKeybinding;
import com.williameze.minegicka3.bridges.Values;
import com.williameze.minegicka3.main.Element;
import com.williameze.minegicka3.main.objects.ItemStaff;
import com.williameze.minegicka3.main.packets.PacketPlayerMana;
import com.williameze.minegicka3.main.packets.PacketStartSpell;
import com.williameze.minegicka3.main.packets.PacketStopSpell;
import com.williameze.minegicka3.main.spells.Spell;
import com.williameze.minegicka3.main.spells.Spell.CastType;

import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
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

    public int elementsPerRow = 5;
    public Minecraft mc = Minecraft.getMinecraft();
    public List<Element> queuedElements = new ArrayList();
    public List<Entry<Element, long[]>> removingElements = new ArrayList();

    public Spell currentClientCastingSpell;
    public List<Spell> currentWorldSpells = new ArrayList();

    public boolean isWizard()
    {
	return mc != null && mc.thePlayer != null;
    }

    public boolean isWizardnessApplicable()
    {
	return isWizard() && mc.thePlayer.getCurrentEquippedItem() != null
		&& mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemStaff;
    }

    public double getManaRate()
    {
	return PlayersData.clientPlayerData.mana / PlayersData.clientPlayerData.maxMana;
    }

    public void spellTriggerReceived(Spell s, boolean start)
    {
	if (s.dimensionID == mc.theWorld.provider.dimensionId)
	{
	    if (start)
	    {
		if (!currentWorldSpells.contains(s)) currentWorldSpells.add(s);
		s.startSpell();
	    }
	    else
	    {
		s.stopSpell();
		currentWorldSpells.remove(s);
		if (s == currentClientCastingSpell) currentClientCastingSpell = null;
	    }
	}
    }

    public void clientStartUsingStaff(World w, EntityPlayer p, ItemStack is)
    {
	if (p == mc.thePlayer && !queuedElements.isEmpty())
	{
	    if (currentClientCastingSpell == null)
	    {
		CastType ct = CastType.Single;
		if (ModKeybinding.keyArea.isPressed()) ct = CastType.Area;
		Spell s = new Spell(queuedElements, w.provider.dimensionId, p.getUniqueID(), ct, Spell.createAdditionalInfo(is));
		currentClientCastingSpell = s;
		ModBase.packetPipeline.sendToServer(new PacketStartSpell(s));
	    }
	}
    }

    public void clientStopUsingStaff(World w, EntityPlayer p, ItemStack is, int usedTick)
    {
	if (p == mc.thePlayer)
	{
	    if (currentClientCastingSpell != null)
	    {
		ModBase.packetPipeline.sendToServer(new PacketStopSpell(currentClientCastingSpell));
		currentClientCastingSpell = null;
	    }
	    clearQueued();
	}
    }

    public void onClientTick(ClientTickEvent event)
    {
	if (isWizardnessApplicable())
	{
	    for (ModKeybinding mkb : ModKeybinding.elementKeys)
	    {
		if (mkb.isPressed()) playerQueueElement(mkb.element);
	    }
	    if (ModKeybinding.keyClear.isPressed())
	    {
		ModBase.proxy.getCoreClient().clearQueued();
	    }
	}
    }

    public void onClientPlayerTick(PlayerTickEvent event)
    {
	if (event.phase==Phase.END && event.player == mc.thePlayer)
	{
	    updateSpells();
	    if (currentClientCastingSpell == null || !currentWorldSpells.contains(currentClientCastingSpell)
		    || currentClientCastingSpell.toBeStopped) recoverMana();
	}
    }

    public void onClientRenderTick(RenderTickEvent event)
    {
    }

    public void updateSpells()
    {
	List<Spell> toRemove = new ArrayList();
	for (int a = 0; a < currentWorldSpells.size(); a++)
	{
	    Spell s = currentWorldSpells.get(a);
	    if (s.dimensionID != mc.theWorld.provider.dimensionId)
	    {
		toRemove.add(s);
		continue;
	    }
	    s.updateSpell();
	    if (s.toBeStopped) toRemove.add(s);
	}
	currentWorldSpells.removeAll(toRemove);
    }

    public void recoverMana()
    {
	EntityPlayer p = mc.thePlayer;
	PlayerData pd = PlayersData.getPlayerData_static(p);
	pd.recoverMana();
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

    public void clearQueued()
    {
	for (int a = 0; a < queuedElements.size(); a++)
	{
	    replaceElement(a, queuedElements.get(a));
	}
	queuedElements.clear();
    }

    public void replaceElement(int index, Element replacement)
    {
	Element current = queuedElements.get(index);

	removingElements.add(new SimpleEntry(current, new long[] { index, System.currentTimeMillis() }));

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
	    alphaCounterTick += 20 * ((isWizardnessApplicable() && alphaCounterTick < maxACT) ? 1
		    : (!isWizardnessApplicable() && alphaCounterTick > 0) ? -1 : 0);
	    alphaCounterTick = Math.max(0, Math.min(alphaCounterTick, maxACT));
	    double fullTranslucent = (double) alphaCounterTick / (double) maxACT;
	    double partialTranslucent = 0.25F + 0.75F * fullTranslucent;
	    double partialTranslucent1 = 0.4F + 0.6F * fullTranslucent;

	    double i = scaledResolution.getScaledWidth_double();
	    double j = scaledResolution.getScaledHeight_double();

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

	    double manaRate = getManaRate();
	    double manaBarWidth = hotkeyWidth;
	    double manaBarHeight = 5;

	    int queuedElementsPerRow = elementsPerRow;
	    int queuedElementsRows = (int) Math.ceil((double) queuedElements.size() / (double) queuedElementsPerRow);
	    double queuedElementSize = 15;
	    double queuedElementGapX = 2;
	    double queuedElementGapY = 0;
	    double queuedWidth = queuedElementSize * queuedElementsPerRow + queuedElementGapX
		    * (Math.max(0, queuedElementsPerRow - 1));
	    double queuedHeight = queuedElementSize * queuedElementsRows + queuedElementGapY
		    * (Math.max(0, queuedElementsRows - 1));

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
		DrawHelper.drawRect(manaBarWidth / 2 - (manaBarWidth - 2) / 2 * manaRate, 1, manaBarWidth / 2
			+ (manaBarWidth - 2) / 2 * manaRate, manaBarHeight - 1, 0.7, 0.7 * Math.max(0, 0.7F - manaRate),
			0.8 * manaRate, partialTranslucent1);
		GL11.glTranslated(-(guiWidth - manaBarWidth) / 2, 0, 0);
		if (positionTop) GL11.glTranslated(0, manaBarHeight, 0);
	    }
	    {
		if (!positionTop) GL11.glTranslated(0, -queuedHeight, 0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glTranslated((guiWidth - queuedWidth) / 2, 0, 0);
		GL11.glColor4d(1, 1, 1, fullTranslucent);
		drawQueuedElement(queuedElementSize, queuedElementSize, queuedElementGapX, queuedElementGapY,
			queuedElementsPerRow);
		drawRemovingElement(queuedElementSize, queuedElementSize, queuedElementGapX, queuedElementGapY,
			queuedElementsPerRow, positionTop);
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
	List<Element> l = Arrays.asList(Element.Water, Element.Life, Element.Shield, Element.Cold, Element.Lightning,
		Element.Arcane, Element.Earth, Element.Fire);
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
		mc.fontRenderer.drawStringWithShadow(hotkey, (int) (x + unitWidth - mc.fontRenderer.getStringWidth(hotkey)),
			(int) (y + unitHeight - 7), 0xffffffff);
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

    @SideOnly(Side.CLIENT)
    public void drawRemovingElement(double unitWidth, double unitHeight, double gapX, double gapY, int perRow, boolean top)
    {
	long maxElapse = 500;
	List<Entry<Element, long[]>> toRemove = new ArrayList();

	mc.renderEngine.bindTexture(Values.elementsTexture);
	for (int a = 0; a < removingElements.size(); a++)
	{
	    Entry<Element, long[]> entry = removingElements.get(a);
	    Element e = entry.getKey();
	    if (entry.getValue().length >= 2)
	    {
		int index = (int) entry.getValue()[0];
		long elapsed = System.currentTimeMillis() - entry.getValue()[1];
		if (elapsed > maxElapse)
		{
		    toRemove.add(entry);
		    continue;
		}

		double rate = 1D - (double) elapsed / (double) maxElapse;

		int column = index % perRow;
		int row = (index - column) / perRow;
		double x = column * unitWidth + Math.max(0, column * gapX);
		double y = (top ? 1 : -1) * ((row + 1) * unitHeight + Math.max(0, row * gapY));
		if (top)
		{
		    y -= unitHeight;
		}
		else if (!queuedElements.isEmpty())
		{
		    y += unitHeight;
		}
		y -= unitHeight * (1 - rate);
		GL11.glColor4d(1, 1, 1, rate);
		DrawHelper.drawElementIcon(null, e, false, x, y, unitWidth, unitHeight, 0.95 * rate);
		GL11.glColor4d(1, 1, 1, 1);
	    }
	    else
	    {
		toRemove.add(entry);
	    }
	}
    }
}
