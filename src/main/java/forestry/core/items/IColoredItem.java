package forestry.core.items;

import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
public interface IColoredItem {
	@OnlyIn(Dist.CLIENT)
	int getColorFromItemstack(ItemStack stack, int tintIndex);
}
