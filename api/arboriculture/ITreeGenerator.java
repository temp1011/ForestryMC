/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.authlib.GameProfile;

import forestry.api.world.ITreeGenData;

/**
 * Implements the tree generation for a tree species.
 */
public interface ITreeGenerator {
	Feature getWorldGenerator(ITreeGenData tree);

	boolean setLogBlock(ITreeGenome genome, World world, BlockPos pos, Direction facing);

	boolean setLeaves(ITreeGenome genome, World world, @Nullable GameProfile owner, BlockPos pos, Random rand);
}
