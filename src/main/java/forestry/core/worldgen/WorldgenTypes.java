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
package forestry.core.worldgen;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.BiomeDictionary;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import forestry.core.config.LocalizedConfiguration;

public enum WorldgenTypes {
	BEEHIVES,
	TREES,
	ORE;

	private boolean enabled;
	private float frequency;
	private Set<Integer> whitelistedDims = new HashSet<>();
	private Set<Integer> blacklistedDims = new HashSet<>();
	private Set<BiomeDictionary.Type> blacklistedBiomeTypes = new HashSet<>();
	private Set<Biome> blacklistedBiomes = new HashSet<>();

	public boolean isEnabled() {
		return enabled || frequency == 0;
	}

	public float getFrequency() {
		return frequency;
	}

	/**
	 * The dimension blacklist has priority over the whitelist: if anything is set in the blacklist then
	 * the whitelist won't be used
	 */
	public boolean isValidDim(int dim) {
		if (!isEnabled()) {
			return false;
		}
		if (blacklistedDims.isEmpty() || !blacklistedDims.contains(dim)) {
			return whitelistedDims.isEmpty() || whitelistedDims.contains(dim);
		}
		return false;
	}

	public boolean isValidDim(World world) {
		return isValidDim(world.provider.getDimension());
	}

	public boolean isValidBiome(Biome biome) {
		if (!isEnabled()) {
			return false;
		}
		if (blacklistedBiomes.contains(biome)) {
			return false;
		}
		return BiomeDictionary.getTypes(biome).stream().noneMatch(blacklistedBiomeTypes::contains);
	}

	public boolean isValidBiome(IBlockAccess world, BlockPos pos) {
		return isValidBiome(world.getBiome(pos));
	}

	public void blacklistDim(int dim) {
		blacklistedDims.add(dim);
	}

	public static void load(LocalizedConfiguration config) {
		config.addCategoryCommentLocalized("world.generate");
		for (WorldgenTypes worldgenType : WorldgenTypes.values()) {

			String name = worldgenType.toString().toLowerCase();
			String category = "world.generate." + name;

			worldgenType.enabled = config.getBooleanLocalized(category, "enabled", true);
			worldgenType.frequency = config.getFloatLocalized(category, "frequency", 1.0f, 0.0F, 10.0F);

			for (int dimId : config.get(category, "dimBlacklist", new int[0]).getIntList()) {
				worldgenType.blacklistedDims.add(dimId);
			}
			for (int dimId : config.get(category, "dimWhitelist", new int[0]).getIntList()) {
				worldgenType.whitelistedDims.add(dimId);
			}

			for (String entry : config.get(category, "biomeBlacklist", new String[0]).getStringList()) {
				BiomeDictionary.Type biomeType = BiomeDictionary.Type.getType(entry);
				Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(entry));
				if (biomeType != null) {
					worldgenType.blacklistedBiomeTypes.add(biomeType);
				} else if (biome != null) {
					worldgenType.blacklistedBiomes.add(biome);
				}
			}
		}
	}

}
