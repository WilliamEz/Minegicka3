package com.williameze.minegicka3.main.guis;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.williameze.api.gui.IGuiHasScrollPanel;
import com.williameze.api.gui.Panel;
import com.williameze.api.gui.PanelScrollVertical;
import com.williameze.api.gui.ScrollItemStack;
import com.williameze.api.gui.ScrollObject;
import com.williameze.api.gui.ScrollParagraph;
import com.williameze.api.gui.ScrollString;
import com.williameze.api.lib.DrawHelper;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.functional.PlayerData;
import com.williameze.minegicka3.functional.PlayersData;
import com.williameze.minegicka3.main.Values.ResourceLocationCustom;
import com.williameze.minegicka3.main.packets.PacketClientUnlockMagick;
import com.williameze.minegicka3.mechanics.ClickCraft;
import com.williameze.minegicka3.mechanics.magicks.Magick;

public class GuiMagickPedia extends GuiScreen implements IGuiHasScrollPanel
{
    public static ResourceLocation guiImg = new ResourceLocationCustom("drawables/guiMagickPedia.png");
    static double guiImgW = 321.75;
    static double guiImgH = 202.5;

    public EntityPlayer user;
    public PlayerData playerData;

    public PanelScrollVertical magicksPanel;
    public ScrollMagick selectedScrollMagick;
    public PanelScrollVertical infoPanel;
    public ItemStack hoveringItemStack;
    public GuiButton unlockButton;

    public GuiMagickPedia(EntityPlayer p)
    {
	user = p;
	playerData = PlayersData.getPlayerData_static(p);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
	return false;
    }

    @Override
    public void initGui()
    {
	super.initGui();
	panels.clear();
	magicksPanel = new PanelScrollVertical(this, mc.displayWidth / 2D, mc.displayHeight / 2D, -150, -66, 100, 140, 3).setSeparatorColor(
		new Color(255, 255, 255)).setSelectedColor(new Color(0, 100, 255, 150));
	for (Magick magick : Magick.magicks.values())
	{
	    ScrollMagick acat = new ScrollMagick(magicksPanel, playerData, magick, 20);
	    acat.yMarginBG = 1;
	    acat.xMarginBG = 4;
	    acat.xMarginString = 8;
	    acat.yCentered = true;
	    acat.scaleDownToFit = true;
	    magicksPanel.addObject(acat);
	}
	infoPanel = new PanelScrollVertical(this, mc.displayWidth / 2D, mc.displayHeight / 2D, -45, -66, 195, 140, 3).setSeparatorColor(new Color(
		255, 255, 255));

	panels.add(magicksPanel);
	panels.add(infoPanel);

	buttonList.clear();

	buttonList
		.add(unlockButton = new GuiButton(0, width / 2 + 91, height / 2 + 57, 60, 20, StatCollector.translateToLocal("mgk3.string.Unlock")));

	if (magicksPanel != null && magicksPanel.objects != null && !magicksPanel.objects.isEmpty())
	{
	    magicksPanel.objectClickedFeedback(magicksPanel.objects.get(0));
	}
    }

    @Override
    public void panelObjClickedFeedback(ScrollObject obj)
    {
	if (magicksPanel.isObjectOfPanel(obj) && obj instanceof ScrollMagick)
	{
	    selectedScrollMagick = (ScrollMagick) obj;
	    Magick selectedMagick = selectedScrollMagick.magick;
	    if (selectedScrollMagick != null && !selectedScrollMagick.unlocked())
	    {
		infoPanel.panelHeight = 120;
	    }
	    else infoPanel.panelHeight = 140;
	    infoPanel.clearObjects();

	    ScrollString magickName = new ScrollString(infoPanel, (selectedScrollMagick.unlocked() ? "" : EnumChatFormatting.OBFUSCATED) + ""
		    + EnumChatFormatting.BOLD + selectedMagick.getDisplayName(), 22);
	    magickName.xCentered = true;
	    magickName.yCentered = true;
	    magickName.xMarginString = 8;
	    magickName.fontSize = 12;
	    magickName.stringColor = selectedScrollMagick.unlocked() ? 0xffffff : 0x404040;
	    magickName.hasShadow = selectedScrollMagick.unlocked();
	    infoPanel.addObject(magickName);

	    if (selectedScrollMagick.unlocked())
	    {
		ScrollElements combination = new ScrollElements(infoPanel, 28, selectedMagick.getCombination());
		combination.elementSize = 20;
		combination.playerData = playerData;
		combination.checkElementsUnlocked = true;
		infoPanel.addObject(combination);

		ScrollString manaCost = new ScrollString(infoPanel, "Base mana cost: " + selectedMagick.getBaseManaCost(), 20);
		manaCost.yMarginBG = 1;
		manaCost.xMarginBG = 4;
		manaCost.xMarginString = 8;
		manaCost.yCentered = true;
		manaCost.scaleDownToFit = true;
		infoPanel.addObject(manaCost);

		List<String> descriptions = selectedMagick.getDescription();
		for (String s : descriptions)
		{
		    ScrollParagraph description = new ScrollParagraph(infoPanel, 4, 8, Arrays.asList(s));
		    infoPanel.addObject(description);
		}
	    }
	    else
	    {
		List<Entry<ItemStack, Integer>> recipe = ClickCraft.combineDuplicates(selectedMagick.getUnlockMaterials());
		if (recipe != null)
		{
		    for (Entry<ItemStack, Integer> e : recipe)
		    {
			ItemStack displayIS = e.getKey().copy();
			displayIS.stackSize = 1;
			ScrollRecipeIS scrl = new ScrollRecipeIS(infoPanel, displayIS, 20, infoPanel.panelWidth, 16);
			scrl.yMarginBG = 1;
			scrl.xMarginBG = 4;
			scrl.quantityNeed = e.getValue();
			infoPanel.addObject(scrl);
		    }
		}
	    }
	}
    }

