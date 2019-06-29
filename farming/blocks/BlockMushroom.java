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
package forestry.farming.blocks;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.EnumProperty;
import net.minecraft.block.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.Feature;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.config.Constants;

public class BlockMushroom extends BushBlock implements IItemModelRegister, IGrowable {

	public static final EnumProperty<MushroomType> VARIANT = EnumProperty.create("mushroom", MushroomType.class);
	public static final PropertyBool MATURE = PropertyBool.create("mature");

	public enum MushroomType implements IStringSerializable {
		BROWN {
			@Override
			public ItemStack getDrop() {
				return new ItemStack(Blocks.BROWN_MUSHROOM);
			}
		},
		RED {
			@Override
			public ItemStack getDrop() {
				return new ItemStack(Blocks.RED_MUSHROOM);
			}
		};

		public abstract ItemStack getDrop();

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	private final Feature[] generators;

	public BlockMushroom() {
		setHardness(0.0f);
		this.generators = new Feature[]{new WorldGenBigMushroom(Blocks.BROWN_MUSHROOM_BLOCK), new WorldGenBigMushroom(Blocks.RED_MUSHROOM_BLOCK)};
		setCreativeTab(null);
		setDefaultState(this.blockState.getBaseState().with(VARIANT, MushroomType.BROWN).with(MATURE, false));
		setTickRandomly(true);
		setSoundType(SoundType.PLANT);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT, MATURE);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(VARIANT).ordinal() | ((state.getValue(MATURE) ? 1 : 0) << 2);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return getDefaultState().with(VARIANT, MushroomType.values()[meta % 2]).with(MATURE, (meta >> 2) == 1);
	}

	@Override
	public boolean getTickRandomly() {
		return true;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		MushroomType type = state.getValue(VARIANT);
		drops.add(type.getDrop());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, this.getDefaultState());
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return state.isFullBlock();
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, BlockState state) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			BlockState BlockState = worldIn.getBlockState(pos.down());
			return BlockState.getBlock() == Blocks.MYCELIUM || (BlockState.getBlock() == Blocks.DIRT && BlockState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL || worldIn.getLight(pos) < 13 && BlockState.getBlock().canSustainPlant(BlockState, worldIn, pos.down(), net.minecraft.util.Direction.UP, this));
		} else {
			return false;
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (world.isRemote || rand.nextInt(2) != 0) {
			return;
		}

		BlockState blockState = world.getBlockState(pos);
		if (!blockState.getValue(MATURE)) {
			world.setBlockState(pos, blockState.with(MATURE, true), Constants.FLAG_BLOCK_SYNC);
		} else {
			int lightValue1 = world.getLightFromNeighbors(pos.up());
			if (lightValue1 <= 7) {
				generateGiantMushroom(world, pos, blockState, rand);
			}
		}
	}

	public void generateGiantMushroom(World world, BlockPos pos, BlockState state, Random rand) {
		MushroomType type = state.getValue(VARIANT);

		world.setBlockToAir(pos);
		if (!generators[type.ordinal()].generate(world, rand, pos)) {
			world.setBlockState(pos, getStateFromMeta(type.ordinal()), 0);
		}
	}

	public void grow(World worldIn, BlockPos pos, BlockState state, Random rand) {
		this.generateGiantMushroom(worldIn, pos, state, rand);
	}

	@Override
	public void getSubBlocks(ItemGroup tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "minecraft", "brown_mushroom");
		manager.registerItemModel(item, 1, "minecraft", "red_mushroom");
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return worldIn.rand.nextFloat() < 0.45D;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, BlockState state) {
		this.grow(worldIn, pos, state, rand);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return state.getValue(VARIANT).getDrop();
	}

	@Override
	public int damageDropped(BlockState state) {
		return state.getValue(VARIANT).ordinal();
	}
}
