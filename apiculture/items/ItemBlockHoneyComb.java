package forestry.apiculture.items;

import net.minecraft.item.ItemStack;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.apiculture.blocks.BlockHoneyComb;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemBlockForestry;

public class ItemBlockHoneyComb extends ItemBlockForestry<BlockHoneyComb> implements IColoredItem {

	public ItemBlockHoneyComb(BlockHoneyComb block) {
		super(block);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
		int meta = stack.getMetadata();
		EnumHoneyComb honeyComb = EnumHoneyComb.get(getBlock().minMeta + meta);
		if (tintIndex == 1) {
			return honeyComb.primaryColor;
		} else {
			return honeyComb.secondaryColor;
		}
	}

}
