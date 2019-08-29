package genetics.api.root.translator;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import genetics.api.individual.IIndividual;

public interface ITranslatorFactory<I extends IIndividual> {
	IIndividualTranslator<I> create(ImmutableMap<Item, IItemTranslator<I>> itemTranslators, ImmutableMap<Block, IBlockTranslator<I>> blockTranslators);
}
