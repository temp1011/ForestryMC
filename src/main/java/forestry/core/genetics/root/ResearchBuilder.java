package forestry.core.genetics.root;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.components.RootComponentBuilder;

import forestry.api.genetics.research.IResearchBuilder;
import forestry.api.genetics.research.IResearchHandler;

public class ResearchBuilder<I extends IIndividual> extends RootComponentBuilder<IResearchHandler, I> implements IResearchBuilder {

	private final ImmutableMap.Builder<ItemStack, Float> catalysts;
	private final ImmutableList.Builder<IResearchPlugin> plugins;

	public ResearchBuilder(IIndividualRoot<I> root) {
		super(root);
		this.catalysts = ImmutableMap.builder();
		this.plugins = ImmutableList.builder();
	}

	@Override
	public void setResearchSuitability(ItemStack stack, float suitability) {
		catalysts.put(stack, suitability);
	}

	@Override
	public void addPlugin(IResearchPlugin plugin) {
		plugins.add(plugin);
	}

	@Override
	public ResearchHandler<I> create() {
		return new ResearchHandler<>(root, catalysts.build(), plugins.build());
	}
}
