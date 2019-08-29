package genetics.organism;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import net.minecraft.item.ItemStack;

import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganism;
import genetics.api.organism.IOrganismHandler;
import genetics.api.organism.IOrganismType;
import genetics.api.organism.IOrganismTypes;

import genetics.Genetics;

public class OrganismTypes<I extends IIndividual> implements IOrganismTypes<I> {
	private final Map<IOrganismType, IOrganismHandler<I>> types;
	private final IOrganismType defaultType;

	public OrganismTypes(Map<IOrganismType, IOrganismHandler<I>> types, IOrganismType defaultType) {
		this.types = types;
		this.defaultType = defaultType;
	}

	@Override
	public ItemStack createStack(I individual, IOrganismType type) {
		IOrganismHandler<I> handler = types.get(type);
		if (handler == null) {
			return ItemStack.EMPTY;
		}
		return handler.createStack(individual);
	}

	@Override
	public Optional<I> createIndividual(ItemStack itemStack) {
		Optional<IOrganismType> optional = getType(itemStack);
		if (!optional.isPresent()) {
			return Optional.empty();
		}
		IOrganismHandler<I> handler = types.get(optional.get());
		if (handler == null) {
			return Optional.empty();
		}
		return handler.createIndividual(itemStack);
	}

	@Override
	public boolean setIndividual(ItemStack itemStack, I individual) {
		Optional<IOrganismType> optional = getType(itemStack);
		if (!optional.isPresent()) {
			return false;
		}
		IOrganismHandler<I> handler = types.get(optional.get());
		if (handler == null) {
			return false;
		}
		return handler.setIndividual(itemStack, individual);
	}

	@Override
	public Optional<IOrganismType> getType(ItemStack itemStack) {
		IOrganism organism = itemStack.getCapability(Genetics.ORGANISM).orElse(GeneticHelper.EMPTY);
		return organism.isEmpty() ? Optional.empty() : Optional.of(organism.getType());
	}

	@Override
	public IOrganismType getDefaultType() {
		return defaultType;
	}

	@Override
	public Optional<IOrganismHandler<I>> getHandler(IOrganismType type) {
		return Optional.ofNullable(types.get(type));
	}

	@Override
	public Optional<IOrganismHandler<I>> getHandler(ItemStack itemStack) {
		Optional<IOrganismType> type = getType(itemStack);
		return type.flatMap(this::getHandler);
	}

	@Override
	public Collection<IOrganismType> getTypes() {
		return types.keySet();
	}

	@Override
	public Collection<IOrganismHandler<I>> getHandlers() {
		return types.values();
	}
}
