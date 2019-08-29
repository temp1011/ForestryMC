package genetics.root;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;
import genetics.api.root.translator.IBlockTranslator;
import genetics.api.root.translator.IIndividualTranslator;
import genetics.api.root.translator.IItemTranslator;

public class IndividualTranslator<I extends IIndividual> implements IIndividualTranslator<I> {
	private final Map<Item, IItemTranslator<I>> itemTranslators;
	private final Map<Block, IBlockTranslator<I>> blockTranslators;

	public IndividualTranslator(ImmutableMap<Item, IItemTranslator<I>> itemTranslators, ImmutableMap<Block, IBlockTranslator<I>> blockTranslators) {
		this.itemTranslators = itemTranslators;
		this.blockTranslators = blockTranslators;
	}

	@Override
	public Optional<IItemTranslator<I>> getTranslator(Item translatorKey) {
		return Optional.ofNullable(itemTranslators.get(translatorKey));
	}

	@Override
	public Optional<IBlockTranslator<I>> getTranslator(Block translatorKey) {
		return Optional.ofNullable(blockTranslators.get(translatorKey));
	}

	@Override
	public Optional<I> translateMember(BlockState objectToTranslate) {
		Optional<IBlockTranslator<I>> optional = getTranslator(objectToTranslate.getBlock());
		if (!optional.isPresent()) {
			return Optional.empty();
		}
		return Optional.ofNullable(optional.get().getIndividualFromObject(objectToTranslate));
	}

	@Override
	public Optional<I> translateMember(ItemStack objectToTranslate) {
		Optional<IItemTranslator<I>> optional = getTranslator(objectToTranslate.getItem());
		if (!optional.isPresent()) {
			return Optional.empty();
		}
		return Optional.ofNullable(optional.get().getIndividualFromObject(objectToTranslate));
	}

	@Override
	public ItemStack getGeneticEquivalent(BlockState objectToTranslate) {
		Optional<IBlockTranslator<I>> optional = getTranslator(objectToTranslate.getBlock());
		return optional.map(blockTranslator -> blockTranslator.getGeneticEquivalent(objectToTranslate)).orElse(ItemStack.EMPTY);
	}

	@Override
	public ItemStack getGeneticEquivalent(ItemStack objectToTranslate) {
		Optional<IItemTranslator<I>> optional = getTranslator(objectToTranslate.getItem());
		return optional.map(itemTranslator -> itemTranslator.getGeneticEquivalent(objectToTranslate)).orElse(ItemStack.EMPTY);
	}
}
