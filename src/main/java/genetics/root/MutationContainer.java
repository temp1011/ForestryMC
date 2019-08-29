package genetics.root;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.IIndividualRoot;

public class MutationContainer<I extends IIndividual, M extends IMutation> implements IMutationContainer<M> {

	private final ImmutableList<M> mutations;
	private final IIndividualRoot<I> root;

	public MutationContainer(IIndividualRoot<I> root, ImmutableList<M> mutations) {
		this.root = root;
		this.mutations = mutations;
	}

	@Override
	public List<M> getMutations(boolean shuffle) {
		if (shuffle) {
			Collections.shuffle(mutations);//ToDO: immutable?
		}
		return mutations;
	}

	@Override
	public List<M> getCombinations(IAllele other) {
		List<M> combinations = new ArrayList<>();
		for (M mutation : getMutations(false)) {
			if (mutation.isPartner(other)) {
				combinations.add(mutation);
			}
		}

		return combinations;
	}

	@Override
	public List<M> getResultantMutations(IAllele other) {
		IKaryotype karyotype = root.getKaryotype();
		List<M> resultants = new ArrayList<>();
		int speciesIndex = karyotype.getSpeciesType().getIndex();
		for (M mutation : getMutations(false)) {
			IAllele[] template = mutation.getTemplate();
			if (template.length <= speciesIndex) {
				continue;
			}
			IAllele speciesAllele = template[speciesIndex];
			if (speciesAllele == other) {
				resultants.add(mutation);
			}
		}

		return resultants;
	}

	@Override
	public List<M> getCombinations(IAlleleSpecies parentFirst, IAlleleSpecies parentSecond, boolean shuffle) {
		List<M> combinations = new ArrayList<>();

		ResourceLocation parentSpecies = parentSecond.getRegistryName();
		for (M mutation : getMutations(shuffle)) {
			if (mutation.isPartner(parentFirst)) {
				IAllele partner = mutation.getPartner(parentFirst);
				if (partner.getRegistryName().equals(parentSpecies)) {
					combinations.add(mutation);
				}
			}
		}

		return combinations;
	}

	@Override
	public Collection<M> getPaths(IAllele result, IChromosomeType chromosomeType) {
		ArrayList<M> paths = new ArrayList<>();
		for (M mutation : getMutations(false)) {
			IAllele[] template = mutation.getTemplate();
			IAllele mutationResult = template[chromosomeType.getIndex()];
			if (mutationResult == result) {
				paths.add(mutation);
			}
		}

		return paths;
	}
}
