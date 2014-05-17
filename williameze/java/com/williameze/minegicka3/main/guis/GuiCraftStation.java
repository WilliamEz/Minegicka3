package com.williameze.minegicka3.main.guis;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.williameze.api.gui.IGuiHasScrollPanel;
import com.williameze.api.gui.PanelScrollList;
import com.williameze.api.gui.ScrollItemStack;
import com.williameze.api.gui.ScrollObject;
import com.williameze.api.gui.ScrollString;
import com.williameze.api.lib.DrawHelper;
import com.williameze.minegicka3.main.ClickCraft;
import com.williameze.minegicka3.main.Values.ResourceLocationCustom;

public class GuiCraftStation extends GuiScreen implements IGuiHasScrollPanel
{
    public static ResourceLocation guiImg = new ResourceLocationCustom("drawables/guiCraftStation.png");
    static double guiImgW = 321.75;
    static double guiImgH = 165;

    public EntityPlayer user;
    public PanelScrollList catPanel;
    public PanelScrollList itemsPanel;
    public PanelScrollList recipePanel;
    public ItemStack hoveringOver;
    public ItemStack selected;
    public int craftable;
    public GuiTextField craftAmountTextField;

    public GuiCraftStation(EntityPlayer p)
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
	panels.clear();
	catPanel = new PanelScrollList(this, mc.displayWidth / 2D, mc.displayHeight / 2D, -150, -66, 60, 140, 3).setSeparatorColor(
		new Color(255, 255, 255)).setSelectedColor(new Color(0, 220, 0, 150));
	for (String cat : ClickCraft.categorizedClickCraftRecipes.keySet())
	{
	    ScrollString acat = new ScrollString(catPanel, cat, 20);
	    acat.yMarginBG = 1;
	    acat.xMarginBG = 4;
	    acat.xMarginString = 8;
	    acat.yCentered = true;
	    acat.scaleDownToFit = true;
	    catPanel.addObject(acat);
	}
	itemsPanel = new PanelScrollList(this, mc.displayWidth / 2D, mc.displayHeight / 2D, -80, -66, 30, 140, 3).setSeparatorColor(
		new Color(255, 255, 255)).setSelectedColor(new Color(0, 220, 0, 150));
	recipePanel = new PanelScrollList(this, mc.displayWidth / 2D, mc.displayHeight / 2D, -40, -66, 120, 140, 3)
		.setSeparatorColor(new Color(255, 255, 255));

	panels.add(catPanel);
	panels.add(itemsPanel);
	panels.add(recipePanel);

	craftAmountTextField = new GuiTextField(fontRendererObj, width / 2 + 110, height / 2 + 25, 40, 20);
	craftAmountTextField.setMaxStringLength(4);
	craftAmountTextField.setText("0");

