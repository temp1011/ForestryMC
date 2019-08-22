package forestry.cultivation.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.Translator;
import forestry.cultivation.blocks.BlockPlanter;

public class ItemBlockPlanter extends ItemBlockForestry<BlockPlanter> {

	public ItemBlockPlanter(BlockPlanter block) {
		super(block);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return new TranslationTextComponent("tile.for.planter." + (/*TODO BlockPlanter.isManual(stack)*/ true ? "manual" : "managed"), super.getDisplayName(stack));
	}
}
