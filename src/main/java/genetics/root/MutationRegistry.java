package genetics.root;

import com.google.common.collect.ImmutableList;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.mutation.IMutationRegistry;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.components.RootComponentBuilder;

public class MutationRegistry<I extends IIndividual, M extends IMutation> extends RootComponentBuilder<IMutationContainer<M>, I> implements IMutationRegistry<M> {
	private final ImmutableList.Builder<M> mutations;

	public MutationRegistry(IIndividualRoot<I> root) {
		super(root);
		this.mutations = new ImmutableList.Builder<>();
	}

	@Override
	public boolean registerMutation(M mutation) {
		IChromosomeType speciesType = root.getKaryotype().getSpeciesType();
		IAlleleRegistry alleleRegistry = GeneticsAPI.apiInstance.getAlleleRegistry();
		IAllele firstParent = mutation.getFirstParent();
		IAllele secondParent = mutation.getSecondParent();
		IAllele resultSpecies = mutation.getTemplate()[speciesType.getIndex()];
		if (alleleRegistry.isBlacklisted(resultSpecies)
			|| alleleRegistry.isBlacklisted(firstParent)
			|| alleleRegistry.isBlacklisted(secondParent)) {
			return false;
		}
		mutations.add(mutation);
		return true;
	}

	@Override
	public IMutationContainer<M> create() {
		return new MutationContainer<>(root, mutations.build());
	}
}