	buttonList.clear();
	buttonList.add(new GuiButton(0, width / 2 + 85, height / 2 - 45, 70, 20, StatCollector.translateToLocal("mgk3.string.Craft")));
	buttonList
		.add(new GuiButton(1, width / 2 + 85, height / 2 + 50, 70, 20, StatCollector.translateToLocal("mgk3.string.Craft Batch")));
    }

    @Override
    public void panelObjClickedFeedback(ScrollObject obj)
    {
	if (catPanel.isObjectOfPanel(obj))
	{
	    setItemsPanelCategory(obj);
	}
	if (itemsPanel.isObjectOfPanel(obj))
	{
	    setRecipePanelRecipe(obj);
	}
    }

    @Override
    public void panelObjHoverFeedback(ScrollObject obj)
    {
	if (obj instanceof ScrollItemStack) hoveringOver = ((ScrollItemStack) obj).is;
    }

    public void setItemsPanelCategory(ScrollObject obj)
    {
	if (obj instanceof ScrollString)
	{
	    selected = null;
	    recipePanel.clearObjects();
	    itemsPanel.clearObjects();
	    String s = ((ScrollString) obj).display;
	    Map<ItemStack, List<Entry<ItemStack, Integer>>> map = ClickCraft.categorizedClickCraftRecipes.get(s);
	    if (map != null)
	    {
		for (ItemStack is : map.keySet())
		{
		    ScrollObject scrl = new ScrollItemStack(itemsPanel, is, 20);
		    scrl.yMarginBG = 1;
		    scrl.xMarginBG = 4;
		    itemsPanel.addObject(scrl);
		}
	    }
	}
    }

    public void setRecipePanelRecipe(ScrollObject obj)
    {
	if (obj instanceof ScrollItemStack)
	{
	    selected = ((ScrollItemStack) obj).is;
	    recipePanel.clearObjects();
	    List<Entry<ItemStack, Integer>> recipe = ClickCraft.getRecipe(selected);
	    if (recipe != null)
	    {
		for (Entry<ItemStack, Integer> e : recipe)
		{
		    ItemStack displayIS = e.getKey().copy();
		    displayIS.stackSize = 1;
		    ScrollClickCraftRecipeItemStack scrl = new ScrollClickCraftRecipeItemStack(recipePanel, displayIS, 20);
		    scrl.yMarginBG = 1;
		    scrl.xMarginBG = 4;
		    scrl.quantityNeed = e.getValue();
		    recipePanel.addObject(scrl);
		}
	    }
	}
    }

    @Override
    public void updateScreen()
    {
	super.updateScreen();
	hoveringOver = null;
	for (PanelScrollList p : panels)
	{
	    p.onUpdate();
	}
	craftable = -1;
	if (user != null)
	{
	    if (recipePanel != null && recipePanel.objects != null)
	    {
		for (int a = 0; a < recipePanel.objects.size(); a++)
		{
		    if (recipePanel.objects.get(a) instanceof ScrollClickCraftRecipeItemStack)
		    {
			ScrollClickCraftRecipeItemStack scrlis = (ScrollClickCraftRecipeItemStack) recipePanel.objects.get(a);
			scrlis.quantityHave = getAmountUserHas(user, scrlis.is);
			int i = (int) Math.floor((double) scrlis.quantityHave / (double) scrlis.quantityNeed);
			craftable = craftable == -1 ? i : Math.min(craftable, i);
		    }
		}
	    }
	}
	craftable = Math.max(craftable, 0);
	String s = craftAmountTextField.getText();
	if (s == null || s == "" || s.length() < 1) s = "0";
	int i = Integer.valueOf(s);
	i = Math.min(craftable, i);
	craftAmountTextField.setText(String.valueOf(i));
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
	super.mouseClicked(par1, par2, par3);
	craftAmountTextField.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2)
    {
	super.keyTyped(par1, par2);
	if (Character.isDigit(par1) || par2 == Keyboard.KEY_BACK)
	{
	    craftAmountTextField.textboxKeyTyped(par1, par2);
	}
    }

    @Override
    protected void actionPerformed(GuiButton b)
    {
	super.actionPerformed(b);
	if (b.id == 0)
	{
	    craft(1);
	}
	if (b.id == 1)
	{
	    craft(Integer.parseInt(craftAmountTextField.getText()));
	}
    }

    public void craft(int amount)
    {
	if (craftable > 0 && amount > 0 && selected != null)
	{
	    int toCraft = Math.min(amount, craftable);
	    ClickCraft.clientPlayerQueueCraft(user, selected, toCraft);
	}
    }

    public int getAmountUserHas(EntityPlayer p, ItemStack hasIS)
    {
	if (hasIS == null) return 0;
	int count = 0;
	for (int a = 0; a < p.inventory.getSizeInventory(); a++)
	{
	    ItemStack is = p.inventory.getStackInSlot(a);
	    if (is != null && ItemStack.areItemStackTagsEqual(is, hasIS) && is.isItemEqual(hasIS))
	    {
		count += is.stackSize;
	    }
	}
	return count;
    }

    @Override
    public void drawScreen(int mx, int my, float partial)
    {
	ScaledResolution scl = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	GL11.glPushMatrix();
	GL11.glScaled(1 / (double) scl.getScaleFactor(), 1 / (double) scl.getScaleFactor(), 1 / (double) scl.getScaleFactor());

	drawGuiBackground();
	for (PanelScrollList p : panels)
	{
	    p.drawPanel();
	}

	GL11.glPopMatrix();

	fontRendererObj.drawString(StatCollector.translateToLocal("mgk3.string.Categories"), width / 2 - 150, height / 2 - 76, 0x000000);
	fontRendererObj.drawString(StatCollector.translateToLocal("mgk3.string.Craft"), width / 2 - 80, height / 2 - 76, 0x000000);
	fontRendererObj.drawString(StatCollector.translateToLocal("mgk3.string.Recipe"), width / 2 - 40, height / 2 - 76, 0x000000);

	String crftbl = StatCollector.translateToLocal("mgk3.string.Craftable");
	String crftblv = "x" + String.valueOf(craftable);
	fontRendererObj.drawStringWithShadow(crftbl, width / 2 + 150 - fontRendererObj.getStringWidth(crftbl), height / 2 - 66, 0xffffff);
	fontRendererObj.drawStringWithShadow(crftblv, width / 2 + 150 - fontRendererObj.getStringWidth(crftblv), height / 2 - 56, 0xffffff);
	String crft = StatCollector.translateToLocal("mgk3.string.Crafting");
	fontRendererObj.drawStringWithShadow(crft, width / 2 + 150 - fontRendererObj.getStringWidth(crft), height / 2 + 12, 0xffffff);
	fontRendererObj.drawStringWithShadow("x", width / 2 + 102, height / 2 + 30, 0xffffff);
	craftAmountTextField.drawTextBox();

	super.drawScreen(mx, my, partial);

	if (hoveringOver != null)
	{
	    GL11.glPushMatrix();
	    GL11.glTranslated(0, 0, 100);
	    renderToolTip(hoveringOver, mx, my);
	    GL11.glPopMatrix();
	}
    }

    public void drawGuiBackground()
    {
	ScaledResolution scl = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	double guiScaleW = guiImgW * scl.getScaleFactor();
	double guiScaleH = guiImgH * scl.getScaleFactor();
	mc.renderEngine.bindTexture(guiImg);
	DrawHelper.blurTexture(true);
	Tessellator tess = Tessellator.instance;
	tess.startDrawingQuads();
	DrawHelper.tessAddQuad(tess, mc.displayWidth / 2 - guiScaleW / 2, mc.displayHeight / 2 - guiScaleH / 2, mc.displayWidth / 2
		+ guiScaleW / 2, mc.displayHeight / 2 + guiScaleH / 2, 0, 0, 1, 1);
	tess.draw();
	DrawHelper.blurTexture(false);
    }

    @Override
    public boolean drawPanelBackground(PanelScrollList panel)
    {
	if (panel == catPanel || panel == itemsPanel || panel == recipePanel)
	{
	    DrawHelper.drawRect(-1, -1, panel.panelWidth + 1, panel.panelHeight + 1, 1, 1, 1, 1);
	    DrawHelper.drawRect(-1, -1, panel.panelWidth + 0, panel.panelHeight + 0, 0.1, 0.1, 0.1, 1);
	    DrawHelper.drawRect(0, 0, panel.panelWidth, panel.panelHeight, 0.55, 0.55, 0.55, 1);
	    return true;
	}
	return false;
    }
}
