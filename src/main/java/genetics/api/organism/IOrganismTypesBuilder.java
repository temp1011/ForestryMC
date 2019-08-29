package genetics.api.organism;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import genetics.api.IGeneticFactory;
import genetics.api.individual.IIndividual;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.IRootComponentBuilder;

public interface IOrganismTypesBuilder<I extends IIndividual> extends IRootComponentBuilder<IOrganismTypes<I>> {

	/**
	 * Registers a {@link IOrganismType} for the {@link IIndividual} of the root.
	 * <p>
	 * {@link IGeneticFactory#createOrganismHandler(IRootDefinition, Supplier)} can be used to create the default
	 * implementation of an {@link IOrganismHandler}.
	 *
	 * @param type        The organism type itself.
	 * @param handler     The organism handler that handles the creation of the {@link IIndividual} and the {@link ItemStack}
	 *                    that contains the {@link IOrganism}.
	 * @param defaultType If the registered type should be the default type of the described individual. The first
	 *                    registered type will be used if no type has been registered as the default type.
	 */
	IOrganismTypesBuilder<I> registerType(IOrganismType type, IOrganismHandler<I> handler, boolean defaultType);

	default IOrganismTypesBuilder<I> registerType(IOrganismType type, IOrganismHandler<I> handler) {
		return registerType(type, handler, false);
	}

	/**
	 * Registers a {@link IOrganismType} for the {@link IIndividual} of the root.
	 * <p>
	 * Uses {@link IGeneticFactory#createOrganismHandler(IRootDefinition, Supplier)} to create the default
	 * implementation of an {@link IOrganismHandler} with the given parameters.
	 *
	 * @param type        The organism type itself.
	 * @param stack       A supplier that supplies the stack that will be used as the default stack for every stack that
	 *                    will be created with {@link IOrganismHandler#createStack(IIndividual)}.
	 * @param defaultType If the registered type should be the default type of the described individual. The first
	 *                    registered type will be used if no type has been registered as the default type.
	 */
	IOrganismTypesBuilder<I> registerType(IOrganismType type, Supplier<ItemStack> stack, boolean defaultType);

	default IOrganismTypesBuilder<I> registerType(IOrganismType type, Supplier<ItemStack> stack) {
		return registerType(type, stack, false);
	}

}
