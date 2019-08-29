package genetics.api.root.components;

import javax.annotation.Nullable;

import genetics.api.root.IIndividualRootBuilder;

/**
 * A registry that can be used to register default factories for {@link IRootComponentBuilder}s and get these.
 *
 * @see IRootComponentBuilder
 * @see IRootComponentFactory
 * @see IRootComponentRegistry
 * @see IIndividualRootBuilder
 */
public interface IRootComponentRegistry {
	/**
	 * Registers a default factory.
	 *
	 * @param key     The component key of the component builder that the factory creates.
	 * @param factory A factory that creates a component builder object.
	 */
	void registerFactory(ComponentKey key, IRootComponentFactory factory);

	/**
	 * @return A factory if one was registered, false otherwise.
	 */
	@Nullable
	IRootComponentFactory getFactory(ComponentKey key);
}
