/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.genetics;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IForestryMutation;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IIndividualTranslator;

public abstract class SpeciesRoot implements IForestrySpeciesRoot {
	/* TRANSLATORS */
	private final HashMap<Object, IIndividualTranslator<IIndividual, Object>> translators = new HashMap<>();

	/* RESEARCH */
	private final LinkedHashMap<ItemStack, Float> researchCatalysts = new LinkedHashMap<>();

	@Override
	public Map<ItemStack, Float> getResearchCatalysts() {
		return Collections.unmodifiableMap(researchCatalysts);
	}

	@Override
	public void setResearchSuitability(ItemStack itemstack, float suitability) {
		researchCatalysts.put(itemstack, suitability);
	}

	/* TEMPLATES */
	protected final HashMap<String, IAllele[]> speciesTemplates = new HashMap<>();

	@Override
	public Map<String, IAllele[]> getGenomeTemplates() {
		return speciesTemplates;
	}

	@Override
	public void registerTemplate(IAllele[] template) {
		Preconditions.checkNotNull(template, "Tried to register null template");
		Preconditions.checkArgument(template.length > 0, "Tried to register empty template");

		registerTemplate(template[0].getUID(), template);
	}

	@Override
	public IAllele[] getRandomTemplate(Random rand) {
		Collection<IAllele[]> templates = speciesTemplates.values();
		int size = templates.size();
		IAllele[][] templatesArray = templates.toArray(new IAllele[size][]);
		return templatesArray[rand.nextInt(size)];
	}

	@Override
	public IAllele[] getTemplate(String identifier) {
		IAllele[] template = speciesTemplates.get(identifier);
		if (template == null) {
			return null;
		}
		return Arrays.copyOf(template, template.length);
	}

	@Override
	public IAllele[] getTemplate(IAlleleForestrySpecies species) {
		IAllele[] template = getTemplate(species.getUID());
		if (template == null) {
			throw new IllegalStateException("No template found for species " + species.getUID());
		}
		return template;
	}

	/* TRANSLATORS */
	@Override
	public <O, I extends IIndividual> void registerTranslator(Object translatorKey, IIndividualTranslator<I, O> translator) {
		if (!translators.containsKey(translatorKey)) {
			translators.put(translatorKey, translator);
		}
	}

	@Nullable
	@Override
	public <O, I extends IIndividual> IIndividualTranslator<I, O> getTranslator(Object translatorKey) {
		return translators.get(translatorKey);
	}

	@Nullable
	@Override
	public <O, I extends IIndividual> I translateMember(O objectToTranslator) {
		Object translatorKey = null;
		if (objectToTranslator instanceof ItemStack) {
			translatorKey = ((ItemStack) objectToTranslator).getItem();
		} else if (objectToTranslator instanceof BlockState) {
			translatorKey = ((BlockState) objectToTranslator).getBlock();
		}
		if (translatorKey == null) {
			return null;
		}
		IIndividualTranslator<I, O> translator = getTranslator(translatorKey);
		if (translator == null) {
			return null;
		}
		return translator.getIndividualFromObject(objectToTranslator);
	}

	/* MUTATIONS */
	@Override
	public List<IForestryMutation> getCombinations(IAllele other) {
		List<IForestryMutation> combinations = new ArrayList<>();
		for (IForestryMutation mutation : getMutations(false)) {
			if (mutation.isPartner(other)) {
				combinations.add(mutation);
			}
		}

		return combinations;
	}

	@Override
	public List<? extends IForestryMutation> getResultantMutations(IAllele other) {
		List<IForestryMutation> mutations = new ArrayList<>();
		int speciesIndex = getSpeciesChromosomeType().ordinal();
		for (IForestryMutation mutation : getMutations(false)) {
			IAllele[] template = mutation.getTemplate();
			if (template == null || template.length <= speciesIndex) {
				continue;
			}
			IAllele speciesAllele = template[speciesIndex];
			if (speciesAllele == other) {
				mutations.add(mutation);
			}
		}

		return mutations;
	}

	@Override
	public List<IForestryMutation> getCombinations(IAlleleForestrySpecies parentSpecies0, IAlleleForestrySpecies parentSpecies1, boolean shuffle) {
		List<IForestryMutation> combinations = new ArrayList<>();

		String parentSpecies1UID = parentSpecies1.getUID();
		for (IForestryMutation mutation : getMutations(shuffle)) {
			if (mutation.isPartner(parentSpecies0)) {
				IAllele partner = mutation.getPartner(parentSpecies0);
				if (partner.getUID().equals(parentSpecies1UID)) {
					combinations.add(mutation);
				}
			}
		}

		return combinations;
	}

	@Override
	public Collection<? extends IForestryMutation> getPaths(IAllele result, IChromosomeType chromosomeType) {
		ArrayList<IForestryMutation> paths = new ArrayList<>();
		for (IForestryMutation mutation : getMutations(false)) {
			if (mutation.getTemplate()[chromosomeType.ordinal()] == result) {
				paths.add(mutation);
			}
		}

		return paths;
	}

	/* GENOME CONVERSIONS */
	@Override
	public IChromosome[] templateAsChromosomes(IAllele[] template) {
		Chromosome[] chromosomes = new Chromosome[template.length];
		for (int i = 0; i < template.length; i++) {
			if (template[i] != null) {
				chromosomes[i] = new Chromosome(template[i]);
			}
		}

		return chromosomes;
	}

	@Override
	public IChromosome[] templateAsChromosomes(IAllele[] templateActive, IAllele[] templateInactive) {
		Chromosome[] chromosomes = new Chromosome[templateActive.length];
		for (int i = 0; i < templateActive.length; i++) {
			if (templateActive[i] != null) {
				chromosomes[i] = new Chromosome(templateActive[i], templateInactive[i]);
			}
		}

		return chromosomes;
	}
}
