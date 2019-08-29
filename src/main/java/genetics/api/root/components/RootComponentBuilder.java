package genetics.api.root.components;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRoot;

/**
 * A default implementation of the {@link IRootComponentBuilder} interface.
 *
 * @param <C> The type of the component that this builder builds.
 * @param <I> The type of the individual that the root object of this component builder describes.
 */
public abstract class RootComponentBuilder<C extends IRootComponent, I extends IIndividual> implements IRootComponentBuilder<C> {

	protected final IIndividualRoot<I> root;

	public RootComponentBuilder(IIndividualRoot<I> root) {
		this.root = root;
	}

	@Override
	public IIndividualRoot<I> getRoot() {
		return root;
	}
}