package genetics.root;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.components.RootComponentBuilder;
import genetics.api.root.translator.IBlockTranslator;
import genetics.api.root.translator.IIndividualTranslator;
import genetics.api.root.translator.IIndividualTranslatorBuilder;
import genetics.api.root.translator.IItemTranslator;

public class IndividualTranslatorBuilder<I extends IIndividual> extends RootComponentBuilder<IIndividualTranslator<I>, I> implements IIndividualTranslatorBuilder<I> {

	private final Map<Item, IItemTranslator<I>> itemTranslators = new HashMap<>();
	private final Map<Block, IBlockTranslator<I>> blockTranslators = new HashMap<>();

	public IndividualTranslatorBuilder(IIndividualRoot<I> root) {
		super(root);
	}

	@Override
	public IIndividualTranslatorBuilder<I> registerTranslator(IBlockTranslator<I> translator, Block... translatorKeys) {
		for (Block key : translatorKeys) {
			blockTranslators.put(key, translator);
		}
		return this;
	}

	@Override
	public IIndividualTranslatorBuilder<I> registerTranslator(IItemTranslator<I> translator, Item... translatorKeys) {
		for (Item key : translatorKeys) {
			itemTranslators.put(key, translator);
		}
		return this;
	}

	@Override
	public IIndividualTranslator<I> create() {
		return new IndividualTranslator<>(ImmutableMap.copyOf(itemTranslators), ImmutableMap.copyOf(blockTranslators));
	}
}
