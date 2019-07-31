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
package forestry.energy.blocks;

import com.google.common.collect.ImmutableList;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import forestry.core.blocks.BlockBase;
import forestry.core.tiles.TileEngine;
import forestry.core.tiles.TileUtil;
import forestry.energy.EnergyHelper;
import forestry.energy.EnergyManager;

public class BlockEngine extends BlockBase<BlockTypeEngine> {
	private static final EnumMap<Direction, List<AxisAlignedBB>> boundingBoxesForDirections = new EnumMap<>(Direction.class);

	static {
		boundingBoxesForDirections.put(Direction.DOWN, ImmutableList.of(
			new AxisAlignedBB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0), new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.5, 0.75)
		));
		boundingBoxesForDirections.put(Direction.UP, ImmutableList.of(
			new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0), new AxisAlignedBB(0.25, 0.5, 0.25, 0.75, 1.0, 0.75)
		));
		boundingBoxesForDirections.put(Direction.NORTH, ImmutableList.of(
			new AxisAlignedBB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0), new AxisAlignedBB(0.25, 0.25, 0.0, 0.75, 0.75, 0.5)
		));
		boundingBoxesForDirections.put(Direction.SOUTH, ImmutableList.of(
			new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5), new AxisAlignedBB(0.25, 0.25, 0.5, 0.75, 0.75, 1.0)
		));
		boundingBoxesForDirections.put(Direction.WEST, ImmutableList.of(
			new AxisAlignedBB(0.5, 0.0, 0.0, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.25, 0.25, 0.5, 0.75, 0.75)
		));
		boundingBoxesForDirections.put(Direction.EAST, ImmutableList.of(
			new AxisAlignedBB(0.0, 0.0, 0.0, 0.5, 1.0, 1.0), new AxisAlignedBB(0.5, 0.25, 0.25, 1.0, 0.75, 0.75)
		));
	}

	public BlockEngine(BlockTypeEngine blockType) {
		super(blockType);
//		setHarvestLevel("pickaxe", 0);	//TODO setToolType in item?
	}

	//TODO raytracing
//	@Override
//	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
//		Direction orientation = state.getComb(FACING);
//		List<AxisAlignedBB> boundingBoxes = boundingBoxesForDirections.getComb(orientation);
//		if (boundingBoxes == null) {
//			return;
//		}
//
//		for (AxisAlignedBB boundingBoxBase : boundingBoxes) {
//			AxisAlignedBB boundingBox = boundingBoxBase.offset(pos.getX(), pos.getY(), pos.getZ());
//			if (entityBox.intersects(boundingBox)) {
//				collidingBoxes.add(boundingBox);
//			}
//		}
//	}

	//TODO potentially getRayTraceResult
//	@Override
//	public RayTraceResult collisionRayTrace(BlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
//		Direction orientation = blockState.getComb(FACING);
//		List<AxisAlignedBB> boundingBoxes = boundingBoxesForDirections.getComb(orientation);
//		if (boundingBoxes == null) {
//			return super.collisionRayTrace(blockState, worldIn, pos, start, end);
//		}
//
//		RayTraceResult nearestIntersection = null;
//		for (AxisAlignedBB boundingBoxBase : boundingBoxes) {
//			AxisAlignedBB boundingBox = boundingBoxBase.offset(pos.getX(), pos.getY(), pos.getZ());
//			RayTraceResult intersection = boundingBox.calculateIntercept(start, end);
//			if (intersection != null) {
//				if (nearestIntersection == null || intersection.hitVec.distanceTo(start) < nearestIntersection.hitVec.distanceTo(start)) {
//					nearestIntersection = intersection;
//				}
//			}
//		}
//
//		if (nearestIntersection != null) {
//			//TODO needs to be entityRayTrace here
//			Object hitInfo = nearestIntersection.hitInfo;
//			Entity entityHit = nearestIntersection.entityHit;
//			nearestIntersection = new RayTraceResult(nearestIntersection.typeOfHit, nearestIntersection.hitVec, nearestIntersection.sideHit, pos);
//			nearestIntersection.hitInfo = hitInfo;
//			nearestIntersection.entityHit = entityHit;
//		}
//
//		return nearestIntersection;
//	}

	@Override
	public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation rot) {
		return rotate(world, pos);	//TODO check
	}

	private static boolean isOrientedAtEnergyReciever(IWorld world, BlockPos pos, Direction orientation) {
		BlockPos offsetPos = pos.offset(orientation);
		TileEntity tile = TileUtil.getTile(world, offsetPos);
		return EnergyHelper.isEnergyReceiverOrEngine(orientation.getOpposite(), tile);
	}

	private static BlockState rotate(IWorld world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		Direction blockFacing = blockState.get(FACING);
		for (int i = blockFacing.ordinal() + 1; i <= blockFacing.ordinal() + 6; ++i) {
			Direction orientation = Direction.values()[i % 6];
			if (isOrientedAtEnergyReciever(world, pos, orientation)) {
				BlockState newState = blockState.with(FACING, orientation);
				if(world.setBlockState(pos, blockState, 3)) {//;	//TODO flags
					return newState;
				}
			}
		}
		return blockState;
	}

	@Override
	public void rotateAfterPlacement(PlayerEntity player, World world, BlockPos pos, Direction side) {
		Direction orientation = side.getOpposite();
		if (isOrientedAtEnergyReciever(world, pos, orientation)) {
			BlockState blockState = world.getBlockState(pos);
			blockState = blockState.with(FACING, orientation);
			world.setBlockState(pos, blockState);
		} else {
			super.rotateAfterPlacement(player, world, pos, side);
			rotate(world, pos);
		}
	}

	//TODO voxelShapes?
//	@Override
//	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
//		BlockState blockState = world.getBlockState(pos);
//		Direction facing = blockState.getComb(BlockBase.FACING);
//		return facing.getOpposite() == side;
//	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		TileEngine tileEngine = TileUtil.getTile(worldIn, pos, TileEngine.class);
		if (tileEngine != null) {
			EnergyManager energyManager = tileEngine.getEnergyManager();
			return energyManager.calculateRedstone();
		}
		return 0;
	}
}
