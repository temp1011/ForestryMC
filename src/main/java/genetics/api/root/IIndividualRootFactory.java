package genetics.api.root;

import java.util.Map;
import java.util.function.Function;

import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.IRootComponent;

public interface IIndividualRootFactory<I extends IIndividual, R extends IIndividualRoot<I>> {
	/**
	 * Creates a new root.
	 * <p>
	 * Used by {@link IIndividualRootBuilder} to create the root object.
	 */
	R createRoot(IKaryotype karyotype, Function<IIndividualRoot<I>, Map<ComponentKey, IRootComponent>> components);
}