    @Override
    public void panelObjHoverFeedback(ScrollObject obj)
    {
	if (obj instanceof ScrollItemStack)
	{
	    hoveringItemStack = ((ScrollItemStack) obj).is;
	}
    }

    @Override
    public void updateScreen()
    {
	super.updateScreen();
	hoveringItemStack = null;
	for (Panel p : panels)
	{
	    p.onUpdate();
	}
	boolean canPressUnlock = false;
	if (user != null && selectedScrollMagick != null)
	{
	    if (infoPanel != null && infoPanel.objects != null)
	    {
		for (int a = 0; a < infoPanel.objects.size(); a++)
		{
		    if (infoPanel.objects.get(a) instanceof ScrollRecipeIS)
		    {
			ScrollRecipeIS aRecipeIS = (ScrollRecipeIS) infoPanel.objects.get(a);
			aRecipeIS.quantityHave = ClickCraft.getAmountUserHas(user, aRecipeIS.is);
			int i = (int) Math.floor((double) aRecipeIS.quantityHave / (double) aRecipeIS.quantityNeed);
			canPressUnlock = i >= 1;
		    }
		}
	    }
	}
	if (selectedScrollMagick != null && selectedScrollMagick.unlocked()) canPressUnlock = false;
	if (unlockButton != null)
	{
	    unlockButton.enabled = canPressUnlock;
	    unlockButton.visible = selectedScrollMagick != null && !selectedScrollMagick.unlocked();
	}
    }

    @Override
    protected void actionPerformed(GuiButton b)
    {
	super.actionPerformed(b);
	if (b == unlockButton && selectedScrollMagick != null)
	{
	    Magick m = selectedScrollMagick.magick;
	    ModBase.packetPipeline.sendToServer(new PacketClientUnlockMagick(playerData, m));
	    playerData.unlock(m);
	    panelObjClickedFeedback(selectedScrollMagick);
	}
    }

    @Override
    public void drawScreen(int mx, int my, float partial)
    {
	ScaledResolution scl = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
	GL11.glPushMatrix();
	GL11.glScaled(1 / (double) scl.getScaleFactor(), 1 / (double) scl.getScaleFactor(), 1 / (double) scl.getScaleFactor());

	drawGuiBackground();
	for (Panel p : panels)
	{
	    p.drawPanel();
	}

	GL11.glPopMatrix();

	String s = EnumChatFormatting.BOLD + "Magickpedia";
	fontRendererObj.drawStringWithShadow(s, width / 2 - fontRendererObj.getStringWidth(s) / 2, height / 2 - 93, 0xffffff);
	fontRendererObj.drawString(StatCollector.translateToLocal("mgk3.string.Magicks"), width / 2 - 145, height / 2 - 76, 0x000000);
	fontRendererObj.drawString(StatCollector.translateToLocal("mgk3.string.Info"), width / 2 - 40, height / 2 - 76, 0x000000);
	super.drawScreen(mx, my, partial);

	if (hoveringItemStack != null)
	{
	    GL11.glPushMatrix();
	    GL11.glTranslated(0, 0, 100);
	    renderToolTip(hoveringItemStack, mx, my);
	    GL11.glPopMatrix();
	}
    }

    public void drawGuiBackground()
    {
	ScaledResolution scl = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
	double guiScaleW = guiImgW * scl.getScaleFactor();
	double guiScaleH = guiImgH * scl.getScaleFactor();
	mc.renderEngine.bindTexture(guiImg);
	DrawHelper.blurTexture(true);
	Tessellator tess = Tessellator.instance;
	tess.startDrawingQuads();
	DrawHelper.tessAddQuad(tess, mc.displayWidth / 2 - guiScaleW / 2, mc.displayHeight / 2 - guiScaleH / 2, mc.displayWidth / 2 + guiScaleW / 2,
		mc.displayHeight / 2 + guiScaleH / 2, 0, 0, 1, 1);
	tess.draw();
	DrawHelper.blurTexture(false);
    }

    @Override
    public boolean drawPanelBackground(Panel panel)
    {
	if (panels.contains(panel))
	{
	    DrawHelper.drawRect(-1, -1, panel.panelWidth + 1, panel.panelHeight + 1, 1, 1, 1, 1);
	    DrawHelper.drawRect(-1, -1, panel.panelWidth + 0, panel.panelHeight + 0, 0.1, 0.1, 0.1, 1);
	    DrawHelper.drawRect(0, 0, panel.panelWidth, panel.panelHeight, 0.55, 0.55, 0.55, 1);
	    return true;
	}
	return false;
    }
}
