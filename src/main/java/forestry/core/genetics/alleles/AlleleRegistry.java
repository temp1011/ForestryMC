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
package forestry.core.genetics.alleles;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import com.mojang.authlib.GameProfile;

import genetics.api.GeneticsAPI;
import genetics.api.classification.IClassification;
import genetics.api.classification.IClassification.EnumClassLevel;
import genetics.api.classification.IClassificationRegistry;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;

import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.api.genetics.IAlleleHandler;
import forestry.api.genetics.IAlleleRegistry;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.genetics.IFruitFamily;
import forestry.core.ModuleCore;
import forestry.core.genetics.ItemResearchNote.EnumNoteType;

public class AlleleRegistry implements IAlleleRegistry {

	/* ALLELES */
	private final LinkedHashMap<String, IFruitFamily> fruitMap = new LinkedHashMap<>(64);

	/*
	 * Internal Set of all alleleHandlers, which trigger when an allele or branch is registered
	 */
	private final Set<IAlleleHandler> alleleHandlers = new HashSet<>();

	@Override
	public Map<String, IForestrySpeciesRoot> getSpeciesRoot() {
		return Collections.unmodifiableMap(rootMap);
	}

	@Override
	@Nullable
	public IForestrySpeciesRoot getSpeciesRoot(String uid) {
		return rootMap.get(uid);
	}

	@Override
	@Nullable
	public IForestrySpeciesRoot getSpeciesRoot(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}

		for (IForestrySpeciesRoot root : rootMap.values()) {
			if (root.isMember(stack)) {
				return root;
			}
		}
		return null;
	}

	@Override
	@Nullable
	public IForestrySpeciesRoot getSpeciesRoot(Class<? extends IIndividual> individualClass) {
		for (IForestrySpeciesRoot root : rootMap.values()) {
			if (root.getMemberClass().isAssignableFrom(individualClass)) {
				return root;
			}
		}
		return null;
	}

	@Override
	public IForestrySpeciesRoot getSpeciesRoot(IIndividual individual) {
		return individual.getGenome().getSpeciesRoot();
	}

	/* INDIVIDUALS */
	@Override
	@Nullable
	public IIndividual getIndividual(ItemStack stack) {
		IForestrySpeciesRoot root = getSpeciesRoot(stack);
		if (root == null) {
			return null;
		}

		return root.create(stack);
	}

	public void initialize() {

		IClassificationRegistry registry = GeneticsAPI.apiInstance.getClassificationRegistry();
		registry.createAndRegisterClassification(IClassification.EnumClassLevel.DOMAIN, "archaea", "Archaea");
		registry.createAndRegisterClassification(EnumClassLevel.DOMAIN, "bacteria", "Bacteria");
		IClassification eukarya = registry.createAndRegisterClassification(EnumClassLevel.DOMAIN, "eukarya", "Eukarya");

		eukarya.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.KINGDOM, "animalia", "Animalia"));
		eukarya.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.KINGDOM, "plantae", "Plantae"));
		eukarya.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.KINGDOM, "fungi", "Fungi"));
		eukarya.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.KINGDOM, "protista", "Protista"));

		registry.getClassification("kingdom.animalia")
			.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.PHYLUM, "arthropoda", "Arthropoda"));

		// Animalia
		registry.getClassification("phylum.arthropoda")
			.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.CLASS, "insecta", "Insecta"));

	}

	/* FRUIT FAMILIES */
	@Override
	public void registerFruitFamily(IFruitFamily family) {
		fruitMap.put(family.getUID(), family);
		for (IAlleleHandler handler : this.alleleHandlers) {
			handler.onRegisterFruitFamily(family);
		}
	}

	@Override
	public Map<String, IFruitFamily> getRegisteredFruitFamilies() {
		return Collections.unmodifiableMap(fruitMap);
	}

	@Override
	public IFruitFamily getFruitFamily(String uid) {
		return fruitMap.get(uid);
	}

	/* ALLELE HANDLERS */
	@Override
	public void registerAlleleHandler(IAlleleHandler handler) {
		this.alleleHandlers.add(handler);
	}

	/* BLACKLIST */
	private final ArrayList<String> blacklist = new ArrayList<>();

	@Override
	public void blacklistAllele(String uid) {
		blacklist.add(uid);
	}

	@Override
	public Collection<String> getAlleleBlacklist() {
		return Collections.unmodifiableCollection(blacklist);
	}

	@Override
	public boolean isBlacklisted(String uid) {
		return blacklist.contains(uid);
	}

	/* RESEARCH */
	@Override
	public ItemStack getSpeciesNoteStack(GameProfile researcher, IAlleleForestrySpecies species) {
		return EnumNoteType.createSpeciesNoteStack(ModuleCore.getItems().researchNote, researcher, species);
	}

	@Override
	public ItemStack getMutationNoteStack(GameProfile researcher, IMutation mutation) {
		return EnumNoteType.createMutationNoteStack(ModuleCore.getItems().researchNote, researcher, mutation);
	}
}
