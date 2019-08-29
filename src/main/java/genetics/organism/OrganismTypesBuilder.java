package genetics.organism;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismHandler;
import genetics.api.organism.IOrganismType;
import genetics.api.organism.IOrganismTypes;
import genetics.api.organism.IOrganismTypesBuilder;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.components.RootComponentBuilder;

import genetics.GeneticFactory;
import genetics.Log;

public class OrganismTypesBuilder<I extends IIndividual> extends RootComponentBuilder<IOrganismTypes<I>, I> implements IOrganismTypesBuilder<I> {
	private final Map<IOrganismType, IOrganismHandler<I>> types = new LinkedHashMap<>();
	@Nullable
	private IOrganismType defaultType;

	public OrganismTypesBuilder(IIndividualRoot<I> root) {
		super(root);
	}

	@Override
	public IOrganismTypesBuilder<I> registerType(IOrganismType type, IOrganismHandler<I> handler, boolean defaultType) {
		types.put(type, handler);
		if (defaultType) {
			this.defaultType = type;
		}
		return this;
	}

	@Override
	public IOrganismTypesBuilder<I> registerType(IOrganismType type, Supplier<ItemStack> stack, boolean defaultType) {
		return registerType(type, GeneticFactory.INSTANCE.createOrganismHandler(root.getDefinition(), stack), defaultType);
	}

	@Override
	public OrganismTypes<I> create() {
		if (defaultType == null) {
			Iterator<IOrganismType> organismTypes = types.keySet().iterator();
			if (!organismTypes.hasNext()) {
				String message = String.format("No types were registered for the individual root '%s'.", root.getUID());
				throw new IllegalStateException(message);
			}
			defaultType = organismTypes.next();

			Log.debug("No default type was registered for individual root '{}' used first registered type.", root.getUID());
		}
		return new OrganismTypes<>(types, Preconditions.checkNotNull(defaultType));
	}
}
