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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import genetics.api.individual.IGenomeWrapper;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IndividualRoot;

import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.genetics.IBeeMutation;
import forestry.api.apiculture.genetics.IBeeRoot;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IDatabasePlugin;
import forestry.apiculture.BeeHousingListener;
import forestry.apiculture.BeeHousingModifier;
import forestry.apiculture.BeekeepingLogic;
import forestry.core.utils.Log;

public class BeeRoot extends IndividualRoot<IBee> implements IBeeRoot {

	private static int beeSpeciesCount = -1;
	private static final List<IBee> beeTemplates = new ArrayList<>();
	/**
	 * List of possible mutations on species alleles.
	 */
	private static final List<IBeeMutation> beeMutations = new ArrayList<>();
	public static final String UID = "rootBees";

	private final List<IBeekeepingMode> beekeepingModes = new ArrayList<>();

	@Nullable
	private static IBeekeepingMode activeBeekeepingMode;

	@Override
	public Class<? extends IBee> getMemberClass() {
		return IBee.class;
	}

	@Override
	public int getSpeciesCount() {
		if (beeSpeciesCount < 0) {
			beeSpeciesCount = 0;
			for (IAllele allele : GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles(BeeChromosomes.SPECIES)) {
				if (allele instanceof IAlleleBeeSpecies) {
					if (((IAlleleBeeSpecies) allele).isCounted()) {
						beeSpeciesCount++;
					}
				}
			}
		}

		return beeSpeciesCount;
	}



	@Override
	public EnumBeeType getIconType() {
		return EnumBeeType.DRONE;
	}

	@Override
	public IOrganismType getTypeForMutation(int position) {
		switch (position) {
			case 0:
				return EnumBeeType.PRINCESS;
			case 1:
				return EnumBeeType.DRONE;
			case 2:
				return EnumBeeType.QUEEN;
		}
		return getIconType();
	}

	@Override
	public boolean isDrone(ItemStack stack) {
		Optional<IOrganismType> optional = getTypes().getType(stack);
		return optional.isPresent() && optional.get() == EnumBeeType.DRONE;
	}

	@Override
	public boolean isMated(ItemStack stack) {
		Optional<IOrganismType> optionalType = types.getType(stack);
		if (!optionalType.isPresent() || optionalType.get() != EnumBeeType.QUEEN) {
			return false;
		}

		CompoundNBT nbt = stack.getTag();
		return nbt != null && nbt.contains("Mate");
	}

	@Override
	public IBee create(IGenome genome) {
		return new Bee(genome);
	}

	@Override
	public IBee create(IGenome genome, IGenome mate) {
		return new Bee(genome, mate);
	}

	@Override
	public IGenomeWrapper createWrapper(IGenome genome) {
		return () -> genome;
	}

	@Override
	public IBee create(CompoundNBT compound) {
		return new Bee(compound);
	}

	@Override
	public IBee getBee(World world, IGenome genome, IBee mate) {
		return new Bee(genome, mate);
	}

	/* BREEDING MODES */
	@Override
	public void resetBeekeepingMode() {
		activeBeekeepingMode = null;
	}

	@Override
	public List<IBeekeepingMode> getBeekeepingModes() {
		return this.beekeepingModes;
	}

	@Override
	public IBeekeepingMode getBeekeepingMode(ServerWorld world) {
		if (activeBeekeepingMode != null) {
			return activeBeekeepingMode;
		}

		// No beekeeping mode yet, getComb it.
		IApiaristTracker tracker = getBreedingTracker(world, null);
		String modeName = tracker.getModeName();
		IBeekeepingMode mode = getBeekeepingMode(modeName);
		Preconditions.checkNotNull(mode);

		setBeekeepingMode(world, mode);
		Log.debug("Set beekeeping mode for a world to " + mode);

		return activeBeekeepingMode;
	}

	@Override
	public void registerBeekeepingMode(IBeekeepingMode mode) {
		beekeepingModes.add(mode);
	}

	@Override
	public void setBeekeepingMode(ServerWorld world, IBeekeepingMode mode) {
		Preconditions.checkNotNull(world);
		Preconditions.checkNotNull(mode);
		activeBeekeepingMode = mode;
		getBreedingTracker(world, null).setModeName(mode.getName());
	}

	@Override
	public IBeekeepingMode getBeekeepingMode(String name) {
		for (IBeekeepingMode mode : beekeepingModes) {
			if (mode.getName().equals(name) || mode.getName().equals(name.toLowerCase(Locale.ENGLISH))) {
				return mode;
			}
		}

		Log.debug("Failed to find a beekeeping mode called '{}', reverting to fallback.", name);
		return beekeepingModes.get(0);
	}

	@Override
	public IApiaristTracker getBreedingTracker(ServerWorld world, @Nullable GameProfile player) {
		String filename = "ApiaristTracker." + (player == null ? "common" : player.getId());
		//TODO ServerWorld needed
		DimensionSavedDataManager manager = world.getSavedData();
		ApiaristTracker tracker = manager.getOrCreate(() -> new ApiaristTracker(filename), filename);


		tracker.setUsername(player);
		tracker.setWorld(world);

		return tracker;

	}

	@Override
	public IBeekeepingLogic createBeekeepingLogic(IBeeHousing housing) {
		return new BeekeepingLogic(housing);
	}

	@Override
	public IBeeModifier createBeeHousingModifier(IBeeHousing housing) {
		return new BeeHousingModifier(housing);
	}

	@Override
	public IBeeListener createBeeHousingListener(IBeeHousing housing) {
		return new BeeHousingListener(housing);
	}

	@Override
	public IAlyzerPlugin getAlyzerPlugin() {
		return BeeAlyzerPlugin.INSTANCE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IDatabasePlugin getSpeciesPlugin() {
		return BeePlugin.INSTANCE;
	}
}
