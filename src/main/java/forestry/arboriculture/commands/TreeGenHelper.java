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
package forestry.arboriculture.commands;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.genetics.AlleleManager;
import forestry.core.commands.SpeciesNotFoundException;
import forestry.core.utils.BlockUtil;
import forestry.core.worldgen.WorldGenBase;

public final class TreeGenHelper {

	public static Feature getWorldGen(String treeName, PlayerEntity player, BlockPos pos) throws SpeciesNotFoundException {
		IGenome treeGenome = getTreeGenome(treeName);
		ITree tree = TreeManager.treeRoot.getTree(player.world, treeGenome);
		return tree.getTreeGenerator(player.world, pos, true);
	}

	public static boolean generateTree(Feature gen, World world, BlockPos pos) {
		if (pos.getY() > 0 && world.isAirBlock(pos.down())) {
			pos = BlockUtil.getNextSolidDownPos(world, pos);
		} else {
			pos = BlockUtil.getNextReplaceableUpPos(world, pos);
		}
		if (pos == null) {
			return false;
		}

		BlockState blockState = world.getBlockState(pos);
		if (BlockUtil.canPlaceTree(blockState, world, pos)) {
			if (gen instanceof WorldGenBase) {
				return ((WorldGenBase) gen).generate(world, world.rand, pos, true);
			} else {
				return gen.generate(world, world.rand, pos);
			}
		}
		return false;
	}

	public static boolean generateTree(ITree tree, World world, BlockPos pos) {
		Feature gen = tree.getTreeGenerator(world, pos, true);

		BlockState blockState = world.getBlockState(pos);
		if (BlockUtil.canPlaceTree(blockState, world, pos)) {
			if (gen instanceof WorldGenBase) {
				return ((WorldGenBase) gen).generate(world, world.rand, pos, true);
			} else {
				return gen.generate(world, world.rand, pos);
			}
		}
		return false;
	}

	private static IGenome getTreeGenome(String speciesName) throws SpeciesNotFoundException {
		IAlleleTreeSpecies species = null;

		for (String uid : AlleleManager.alleleRegistry.getRegisteredAlleles().keySet()) {

			if (!uid.equals(speciesName)) {
				continue;
			}

			IAllele allele = AlleleManager.alleleRegistry.getAllele(uid);
			if (allele instanceof IAlleleTreeSpecies) {
				species = (IAlleleTreeSpecies) allele;
				break;
			}
		}

		if (species == null) {
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
				if (allele instanceof IAlleleTreeSpecies && allele.getAlleleName().replaceAll("\\s", "").equals(speciesName)) {
					species = (IAlleleTreeSpecies) allele;
					break;
				}
			}
		}

		if (species == null) {
			throw new SpeciesNotFoundException(speciesName);
		}

		IAllele[] template = TreeManager.treeRoot.getTemplate(species);

		return TreeManager.treeRoot.templateAsGenome(template);
	}
}
