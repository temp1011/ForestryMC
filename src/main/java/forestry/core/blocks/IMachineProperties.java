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
package forestry.core.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IModelManager;
import forestry.core.tiles.TileForestry;

public interface IMachineProperties<T extends TileForestry> extends IStringSerializable {
	Class<T> getTeClass();

	/**
	 * Registers the tile entity with MC.
	 */
	void registerTileEntity();

	@OnlyIn(Dist.CLIENT)
	void registerModel(Item item, IModelManager manager);

	TileEntity createTileEntity();

	void setBlock(Block block);

	@Nullable
	Block getBlock();

	boolean isFullCube(BlockState state);

	AxisAlignedBB getBoundingBox(IBlockReader world, BlockPos pos, BlockState state);

	@Nullable
	RayTraceResult collisionRayTrace(World world, BlockPos pos, BlockState state, Vec3d startVec, Vec3d endVec);
}
