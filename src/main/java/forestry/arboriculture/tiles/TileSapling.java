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
package forestry.arboriculture.tiles;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;

import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IBreedingTracker;
import forestry.arboriculture.ModuleArboriculture;
import forestry.core.worldgen.WorldGenBase;

public class TileSapling extends TileTreeContainer {
	private int timesTicked = 0;

	public TileSapling() {
		super(ModuleArboriculture.getTiles().SAPLING);
	}

	/* SAVING & LOADING */
	@Override
	public void read(CompoundNBT compoundNBT) {
		super.read(compoundNBT);

		timesTicked = compoundNBT.getInt("TT");
	}

	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		compoundNBT = super.write(compoundNBT);

		compoundNBT.putInt("TT", timesTicked);
		return compoundNBT;
	}

	@Override
	public void onBlockTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		timesTicked++;
		tryGrow(rand, false);
	}

	private static int getRequiredMaturity(ServerWorld world, ITree tree) {
		ITreekeepingMode treekeepingMode = TreeManager.treeRoot.getTreekeepingMode(world);
		float maturationModifier = treekeepingMode.getMaturationModifier(tree.getGenome(), 1f);
		return Math.round(tree.getRequiredMaturity() * maturationModifier);
	}

	public boolean canAcceptBoneMeal(Random rand) {
		ITree tree = getTree();

		if (tree == null) {
			return false;
		}

		if (world.isRemote) {
			return false;
		}

		ServerWorld sWorld = (ServerWorld) world;

		int maturity = getRequiredMaturity(sWorld, tree);
		if (timesTicked < maturity) {
			return true;
		}

		Feature generator = tree.getTreeGenerator(world, getPos(), true);
		//		if (generator instanceof WorldGenArboriculture) {
		//			WorldGenArboriculture arboricultureGenerator = (WorldGenArboriculture) generator;
		//			arboricultureGenerator.preGenerate(world, rand, getPos());
		//			return arboricultureGenerator.getValidGrowthPos(world, getPos()) != null;
		//		} else {
		//			return true;
		//		}
		return false; //TODO worldgen
	}

	public void tryGrow(Random random, boolean bonemealed) {
		ITree tree = getTree();

		if (tree == null) {
			return;
		}

		if (world.isRemote) {
			return;
		}


		int maturity = getRequiredMaturity((ServerWorld) world, tree);
		if (timesTicked < maturity) {
			if (bonemealed) {
				timesTicked = maturity;
			}
			return;
		}

		Feature generator = tree.getTreeGenerator(world, getPos(), bonemealed);
		final boolean generated = false;
		if (generator instanceof WorldGenBase) {
			//			generated = ((WorldGenBase) generator).generate(world, random, getPos(), false);
		} else {
			//			generated = generator.generate(world, random, getPos());
		}

		if (generated) {
			IBreedingTracker breedingTracker = TreeManager.treeRoot.getBreedingTracker((ServerWorld) world, getOwnerHandler().getOwner());
			breedingTracker.registerBirth(tree);
		}
	}

}
