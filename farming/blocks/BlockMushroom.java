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

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
//import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.storage.loot.LootContext;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.config.Constants;

//TODO - figure out why this class exists
public class BlockMushroom extends BushBlock implements IItemModelRegister, IGrowable {

	public static final EnumProperty<MushroomType> VARIANT = EnumProperty.create("mushroom", MushroomType.class);
	public static final BooleanProperty MATURE = BooleanProperty.create("mature");

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
		super(Block.Properties.create(Material.PLANTS)
				.hardnessAndResistance(0.0f)
				.tickRandomly()
				.sound(SoundType.PLANT));
		this.generators = new Feature[]{Feature.HUGE_BROWN_MUSHROOM, Feature.HUGE_RED_MUSHROOM};
		//		setCreativeTab(null); TODO - done in item
		setDefaultState(this.getStateContainer().getBaseState().with(VARIANT, MushroomType.BROWN).with(MATURE, false));
	}

	//TODO - idk
	//	@Override
	//	protected BlockStateContainer createBlockState() {
	//		return new BlockStateContainer(this, VARIANT, MATURE);
	//	}

	//TODO - right method to override?
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		MushroomType type = state.get(VARIANT);
		return Lists.newArrayList(type.getDrop());
	}

	//TODO now through isValidPosition?
//	@Override
//	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
//		return super.canPlaceBlockAt(worldIn, pos) && this.isValidPosition(this.getDefaultState(), worldIn, pos);
//	}

	@Override
	protected boolean isValidGround(BlockState state, IBlockReader world, BlockPos pos) {
//		return state.isFullBlock();	//TODO VoxelShapes?
		return false;
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			BlockState blockState = worldIn.getBlockState(pos.down());
			Block block = blockState.getBlock();
			return block == Blocks.MYCELIUM || block == Blocks.PODZOL || worldIn.getLight(pos) < 13 && block.canSustainPlant(blockState, worldIn, pos.down(), net.minecraft.util.Direction.UP, this);
		} else {
			return false;
		}
	}

	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random rand) {
		if (world.isRemote || rand.nextInt(2) != 0) {
			return;
		}

		BlockState blockState = world.getBlockState(pos);
		if (!blockState.get(MATURE)) {
			world.setBlockState(pos, blockState.with(MATURE, true), Constants.FLAG_BLOCK_SYNC);
		} else {
			//TODO probably not right
			int lightValue1 = world.getLightFor(LightType.BLOCK, pos.up());
			if (lightValue1 <= 7) {
				//TODO worldgen
//				generateGiantMushroom(world, pos, blockState, rand);
			}
		}
	}

	public void generateGiantMushroom(IWorld world, ChunkGenerator<? extends GenerationSettings> gen, BlockPos pos, BlockState state, Random rand, IFeatureConfig config) {
		MushroomType type = state.get(VARIANT);

		world.removeBlock(pos, false);
		if (!generators[type.ordinal()].place(world, gen, rand, pos, config)) {//TODO worldgen .generate(world, rand, pos)) {
			world.setBlockState(pos, this.stateContainer.getBaseState().with(VARIANT, type), 0);
		}
	}

	public void grow(World worldIn, BlockPos pos, BlockState state, Random rand) {
//		this.generateGiantMushroom(worldIn, pos, state, rand);
		//TODO worldgen
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> list) {
		//TODO - blockstate into itemstack (or flatten?)
		list.add(new ItemStack(this, 1));//, 0));
		list.add(new ItemStack(this, 1));//, 1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "minecraft", "brown_mushroom");
		manager.registerItemModel(item, 1, "minecraft", "red_mushroom");
	}

	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
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
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return state.get(VARIANT).getDrop();
	}

}
