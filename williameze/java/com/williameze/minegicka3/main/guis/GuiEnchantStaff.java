package com.williameze.minegicka3.main.guis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.williameze.api.gui.IGuiHasScrollPanel;
import com.williameze.api.gui.Panel;
import com.williameze.api.gui.PanelScrollHorizontal;
import com.williameze.api.gui.PanelScrollVertical;
import com.williameze.api.gui.ScrollIsWithText;
import com.williameze.api.gui.ScrollItemStack;
import com.williameze.api.gui.ScrollObject;
import com.williameze.api.gui.ScrollParagraph;
import com.williameze.api.lib.DrawHelper;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.main.Values.ResourceLocationCustom;
import com.williameze.minegicka3.main.objects.items.Staff;
import com.williameze.minegicka3.main.packets.PacketEnchantStaff;
import com.williameze.minegicka3.mechanics.ClickCraft;
import com.williameze.minegicka3.mechanics.Enchant;
import com.williameze.minegicka3.mechanics.EnchantEntry;

public class GuiEnchantStaff extends GuiScreen implements IGuiHasScrollPanel
{
    public static ResourceLocation guiImg = new ResourceLocationCustom("drawables/guiEnchantStaff.png");
    static double guiImgW = 321.75;
    static double guiImgH = 202.5;

    public EntityPlayer user;
    public PanelScrollVertical staffPanel;
    public PanelScrollVertical itemsPanel;
    public PanelScrollVertical infoPanel;
    public PanelScrollHorizontal inputPanel;

    public ItemStack hoveringItemStack;
    public int maxInput = 50;
    public List<ItemStack> checkedThrough = new ArrayList();
    public Map<Object, EnchantEntry> originalEnchantList = new HashMap();

    public EnchantEntry finalEntry = null;
    public boolean justEnchanted = false;

    public GuiEnchantStaff(EntityPlayer p)
    {
	user = p;
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
	originalEnchantList.clear();
	originalEnchantList.putAll(Enchant.enchantList);
	panels.clear();
	checkedThrough.clear();
	staffPanel = new PanelScrollVertical(this, mc.displayWidth / 2D, mc.displayHeight / 2D, -150, -66, 30, 140, 3).setSeparatorColor(
		new Color(255, 255, 255)).setSelectedColor(new Color(200, 50, 200, 150));
	itemsPanel = new PanelScrollVertical(this, mc.displayWidth / 2D, mc.displayHeight / 2D, -110, -66, 55, 140, 3).setSeparatorColor(new Color(
		255, 255, 255));
	infoPanel = new PanelScrollVertical(this, mc.displayWidth / 2D, mc.displayHeight / 2D, -45, -66, 195, 100, 3).setSeparatorColor(new Color(
		255, 255, 255));
	inputPanel = new PanelScrollHorizontal(this, mc.displayWidth / 2D, mc.displayHeight / 2D, -45, 36, 195, 17, 2).setSeparatorColor(new Color(
		255, 255, 255));
	for (int a = 0; a < user.inventory.getSizeInventory(); a++)
	{
	    ItemStack is = user.inventory.getStackInSlot(a);
	    if (is != null && is.getItem() != null)
	    {
		if (is.getItem() instanceof Staff)
		{
		    ScrollItemStack scrollIS = new ScrollItemStack(staffPanel, is, 20);
		    scrollIS.yMarginBG = 1;
		    scrollIS.xMarginBG = 4;
		    scrollIS.invIndex = a;
		    staffPanel.addObject(scrollIS);
		}
		if (!hasItemBeenChecked(is))
		{
		    Entry<Object, EnchantEntry> mapEntry = Enchant.getEnchantEntry(is);
		    if (mapEntry != null)
		    {
			EnchantEntry entry = mapEntry.getValue();
			ItemStack is1 = is.copy();
			is1.stackSize = 1;
			ScrollRecipeIS scrollIS = new ScrollRecipeIS(itemsPanel, is1, 20, itemsPanel.panelWidth, 16, 10);
			scrollIS.quantityHave = scrollIS.quantityNeed = ClickCraft.getAmountUserHas(user, is1);
			itemsPanel.addObject(scrollIS);
			originalEnchantList.remove(mapEntry.getKey());
		    }
		    checkedThrough.add(is);
		}
	    }
	}
	for (EnchantEntry entry : originalEnchantList.values())
	{
	    Object o = entry.enchantee;
	    ItemStack is = null;
	    if (o instanceof ItemStack) is = (ItemStack) o;
	    else if (o instanceof Item) is = new ItemStack((Item) o);
	    else if (o instanceof Block) is = new ItemStack((Block) o);
	    if (is != null)
	    {
		is.stackSize = 1;
		ScrollRecipeIS scrollIS = new ScrollRecipeIS(itemsPanel, is, 20, itemsPanel.panelWidth, 16, 10);
		scrollIS.color2 = 0xc5c5c5;
		scrollIS.quantityHave = scrollIS.quantityNeed = 0;
		itemsPanel.addObject(scrollIS);
	    }
	}

	panels.add(staffPanel);
	panels.add(itemsPanel);
	panels.add(infoPanel);
	panels.add(inputPanel);

	buttonList.clear();
	buttonList.add(new GuiButton(0, width / 2 + 102, height / 2 + 56, 50, 20, "Enchant"));
	buttonList.add(new GuiButton(1, width / 2 - 46, height / 2 + 56, 50, 20, "Clear"));
    }

    public boolean hasItemBeenChecked(ItemStack is)
    {
	for (ItemStack is1 : checkedThrough)
	{
	    if (is1 != null && is != null && is.isItemEqual(is1))
	    {
		return true;
	    }
	}
	return false;
    }

