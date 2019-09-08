/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.client.config.GuiUtils;

import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;

@OnlyIn(Dist.CLIENT)
public class GuiUtil {
	public static void drawItemStack(GuiForestry gui, ItemStack stack, int xPos, int yPos) {
		drawItemStack(gui.getFontRenderer(), stack, xPos, yPos);
	}

	public static void drawItemStack(FontRenderer fontRenderer, ItemStack stack, int xPos, int yPos) {
		FontRenderer font = null;
		if (!stack.isEmpty()) {
			font = stack.getItem().getFontRenderer(stack);
		}
		if (font == null) {
			font = fontRenderer;
		}

		ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
		itemRender.renderItemAndEffectIntoGUI(stack, xPos, yPos);
		itemRender.renderItemOverlayIntoGUI(font, stack, xPos, yPos, null);
	}

	//TODO hopefully this is client side...
	public static void drawToolTips(IGuiSizable gui, @Nullable IToolTipProvider provider, ToolTip toolTips, int mouseX, int mouseY) {
		//TODO textcomponent
		List<String> lines = toolTips.getLines().stream().map(ITextComponent::getFormattedText).collect(Collectors.toList());
		if (!lines.isEmpty()) {
			GlStateManager.pushMatrix();
			if (provider == null || provider.isRelativeToGui()) {
				GlStateManager.translatef(-gui.getGuiLeft(), -gui.getGuiTop(), 0);
			}
			MainWindow window = Minecraft.getInstance().mainWindow;	//TODO - more resolution stuff to check
			GuiUtils.drawHoveringText(lines, mouseX, mouseY, window.getScaledWidth(), window.getScaledHeight(), -1, gui.getMC().fontRenderer);
			GlStateManager.popMatrix();
		}
	}

	public static void drawToolTips(IGuiSizable gui, Collection<?> objects, int mouseX, int mouseY) {
		for (Object obj : objects) {
			if (!(obj instanceof IToolTipProvider)) {
				continue;
			}
			IToolTipProvider provider = (IToolTipProvider) obj;
			if (!provider.isToolTipVisible()) {
				continue;
			}
			int mX = mouseX;
			int mY = mouseY;
			if (provider.isRelativeToGui()) {
				mX -= gui.getGuiLeft();
				mY -= gui.getGuiTop();
			}
			ToolTip tips = provider.getToolTip(mX, mY);
			if (tips == null) {
				continue;
			}
			boolean mouseOver = provider.isMouseOver(mX, mY);
			tips.onTick(mouseOver);
			if (mouseOver && tips.isReady()) {
				tips.refresh();
				drawToolTips(gui, provider, tips, mouseX, mouseY);
			}
		}
	}
}