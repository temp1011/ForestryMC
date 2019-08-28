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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import genetics.api.IGeneticApiInstance;
import genetics.api.alleles.IAlleleHelper;
import genetics.api.alleles.IAlleleRegistry;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IChromosomeType;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.apiculture.flowers.FlowerProvider;
import forestry.core.config.Constants;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

public class AlleleHelper implements IAlleleHelper {

	private static final String modId = Constants.MOD_ID;
	@Nullable
	private static AlleleHelper instance;

	private final Map<Class, Map<?, ? extends IAllele>> alleleMaps = new HashMap<>();

	public static AlleleHelper getInstance() {
		if (instance == null) {
			instance = new AlleleHelper();
			instance.init();
		}
		return instance;
	}

	private void init(IAlleleRegistry registry, IGeneticApiInstance apiInstance) {
		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			createAlleles(EnumAllele.Fertility.class, BeeChromosomes.FERTILITY);
			createAlleles(EnumAllele.Flowering.class, BeeChromosomes.FLOWERING);
			createAlleles(EnumAllele.Territory.class, BeeChromosomes.TERRITORY);
		}

		if (ModuleHelper.anyEnabled(ForestryModuleUids.APICULTURE, ForestryModuleUids.LEPIDOPTEROLOGY)) {
			createAlleles(EnumAllele.Speed.class,
				BeeChromosomes.SPEED,
				ButterflyChromosomes.SPEED
			);
			createAlleles(EnumAllele.Lifespan.class,
				BeeChromosomes.LIFESPAN,
				ButterflyChromosomes.LIFESPAN
			);
			createAlleles(EnumAllele.Tolerance.class,
				BeeChromosomes.TEMPERATURE_TOLERANCE,
				BeeChromosomes.HUMIDITY_TOLERANCE,
				ButterflyChromosomes.TEMPERATURE_TOLERANCE,
				ButterflyChromosomes.HUMIDITY_TOLERANCE
			);
			createAlleles(EnumAllele.Flowers.class,
				BeeChromosomes.FLOWER_PROVIDER,
				ButterflyChromosomes.FLOWER_PROVIDER
			);
		}

		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			registry.registerAlleles(EnumAllele.Height.values(), TreeChromosomes.HEIGHT);
			createAlleles(EnumAllele.Saplings.class, TreeChromosomes.FERTILITY);
			createAlleles(EnumAllele.Yield.class, TreeChromosomes.YIELD);
			createAlleles(EnumAllele.Fireproof.class, TreeChromosomes.FIREPROOF);
			createAlleles(EnumAllele.Maturation.class, TreeChromosomes.MATURATION);
			createAlleles(EnumAllele.Sappiness.class, TreeChromosomes.SAPPINESS);
		}

		if (ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
			createAlleles(EnumAllele.Size.class, ButterflyChromosomes.SIZE);
		}

		Map<Integer, IAlleleInteger> integers = new HashMap<>();
		for (int i = 1; i <= 10; i++) {
			IAlleleInteger alleleInteger = new AlleleInteger(modId, "i", i + "d", i, true);
			AlleleManager.alleleRegistry.registerAllele(alleleInteger,
				TreeChromosomes.GIRTH,
				ButterflyChromosomes.METABOLISM,
				ButterflyChromosomes.FERTILITY
			);
			integers.put(i, alleleInteger);
		}
		alleleMaps.put(Integer.class, integers);

		Map<Boolean, IAlleleBoolean> booleans = new HashMap<>();
		booleans.put(true, new AlleleBoolean(modId, "bool", true, false));
		booleans.put(false, new AlleleBoolean(modId, "bool", false, false));
		for (IAlleleBoolean alleleBoolean : booleans.values()) {
			AlleleManager.alleleRegistry.registerAllele(alleleBoolean,
				BeeChromosomes.NEVER_SLEEPS,
				BeeChromosomes.TOLERATES_RAIN,
				BeeChromosomes.CAVE_DWELLING,
				ButterflyChromosomes.NOCTURNAL,
				ButterflyChromosomes.TOLERANT_FLYER,
				ButterflyChromosomes.FIRE_RESIST
			);
		}
		alleleMaps.put(Boolean.class, booleans);
	}

	private static <K extends IAlleleValue<V>, V> IAllele createAllele(String category, K enumValue, IChromosomeType... types) {
		V value = enumValue.getValue();
		boolean isDominant = enumValue.isDominant();
		String name = enumValue.toString().toLowerCase(Locale.ENGLISH);

		Class<?> valueClass = value.getClass();
		if (FlowerProvider.class.isAssignableFrom(valueClass)) {
			return AlleleManager.alleleFactory.createFlowers(modId, category, name, (FlowerProvider) value, isDominant, types);
		}
		throw new RuntimeException("could not create allele for category: " + category + " and value " + valueClass);
	}
}
