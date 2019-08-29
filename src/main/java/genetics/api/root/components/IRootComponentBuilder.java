package genetics.api.root.components;

import java.util.function.Consumer;

import genetics.api.root.IIndividualRoot;
import genetics.api.root.IIndividualRootBuilder;

/**
 * A component builder can be used to create a {@link IRootComponent} and to gather many thing for the component like
 * {@link genetics.api.organism.IOrganismType}s or {@link genetics.api.mutation.IMutation}s.
 * <p>
 * If you want to use a method of an builder you can use {@link IIndividualRootBuilder#addListener(ComponentKey, Consumer)}.
 * <p>
 *
 * @param <C> The type of the component
 * @see IRootComponentBuilder
 * @see IRootComponentFactory
 * @see IRootComponentRegistry
 * @see IIndividualRootBuilder
 */
public interface IRootComponentBuilder<C extends IRootComponent> {
	C create();

	IIndividualRoot getRoot();
}
