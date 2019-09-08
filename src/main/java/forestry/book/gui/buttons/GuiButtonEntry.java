package forestry.book.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.IBookEntry;
import forestry.core.gui.GuiUtil;

@OnlyIn(Dist.CLIENT)
public class GuiButtonEntry extends Button {
	public final IBookEntry entry;

	public GuiButtonEntry(int x, int y, IBookEntry entry, IPressable action) {
		super(x, y, Minecraft.getInstance().fontRenderer.getStringWidth(entry.getTitle()) + 9, 11, entry.getTitle(), action);
		this.entry = entry;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

			String text = getMessage();
			if (isHovered) {
				text = TextFormatting.GOLD + text;
			} else {
				text = TextFormatting.DARK_GRAY + text;
			}

			boolean unicode = fontRenderer.getBidiFlag();
			fontRenderer.setBidiFlag(true);
			fontRenderer.drawString(text, this.x + 9, this.y, 0);
			fontRenderer.setBidiFlag(unicode);

			ItemStack stack = entry.getStack();
			if (!stack.isEmpty()) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef(x, y, blitOffset);	//TODO correct?
				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.enableRescaleNormal();
				GlStateManager.scalef(0.5F, 0.5F, 0.5F);
				GuiUtil.drawItemStack(fontRenderer, stack, 0, 0);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.popMatrix();
			}
		}
	}
}
