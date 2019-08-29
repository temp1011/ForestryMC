package genetics.api.organism;

import com.google.common.collect.ImmutableMap;

import genetics.api.individual.IIndividual;

public interface IOrganismTypesFactory<I extends IIndividual> {
	IOrganismTypes<I> create(ImmutableMap<IOrganismType, IOrganismHandler<I>> registeredTypes, IOrganismType defaultType);
}
