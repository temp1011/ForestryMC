package genetics.api.mutation;

import genetics.api.root.components.IRootComponentBuilder;

public interface IMutationRegistry<M extends IMutation> extends IRootComponentBuilder<IMutationContainer<M>> {
	/**
	 * Registers the given mutation to the component.
	 */
	boolean registerMutation(M mutation);
}