    @Override
    public void panelObjClickedFeedback(ScrollObject obj)
    {
	if (itemsPanel.isObjectOfPanel(obj))
	{
	    if (inputPanel.objects.size() < maxInput && obj instanceof ScrollRecipeIS && ((ScrollRecipeIS) obj).quantityHave > 0)
	    {
		((ScrollRecipeIS) obj).quantityHave--;
		ItemStack is = ((ScrollItemStack) obj).is.copy();
		is.stackSize = 1;
		ScrollItemStack scrollIS = new ScrollItemStack(inputPanel, is, 20);
		scrollIS.invIndex = itemsPanel.objects.indexOf(obj);
		inputPanel.addObject(scrollIS);
	    }
	    itemsPanel.selectedIndex = -1;
	}
	if (inputPanel.isObjectOfPanel(obj) && obj instanceof ScrollItemStack)
	{
	    ((ScrollRecipeIS) itemsPanel.objects.get(((ScrollItemStack) obj).invIndex)).quantityHave++;
	    inputPanel.objects.remove(obj);
	    inputPanel.listUpdated();
	    inputPanel.selectedIndex = -1;
	}
    }

    @Override
    public void panelObjHoverFeedback(ScrollObject obj)
    {
	if (obj instanceof ScrollItemStack) hoveringItemStack = ((ScrollItemStack) obj).is;
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
	if (staffPanel.selectedIndex < 0 || inputPanel.objects.isEmpty())
	{
	    ((GuiButton) buttonList.get(0)).enabled = false;
	}
	else
	{
	    ((GuiButton) buttonList.get(0)).enabled = true;
	}

	infoPanel.clearObjects();
	if (staffPanel.selectedIndex > -1)
	{
	    ScrollItemStack scrollIS = (ScrollItemStack) staffPanel.objects.get(staffPanel.selectedIndex);
	    ScrollIsWithText scrollIsWithText = new ScrollIsWithText(infoPanel, scrollIS.is, 40, infoPanel.panelWidth, 32);
	    scrollIsWithText.textImageSpace = 6;
	    scrollIsWithText.leftMargin = 18;
	    scrollIsWithText.fontSize = 12;
	    boolean enchanted = ((Staff) scrollIS.is.getItem()).getEnchanted(scrollIS.is);
	    scrollIsWithText.text = EnumChatFormatting.YELLOW + "" + EnumChatFormatting.ITALIC + (enchanted ? "Enchanted" : "Unenchanted");
	    scrollIsWithText.text = scrollIS.is.getDisplayName();
	    infoPanel.addObject(scrollIsWithText);
	}
	if (!inputPanel.objects.isEmpty())
	{
	    List<EnchantEntry> l1 = new ArrayList();
	    for (ScrollObject scroll : inputPanel.objects)
	    {
		if (scroll instanceof ScrollItemStack && ((ScrollItemStack) scroll).is != null)
		{
		    EnchantEntry entry = Enchant.getEnchantEntry(((ScrollItemStack) scroll).is).getValue();
		    l1.add(entry);
		}
	    }
	    finalEntry = Enchant.finalizeEntryChain(l1);
	    List<String> positive = finalEntry.getEffectStrings(true);
	    List<String> negative = finalEntry.getEffectStrings(false);
	    ScrollParagraph positivePara = new ScrollParagraph(infoPanel, 6, 6, positive);
	    ScrollParagraph negativePara = new ScrollParagraph(infoPanel, 6, 6, negative);
	    positivePara.stringColor = 0x00ff00;
	    negativePara.stringColor = 0xff0000;
	    infoPanel.addObject(positivePara);
	    infoPanel.addObject(negativePara);
	}
	else
	{
	    finalEntry = null;
	}
    }

    @Override
    protected void actionPerformed(GuiButton b)
    {
	super.actionPerformed(b);
	if (b.id == 0 && finalEntry != null)
	{
	    ItemStack is = ((ScrollItemStack) staffPanel.objects.get(staffPanel.selectedIndex)).is;
	    if (is != null)
	    {
		List<ItemStack> items = new ArrayList();

		for (ScrollObject scroll : inputPanel.objects)
		{
		    if (scroll instanceof ScrollItemStack && ((ScrollItemStack) scroll).is != null)
		    {
			items.add(((ScrollItemStack) scroll).is);
		    }
		}
		Enchant.applyStaffEnchant(is, finalEntry, user.getGameProfile().getName());
		Enchant.playerConsume(user, items);
		ModBase.packetPipeline.sendToServer(new PacketEnchantStaff(user,
			((ScrollItemStack) staffPanel.objects.get(staffPanel.selectedIndex)).invIndex, items));
		initGui();
	    }
	    justEnchanted = true;
	}
	if (b.id == 1)
	{
	    initGui();
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

	String s = EnumChatFormatting.BOLD + "Staff Enchanting";
	fontRendererObj.drawStringWithShadow(s, width / 2 - fontRendererObj.getStringWidth(s) / 2, height / 2 - 93, 0xffffff);
	fontRendererObj.drawString(StatCollector.translateToLocal("mgk3.string.Staves"), width / 2 - 150, height / 2 - 76, 0x000000);
	fontRendererObj.drawString(StatCollector.translateToLocal("mgk3.string.Materials"), width / 2 - 110, height / 2 - 76, 0x000000);
	fontRendererObj.drawString(StatCollector.translateToLocal("mgk3.string.Enchantment"), width / 2 - 45, height / 2 - 76, 0x000000);
	String count = inputPanel.objects.size() + "/" + maxInput;
	fontRendererObj.drawStringWithShadow(count, width / 2 + 55 - mc.fontRenderer.getStringWidth(count) / 2, height / 2 + 63, 0xffffff);

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
