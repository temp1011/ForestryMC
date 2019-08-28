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
package forestry.apiculture.genetics;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3i;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IAlleleBeeEffect;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBeeGenome;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.genetics.Genome;
import forestry.core.genetics.alleles.AlleleArea;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.genetics.alleles.AlleleTolerance;

public class BeeGenome extends Genome implements IBeeGenome {
	/**
	 * 0 - Species (determines product)
	 * 1 - Speed
	 * 2 - Lifespan
	 * 3 - Fertility (Maximum number of offspring)
	 * 4 - Preferred temperature Icy: Snow biomes Cold: Tundra/Steppe, Extreme Mountains/Hills? Normal: Plains, Forests, Mountains Hot: Desert Hellish: Nether
	 * 5 - Temperature tolerance (Range +/-)
	 * 6 - Nocturnal
	 * 7 - Preferred humidity (Arid - Normal - Damp)
	 * 8 - Humidity tolerance (Range +/-)
	 * 9 - Flight interference tolerance (stuff falling from the sky/other hindrances -> tolerates dampness + flight interference tolerance => rain resistance)
	 * 10 - Cave dwelling
	 * 11 - Required flowers
	 * 12 - Flower plant chance
	 * 13 - Territory
	 */

	private static final LoadingCache<CompoundNBT, BeeGenome> beeGenomeCache = CacheBuilder.newBuilder()
		.maximumSize(128)
		.expireAfterAccess(1, TimeUnit.MINUTES)
		.build(new CacheLoader<CompoundNBT, BeeGenome>() {
			@Override
			public BeeGenome load(CompoundNBT tagCompound) {
				return new BeeGenome(tagCompound);
			}
		});

	public static BeeGenome fromNBT(CompoundNBT CompoundNBT) {
		return beeGenomeCache.getUnchecked(CompoundNBT);
	}

	/* CONSTRUCTOR */
	private BeeGenome(CompoundNBT CompoundNBT) {
		super(CompoundNBT);
	}

	public BeeGenome(IChromosome[] chromosomes) {
		super(chromosomes);
	}

	// NBT RETRIEVAL
	public static IAlleleBeeSpecies getSpecies(ItemStack itemStack) {
		Preconditions.checkArgument(BeeManager.beeRoot.isMember(itemStack), "itemStack must be a bee");

		IAlleleForestrySpecies species = getSpeciesDirectly(BeeManager.beeRoot, itemStack);
		if (species instanceof IAlleleBeeSpecies) {
			return (IAlleleBeeSpecies) species;
		}

		return (IAlleleBeeSpecies) getActiveAllele(itemStack, BeeChromosomes.SPECIES, BeeManager.beeRoot);
	}

	// / INFORMATION RETRIEVAL
	@Override
	public IAlleleBeeSpecies getPrimary() {
		return (IAlleleBeeSpecies) getActiveAllele(BeeChromosomes.SPECIES);
	}

	@Override
	public IAlleleBeeSpecies getSecondary() {
		return (IAlleleBeeSpecies) getInactiveAllele(BeeChromosomes.SPECIES);
	}

	@Override
	public float getSpeed() {
		return ((IAlleleFloat) getActiveAllele(BeeChromosomes.SPEED)).getValue();
	}

	@Override
	public int getLifespan() {
		return ((IAlleleInteger) getActiveAllele(BeeChromosomes.LIFESPAN)).getValue();
	}

	@Override
	public int getFertility() {
		return ((IAlleleInteger) getActiveAllele(BeeChromosomes.FERTILITY)).getValue();
	}

	@Override
	public EnumTolerance getToleranceTemp() {
		return ((AlleleTolerance) getActiveAllele(BeeChromosomes.TEMPERATURE_TOLERANCE)).getValue();
	}

	@Override
	public boolean getNeverSleeps() {
		return ((AlleleBoolean) getActiveAllele(BeeChromosomes.NEVER_SLEEPS)).getValue();
	}

	@Override
	public EnumTolerance getToleranceHumid() {
		return ((AlleleTolerance) getActiveAllele(BeeChromosomes.HUMIDITY_TOLERANCE)).getValue();
	}

	@Override
	public boolean getToleratesRain() {
		return ((AlleleBoolean) getActiveAllele(BeeChromosomes.TOLERATES_RAIN)).getValue();
	}

	@Override
	public boolean getCaveDwelling() {
		return ((AlleleBoolean) getActiveAllele(BeeChromosomes.CAVE_DWELLING)).getValue();
	}

	@Override
	public IFlowerProvider getFlowerProvider() {
		return ((IAlleleFlowers) getActiveAllele(BeeChromosomes.FLOWER_PROVIDER)).getProvider();
	}

	@Override
	public int getFlowering() {
		return ((IAlleleInteger) getActiveAllele(BeeChromosomes.FLOWERING)).getValue();
	}

	@Override
	public Vec3i getTerritory() {
		return ((AlleleArea) getActiveAllele(BeeChromosomes.TERRITORY)).getArea();
	}

	@Override
	public IAlleleBeeEffect getEffect() {
		return (IAlleleBeeEffect) getActiveAllele(BeeChromosomes.EFFECT);
	}

	@Override
	public IForestrySpeciesRoot getSpeciesRoot() {
		return BeeManager.beeRoot;
	}
}
