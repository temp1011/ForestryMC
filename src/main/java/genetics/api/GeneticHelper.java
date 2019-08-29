package genetics.api;

import java.util.Optional;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganism;
import genetics.api.organism.IOrganismHandler;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

/**
 * A helper class that contains some help methods.
 */
public class GeneticHelper {

	@CapabilityInject(IOrganism.class)
	public static Capability<IOrganism> ORGANISM;
	public static IOrganism<?> EMPTY;

	private GeneticHelper() {
	}

	public static <I extends IIndividual> IOrganism<I> createOrganism(ItemStack itemStack, IOrganismType type, IRootDefinition<? extends IIndividualRoot<I>> root) {
		IGeneticFactory geneticFactory = GeneticsAPI.apiInstance.getGeneticFactory();
		return geneticFactory.createOrganism(itemStack, type, root);
	}

	@SuppressWarnings("unchecked")
	public static <I extends IIndividual> IOrganism<I> getOrganism(ItemStack itemStack) {
		return itemStack.getCapability(ORGANISM).orElse(EMPTY);
	}

	@SuppressWarnings("unchecked")
	public static <I extends IIndividual> boolean setIndividual(ItemStack itemStack, I individual) {
		IOrganism organism = itemStack.getCapability(ORGANISM).orElse(EMPTY);
		return organism.setIndividual(individual);
	}

	@SuppressWarnings("unchecked")
	public static <I extends IIndividual> Optional<I> getIndividual(ItemStack itemStack) {
		return itemStack.getCapability(ORGANISM).orElse(EMPTY).getIndividual();
	}

	public static IOrganismHandler getOrganismHandler(IIndividualRoot<IIndividual> root, IOrganismType type) {
		Optional<IOrganismHandler<IIndividual>> optionalHandler = root.getTypes().getHandler(type);
		if (!optionalHandler.isPresent()) {
			throw new IllegalArgumentException(String.format("No organism handler was registered for the organism type '%s'", type.getName()));
		}
		return optionalHandler.get();
	}
}
