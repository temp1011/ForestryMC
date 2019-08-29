package genetics.api.organism;

import java.util.Collection;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import net.minecraftforge.common.capabilities.Capability;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRootBuilder;
import genetics.api.root.components.IRootComponent;

/**
 * The IGeneticTypes offers several functions to retrieving genetic information from an {@link ItemStack}.
 * For every item that should contain genetic information you have to provide a {@link IOrganism} that can be
 * retrieved with {@link ItemStack#getCapability(Capability, Direction)} and you have to register a {@link IOrganismType}
 * and a {@link IOrganismHandler} for this type at the {@link IIndividualRootBuilder} that handles the individual.
 *
 * @param <I> The type of {@link IIndividual} that all items are containing.
 */
public interface IOrganismTypes<I extends IIndividual> extends IRootComponent {

	/**
	 * Creates a stack that has the item of the given type an the genetic information of the given individual with the
	 * help of the {@link IOrganismHandler} that was registered for the given type.
	 * {@link IOrganismHandler#createStack(IIndividual)}
	 *
	 * @param individual The individual that contains the genetic information
	 * @param type       The type in tha the individual
	 */
	ItemStack createStack(I individual, IOrganismType type);

	/**
	 * Creates a individual with the genetic information that the NBT-Data of the stack contains with the
	 * help of the {@link IOrganismHandler} that was registered for the given type.
	 * {@link IOrganismHandler#createIndividual(ItemStack)}
	 *
	 * @return A empty optional if no {@link IOrganismType} was registered for the item of this stack.
	 */
	Optional<I> createIndividual(ItemStack itemStack);

	/**
	 * Writes the genetic information of the given individual to the NBT-Data of the given stack with the help of
	 * the {@link IOrganismHandler} that was registered for the given type.
	 *
	 * @param individual The individual that contains the genetic information
	 */
	boolean setIndividual(ItemStack itemStack, I individual);

	/**
	 * Gets the type of the item that the given stack contains
	 *
	 * @return A empty optional if no {@link IOrganismType} was registered for the item of this stack.
	 */
	Optional<IOrganismType> getType(ItemStack itemStack);

	/**
	 * Gets the default type that will be used by the {@link genetics.api.root.IDisplayHelper} of the
	 * {@link genetics.api.root.IIndividualRoot} and every time a {@link IOrganismType} is required and no other type
	 * was provided.
	 *
	 * @return The default type that was registered at the builder.
	 */
	IOrganismType getDefaultType();

	/**
	 * Gets the handler that handles the {@link ItemStack}s of the given genetic type.
	 *
	 * @return A empty optional if the given {@link IOrganismType} was not registered in the
	 * {@link IIndividualRootBuilder}.
	 */
	Optional<IOrganismHandler<I>> getHandler(IOrganismType type);

	Optional<IOrganismHandler<I>> getHandler(ItemStack itemStack);

	/**
	 * All types that were registered at the {@link IIndividualRootBuilder}.
	 */
	Collection<IOrganismType> getTypes();

	/**
	 * All handlers that were registered at the {@link IIndividualRootBuilder}.
	 */
	Collection<IOrganismHandler<I>> getHandlers();
}
