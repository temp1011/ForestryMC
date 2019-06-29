package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
public class ItemTooltipUtil {
	@OnlyIn(Dist.CLIENT)
	public static void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		String unlocalizedName = stack.getTranslationKey();
		String tooltipKey = unlocalizedName + ".tooltip";
		if (Translator.canTranslateToLocal(tooltipKey)) {
			String tooltipInfo = Translator.translateToLocal(tooltipKey);
			Minecraft minecraft = Minecraft.getInstance();
			List<String> tooltipInfoWrapped = minecraft.fontRenderer.listFormattedStringToWidth(tooltipInfo, 150);
			tooltip.addAll(tooltipInfoWrapped);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void addShiftInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		tooltip.add(TextFormatting.ITALIC.toString() + '<' + Translator.translateToLocal("for.gui.tooltip.tmi") + '>');
	}

	@OnlyIn(Dist.CLIENT)
	public static List<String> getInformation(ItemStack stack) {
		Minecraft minecraft = Minecraft.getInstance();
		boolean advancedTooltips = minecraft.gameSettings.advancedItemTooltips;
		return getInformation(stack, minecraft.player, advancedTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
	}

	@OnlyIn(Dist.CLIENT)
	public static List<String> getInformation(ItemStack stack, PlayerEntity player, ITooltipFlag flag) {
		if (stack.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> tooltip = stack.getTooltip(player, flag);
		for (int i = 0; i < tooltip.size(); ++i) {
			if (i == 0) {
				tooltip.set(i, stack.getRarity().color + tooltip.get(i));
			} else {
				tooltip.set(i, TextFormatting.GRAY + tooltip.get(i));
			}
		}
		return tooltip;
	}
}
