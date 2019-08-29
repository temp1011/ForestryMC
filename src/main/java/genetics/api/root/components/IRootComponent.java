package genetics.api.root.components;

import java.util.function.Consumer;

import genetics.api.mutation.IMutation;
import genetics.api.organism.IOrganismType;
import genetics.api.organism.IOrganismTypes;
import genetics.api.organism.IOrganismTypesBuilder;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IIndividualRootBuilder;

/**
 * Every {@link IIndividualRoot} contains {@link IRootComponent}s. They provide expand the function of the root without
 * expanding the root object itself.
 * <p>
 * A {@link IRootComponent} can be added to a root in the building stage of the root. It can be added with
 * {@link IIndividualRootBuilder#addComponent(ComponentKey, IRootComponentFactory)} or
 * {@link IIndividualRootBuilder#addComponent(ComponentKey)}. The last one of the two methods gets the
 * {@link IRootComponentFactory} that creates the builder of the component from the
 * {@link IRootComponentRegistry}. A registry where default component factories can be registered.
 * <p>
 * Every component needs a {@link IRootComponentBuilder} that gets created by a {@link IRootComponentFactory}. The builder
 * can be used to register all kind of things like {@link IOrganismType}s or {@link IMutation}s.
 * As a example the {@link IOrganismTypes} component uses its builder {@link IOrganismTypesBuilder} to register the
 * {@link IOrganismType}s.
 * <p>
 * The {@link IIndividualRootBuilder} can also be used to add listeners to it with
 * {@link IIndividualRootBuilder#addListener(ComponentKey, Consumer)}. These listeners are getting called before the
 * components are getting created. You can use these listeners to register things to the component builders.
 *
 * @see IRootComponentBuilder
 * @see IRootComponentFactory
 * @see IRootComponentRegistry
 * @see IIndividualRootBuilder
 */
public interface IRootComponent {
}
