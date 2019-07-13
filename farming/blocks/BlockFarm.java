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

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.EnumProperty;
import net.minecraft.block.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.core.IModelManager;
import forestry.api.core.ItemGroups;
import forestry.core.blocks.BlockStructure;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.farming.models.EnumFarmBlockTexture;
import forestry.farming.tiles.TileFarm;
import forestry.farming.tiles.TileFarmControl;
import forestry.farming.tiles.TileFarmGearbox;
import forestry.farming.tiles.TileFarmHatch;
import forestry.farming.tiles.TileFarmPlain;
import forestry.farming.tiles.TileFarmValve;

public class BlockFarm extends BlockStructure {

	public static final EnumProperty<EnumFarmBlockType> META = EnumProperty.create("meta", EnumFarmBlockType.class);

	public BlockFarm() {
		super(Material.ROCK);
		setHardness(1.0f);
		setHarvestLevel("pickaxe", 0);
		setDefaultState(blockState.getBaseState().with(META, EnumFarmBlockType.PLAIN));
		setCreativeTab(ItemGroups.tabAgriculture);
	}


	@Override
	public BlockState getStateFromMeta(int meta) {
		return getDefaultState().with(META, EnumFarmBlockType.VALUES[meta]);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(META).ordinal();
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).with(UnlistedBlockPos.POS, pos)
			.with(UnlistedBlockAccess.BLOCKACCESS, world);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{META},
			new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
	}

	@Override
	public void getSubBlocks(ItemGroup tab, NonNullList<ItemStack> list) {
		for (int i = 0; i < 6; i++) {
			if (i == 1) {
				continue;
			}

			for (EnumFarmBlockTexture block : EnumFarmBlockTexture.values()) {
				ItemStack stack = new ItemStack(this, 1, i);
				CompoundNBT compound = new CompoundNBT();
				block.saveToCompound(compound);
				stack.setTagCompound(compound);
				list.add(stack);
			}
		}
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		List<ItemStack> drops = getDrops(world, pos, world.getBlockState(pos), 0);
		if (drops.isEmpty()) {
			return super.getPickBlock(state, target, world, pos, player);
		}
		return drops.get(0);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		if (stack.getTag() == null) {
			return;
		}

		TileFarm tile = TileUtil.getTile(world, pos, TileFarm.class);
		if (tile != null) {
			tile.setFarmBlockTexture(EnumFarmBlockTexture.getFromCompound(stack.getTag()));
		}
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		if (!world.isRemote && canHarvestBlock(world, pos, player)) {
			List<ItemStack> drops = getDrops(world, pos, world.getBlockState(pos), 0);
			for (ItemStack drop : drops) {
				ItemStackUtil.dropItemStackAsEntity(drop, world, pos);
			}
		}
		return world.setBlockToAir(pos);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		int meta = getMetaFromState(state);
		TileUtil.actOnTile(world, pos, TileFarm.class, farm -> {
			ItemStack stack = new ItemStack(this, 1, meta != 1 ? meta : 0);
			CompoundNBT compound = new CompoundNBT();
			farm.getFarmBlockTexture().saveToCompound(compound);
			stack.setTagCompound(compound);
			drops.add(stack);
		});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch (meta) {
			case 2:
				return new TileFarmGearbox();
			case 3:
				return new TileFarmHatch();
			case 4:
				return new TileFarmValve();
			case 5:
				return new TileFarmControl();
			default:
				return new TileFarmPlain();
		}
	}

	/* MODELS */
	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < 6; i++) {
			if (i == 1) {
				continue;
			}
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation("forestry:ffarm", "inventory"));
		}
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
		return state.getValue(META) == EnumFarmBlockType.CONTROL;
	}

	public ItemStack get(EnumFarmBlockType type, int amount) {
		return new ItemStack(this, amount, type.ordinal());
	}
}
