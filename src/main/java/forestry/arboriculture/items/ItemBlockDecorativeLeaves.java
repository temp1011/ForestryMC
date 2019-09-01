package forestry.arboriculture.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemBlockForestry;

public class ItemBlockDecorativeLeaves extends ItemBlockForestry<BlockDecorativeLeaves> implements IColoredItem {
	public ItemBlockDecorativeLeaves(BlockDecorativeLeaves block) {
		super(block);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack itemStack) {
		BlockDecorativeLeaves block = getBlock();
		TreeDefinition treeDefinition = block.getDefinition();
		String unlocalizedSpeciesName = treeDefinition.getUnlocalizedName();
		return ItemBlockLeaves.getDisplayName(unlocalizedSpeciesName);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColorFromItemstack(ItemStack itemStack, int renderPass) {
		BlockDecorativeLeaves block = getBlock();
		TreeDefinition treeDefinition = block.getDefinition();

		ITreeGenome genome = treeDefinition.getGenome();

		if (renderPass == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			IFruitProvider fruitProvider = genome.getFruitProvider();
			return fruitProvider.getDecorativeColor();
		}
		return genome.getPrimary().getLeafSpriteProvider().getColor(false);
	}
}
