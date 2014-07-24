package com.williameze.minegicka3.functional;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.ModKeybinding;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.objects.items.Hat;
import com.williameze.minegicka3.main.objects.items.Staff;
import com.williameze.minegicka3.main.packets.PacketStartSpell;
import com.williameze.minegicka3.main.packets.PacketStopSpell;
import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.magicks.Magick;
import com.williameze.minegicka3.mechanics.spells.Spell;
import com.williameze.minegicka3.mechanics.spells.Spell.CastType;

import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
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
    public int maxQueueElements = 5;
    public Minecraft mc = Minecraft.getMinecraft();
    public List<Element> queuedElements = new ArrayList();
    public List<Entry<Element, long[]>> removingElements = new ArrayList();

    public Spell currentClientCastingSpell;
    public CastType currentClientSpellCastType = CastType.Single;
    public List<Spell> currentWorldSpells = new ArrayList();
    public Magick currentQueuedMagick;

    public boolean recoveringMana = false;
    public boolean selfcasting = false;

    public boolean isWizard()
    {
	return mc != null && mc.thePlayer != null && hasStaffOrHat(mc.thePlayer);
    }

    public boolean isWizardnessApplicable()
    {
	return isWizard() && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof Staff;
    }

    public boolean hasStaffOrHat(EntityPlayer p)
    {
	if (p == null) return false;
	for (int a = 0; a < p.inventory.getSizeInventory(); a++)
	{
	    ItemStack is = p.inventory.getStackInSlot(a);
	    if (is != null && (is.getItem() instanceof Staff || is.getItem() instanceof Hat)) return true;
	}
	return false;
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
		if (!currentWorldSpells.contains(s))
		{
		    currentWorldSpells.add(s);
		    s.startSpell();
		}
	    }
	    else
	    {
		if (currentWorldSpells.contains(s)) s.stopSpell();
		currentWorldSpells.remove(s);
		if (s == currentClientCastingSpell) currentClientCastingSpell = null;
	    }
	}
    }

    public void clientStartUsingStaff(World w, EntityPlayer p, ItemStack is)
    {
	if (p == mc.thePlayer && PlayersData.getPlayerData_static(p).mana >= Values.minManaToCastSpell && !queuedElements.isEmpty())
	{
	    if (currentClientCastingSpell == null)
	    {
		Spell s = new Spell(queuedElements, w.provider.dimensionId, p.getPersistentID(), p.getGameProfile().getName(),
			currentClientSpellCastType, Spell.createAdditionalInfo(p));
		currentClientCastingSpell = s;
		ModBase.packetPipeline.sendToServer(new PacketStartSpell(s));
	    }
	}
    }

    public void clientStopUsingStaff(World w, EntityPlayer p, ItemStack is, int usedTick)
    {
	if (p == mc.thePlayer)
	{
	    if (currentClientCastingSpell != null && currentWorldSpells.contains(currentClientCastingSpell))
	    {
		int index = currentWorldSpells.indexOf(currentClientCastingSpell);
		ModBase.packetPipeline.sendToServer(new PacketStopSpell(currentWorldSpells.get(index)));
	    }
	    currentClientCastingSpell = null;
	    clearQueued();
	}
    }

    public void clientCastMagick()
    {
	if (currentQueuedMagick != null && PlayersData.clientPlayerData.isUnlocked(currentQueuedMagick))
	{
	    World world = ModBase.proxy.getClientWorld();
	    EntityPlayer player = ModBase.proxy.getClientPlayer();
	    currentQueuedMagick.clientSendMagick(world, player.posX, player.posY, player.posZ, player, null);
	    clearQueued();
	}
    }

    public void onClientTick(ClientTickEvent event)
    {
	if (mc != null && mc.thePlayer != null)
	{
	    if (selfcasting && (mc.currentScreen != null || !Mouse.isButtonDown(0)))
	    {
		clientStopUsingStaff(mc.theWorld, mc.thePlayer, mc.thePlayer.getHeldItem(), 1);
		selfcasting = false;
	    }
	    if (currentClientCastingSpell == null && isWizardnessApplicable())
	    {
		for (KeyBinding mkb : ModKeybinding.elementKeys)
		{
		    if (mkb.isPressed() && ModBase.proxy.getClientPlayer().getItemInUse() == null)
		    {
			playerQueueElement(ModKeybinding.keyToElementMap.get(mkb));
		    }
		}
		if (!selfcasting && mc.currentScreen == null && Mouse.isButtonDown(0))
		{
		    selfcasting = true;
		    currentClientSpellCastType = CastType.Self;
		    clientStartUsingStaff(mc.theWorld, mc.thePlayer, mc.thePlayer.getHeldItem());
		    currentClientSpellCastType = CastType.Single;
		}

		if (mc.thePlayer.isSneaking())
		{
		    currentClientSpellCastType = CastType.Area;
		}
		else currentClientSpellCastType = CastType.Single;

		if (ModKeybinding.keyUltility.isPressed())
		{
		    if (!mc.thePlayer.isSneaking()) clientCastMagick();
		    else
		    {
			clearQueued();
		    }
		}
	    }
	}
	if (!Mouse.isButtonDown(0))
	{
	    selfcasting = false;
	}
	ModKeybinding.keyUltility.isPressed();
	for (KeyBinding mkb : ModKeybinding.elementKeys)
	{
	    mkb.isPressed();
	}
    }

    public void onClientPlayerTick(PlayerTickEvent event)
    {
	if (event.phase == Phase.END && event.player == mc.thePlayer)
	{
	    ItemStack is = event.player.getHeldItem();
	    Staff item = Staff.getStaffItem(is);
	    if (item != null)
	    {
		maxQueueElements = 5 + item.getStaffTag(is).getInteger("Add queueable");
	    }
	    else maxQueueElements = 5;

	    if (selfcasting==false && event.player.getItemInUse() == null)
	    {
		currentClientCastingSpell = null;
	    }

	    if (currentClientCastingSpell == null || !currentWorldSpells.contains(currentClientCastingSpell)
		    || currentClientCastingSpell.toBeInvalidated)
	    {
		recoverMana();
		recoveringMana = true;
	    }
	    else
	    {
		recoveringMana = false;
	    }

	    if (queuedElements.size() > maxQueueElements)
	    {
		for (int a = queuedElements.size() - 1; a >= maxQueueElements; a--)
		{
		    replaceElement(a, null);
		}
	    }

	    updateSpells();
	}
    }

    public void onClientRenderTick(RenderTickEvent event)
    {
    }

    public void updateSpells()
    {
	List<Spell> toRemove = new ArrayList();
	for (Spell s : currentWorldSpells)
	{
	    if (s.dimensionID != mc.theWorld.provider.dimensionId)
	    {
		toRemove.add(s);
		continue;
	    }
	    s.updateSpell();
	    if (s.toBeInvalidated)
	    {
		toRemove.add(s);
	    }
	}
	for (Spell s : toRemove)
	{
	    spellTriggerReceived(s, false);
	}
	if (currentClientCastingSpell != null && currentClientCastingSpell.toBeInvalidated) currentClientCastingSpell = null;
    }

    public void recoverMana()
    {
	EntityPlayer p = mc.thePlayer;
	PlayerData pd = PlayersData.getPlayerData_static(p);
	pd.recoverMana();
    }

    public void playerQueueElement(Element e)
    {
	if (!PlayersData.clientPlayerData.isUnlocked(e)) return;
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

	if (queuedElements.size() < maxQueueElements)
	{
	    queuedElements.add(e);
	}
	checkForMatchedMagick();
    }

    public void clearQueued()
    {
	for (int a = 0; a < queuedElements.size(); a++)
	{
	    replaceElement(a, queuedElements.get(a));
	}
	queuedElements.clear();
	checkForMatchedMagick();
    }

    public void replaceElement(int index, Element replacement)
    {
	Element current = queuedElements.get(index);

	removingElements.add(new SimpleEntry(current, new long[] { index, System.currentTimeMillis() }));

	if (replacement != null) queuedElements.set(index, replacement);
	else queuedElements.remove(index);
	checkForMatchedMagick();
    }

    public void checkForMatchedMagick()
    {
	if (queuedElements.isEmpty()) currentQueuedMagick = null;
	else
	{
	    currentQueuedMagick = Magick.getMatchingMagick(queuedElements);
	}
    }

    public int alphaCounterTick = 0;
    public int maxACT = 100;

    @SideOnly(Side.CLIENT)
    public void onRenderGameOverlayTick(RenderGameOverlayEvent event)
    {
	ScaledResolution scaledResolution = event.resolution;
	PlayerData pd = PlayersData.clientPlayerData;

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
	    {// hotkey elements
		if (!positionTop) GL11.glTranslated(0, -hotkeyHeight, 0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glTranslated((guiWidth - hotkeyWidth) / 2, 0, 0);
		GL11.glColor4d(1, 1, 1, partialTranslucent);
		drawHotkeyElements(hotkeyElementSize, hotkeyElementSize, hotkeyElementGapX, hotkeyElementGapY);
		GL11.glTranslated(-(guiWidth - hotkeyWidth) / 2, 0, 0);
		if (positionTop) GL11.glTranslated(0, hotkeyHeight, 0);
	    }
	    {// mana bar
		if (!positionTop) GL11.glTranslated(0, -manaBarHeight, 0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glTranslated((guiWidth - manaBarWidth) / 2, 0, 0);
		DrawHelper.drawRect(0, 0, manaBarWidth, manaBarHeight, 0.2, 0.2, 0.2, partialTranslucent1);
		DrawHelper.drawRect(0.5, 0.5, manaBarWidth - 0.5, manaBarHeight - 0.5, 0, 0, 0, partialTranslucent1);
		if (pd.mana < Values.minManaToCastSpell && recoveringMana)
		{
		    DrawHelper.drawRect(manaBarWidth / 2 - (manaBarWidth - 2) / 2 * manaRate, 1,
			    manaBarWidth / 2 + (manaBarWidth - 2) / 2 * manaRate, manaBarHeight - 1, 0.8, 0.8, 0.8, partialTranslucent1);
		}
		else
		{
		    DrawHelper.drawRect(manaBarWidth / 2 - (manaBarWidth - 2) / 2 * manaRate, 1,
			    manaBarWidth / 2 + (manaBarWidth - 2) / 2 * manaRate, manaBarHeight - 1, 0.7, 0.7 * Math.max(0, 0.7F - manaRate),
			    0.8 * manaRate, partialTranslucent1);
		}
		if (isWizardnessApplicable())
		{
		    int manaInt = (int) Math.ceil(pd.maxMana * manaRate);
		    int maxManaInt = (int) Math.ceil(pd.maxMana);
		    String manaString = manaInt + "/" + maxManaInt;
		    double transX = guiWidth / 2 - mc.fontRenderer.getStringWidth(manaString) / 2 * 0.6;
		    GL11.glTranslated(transX, 0, 0);
		    GL11.glScaled(0.6, 0.6, 0.6);
		    mc.fontRenderer.drawStringWithShadow(manaString, 0, 0, 0xffffff);
		    GL11.glScaled(1 / 0.6D, 1 / 0.6D, 1 / 0.6D);
		    GL11.glTranslated(-transX, 0, 0);
		}
		GL11.glTranslated(-(guiWidth - manaBarWidth) / 2, 0, 0);
		if (positionTop) GL11.glTranslated(0, manaBarHeight, 0);
	    }
	    {// queued elements and recently removed elements
		if (!positionTop) GL11.glTranslated(0, -queuedHeight, 0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glTranslated((guiWidth - queuedWidth) / 2, 0, 0);
		GL11.glColor4d(1, 1, 1, fullTranslucent);
		drawQueuedElement(queuedElementSize, queuedElementSize, queuedElementGapX, queuedElementGapY, queuedElementsPerRow);
		drawRemovingElement(queuedElementSize, queuedElementSize, queuedElementGapX, queuedElementGapY, queuedElementsPerRow, positionTop);
		GL11.glTranslated(-(guiWidth - queuedWidth) / 2, 0, 0);
		if (!positionTop) GL11.glTranslated(0, queuedHeight, 0);
	    }
	    {// queued magick name
		if (currentQueuedMagick != null && isWizardnessApplicable() && pd.isUnlocked(currentQueuedMagick))
		{
		    String name = currentQueuedMagick.getDisplayName();
		    int nameLength = mc.fontRenderer.getStringWidth(name);
		    double scale = nameLength > guiWidth ? guiWidth / (double) nameLength : 1;
		    if (positionTop) GL11.glTranslated(0, 2 + queuedHeight, 0);
		    else GL11.glTranslated(0, -queuedHeight - 8 * scale, 0);
		    GL11.glEnable(GL11.GL_BLEND);
		    GL11.glTranslated((guiWidth - nameLength * scale) / 2, 0, 0);
		    GL11.glScaled(scale, scale, scale);
		    mc.fontRenderer.drawString(name, 0, 0, 0xffffff);
		    GL11.glScaled(1D / scale, 1D / scale, 1D / scale);
		    GL11.glTranslated((guiWidth - nameLength * scale) / 2, 0, 0);
		    if (positionTop) GL11.glTranslated(0, -2 - queuedHeight, 0);
		    else GL11.glTranslated(0, queuedHeight + 8 * scale, 0);
		}
	    }
	    GL11.glColor4d(1, 1, 1, 1);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glPopMatrix();
	}
    }

    @SideOnly(Side.CLIENT)
    public void drawHotkeyElements(double unitWidth, double unitHeight, double gapX, double gapY)
    {
	mc.renderEngine.bindTexture(Values.elementsTexture);
	List<Element> l = Arrays.asList(Element.Water, Element.Life, Element.Shield, Element.Cold, Element.Lightning, Element.Arcane, Element.Earth,
		Element.Fire);
	double x = 0;
	double y = 0;
	for (int a = 0; a < l.size(); a++)
	{
	    Element e = l.get(a);
	    DrawHelper.drawElementIcon(null, e, !PlayersData.clientPlayerData.isUnlocked(e), x, y, unitWidth, unitHeight, 0.9);
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
		double y = (top ? 1 : -1) * ((-row + 1) * unitHeight + Math.max(0, row * gapY));
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
	removingElements.removeAll(toRemove);
    }
}
