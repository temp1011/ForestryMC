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
package forestry.apiculture.blocks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IHiveDrop;
import forestry.api.apiculture.IHiveTile;
import forestry.api.apiculture.hives.IHiveRegistry.HiveType;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.ItemGroups;
import forestry.apiculture.MaterialBeehive;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.tiles.TileHive;
import forestry.core.blocks.IBlockWithMeta;
import forestry.core.tiles.TileUtil;

public class BlockBeeHives extends ContainerBlock implements IItemModelRegister, IBlockWithMeta {
	private static final EnumProperty<HiveType> HIVE_TYPES = EnumProperty.create("hive", HiveType.class);

	public BlockBeeHives() {
		super(Properties.create(MaterialBeehive.BEEHIVE_WORLD)
				.lightValue((int) (0.4f * 15))    //TODO - correct?
				.hardnessAndResistance(2.5f));
		//		setCreativeTab(ItemGroups.tabApiculture); TODO done in item
		//		setHarvestLevel("scoop", 0); TODO harvest level, addToolType in item?
		setDefaultState(this.getStateContainer().getBaseState().with(HIVE_TYPES, HiveType.FOREST));
	}
	//
	//	@Override
	//	protected BlockStateContainer createBlockState() {
	//		return new BlockStateContainer(this, HIVE_TYPES);
	//	}

	public BlockState getStateForType(HiveType type) {
		return getDefaultState().with(HIVE_TYPES, type);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileHive();
	}

	@Override
	public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		super.onBlockClicked(state, world, pos, player);
		TileUtil.actOnTile(world, pos, IHiveTile.class, tile -> tile.onAttack(world, pos, player));
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		boolean canHarvest = canHarvestBlock(state, world, pos, player);
		TileUtil.actOnTile(world, pos, IHiveTile.class, tile -> tile.onBroken(world, pos, player, canHarvest));
	}

	//TODO loot table drops things
	//	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		Random random = world instanceof World ? ((World) world).rand : RANDOM;

		List<IHiveDrop> hiveDrops = getDropsForHive(0);//TODO flatten getMetaFromState(state));
		Collections.shuffle(hiveDrops);

		// Grab a princess
		int tries = 0;
		boolean hasPrincess = false;
		while (tries <= 10 && !hasPrincess) {
			tries++;

			for (IHiveDrop drop : hiveDrops) {
				if (random.nextDouble() < drop.getChance(world, pos, fortune)) {
					IBee bee = drop.getBeeType(world, pos);
					if (random.nextFloat() < drop.getIgnobleChance(world, pos, fortune)) {
						bee.setIsNatural(false);
					}

					ItemStack princess = BeeManager.beeRoot.getMemberStack(bee, EnumBeeType.PRINCESS);
					drops.add(princess);
					hasPrincess = true;
					break;
				}
			}
		}

		// Grab drones
		for (IHiveDrop drop : hiveDrops) {
			if (random.nextDouble() < drop.getChance(world, pos, fortune)) {
				IBee bee = drop.getBeeType(world, pos);
				ItemStack drone = BeeManager.beeRoot.getMemberStack(bee, EnumBeeType.DRONE);
				drops.add(drone);
				break;
			}
		}

		// Grab anything else on offer
		for (IHiveDrop drop : hiveDrops) {
			if (random.nextDouble() < drop.getChance(world, pos, fortune)) {
				drops.addAll(drop.getExtraItems(world, pos, fortune));
				break;
			}
		}
	}

	// / CREATIVE INVENTORY
	//	@Override
	//	public int damageDropped(BlockState state) {
	//		return getMetaFromState(state);
	//	}

	private static List<IHiveDrop> getDropsForHive(int meta) {
		String hiveName = getHiveNameForMeta(meta);
		if (hiveName == null || hiveName.equals(HiveType.SWARM.getHiveUid())) {
			return Collections.emptyList();
		}
		return ModuleApiculture.getHiveRegistry().getDrops(hiveName);
	}

	@Nullable
	private static String getHiveNameForMeta(int meta) {
		if (meta < 0 || meta >= HiveType.VALUES.length) {
			return null;
		}
		return HiveType.VALUES[meta].getHiveUid();
	}

	public static HiveType getHiveType(BlockState state) {
		return state.get(HIVE_TYPES);
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> list) {
		for (BlockState blockState : this.getStateContainer().getValidStates()) {
			if (getHiveType(blockState) != HiveType.SWARM) {
				list.add(new ItemStack(this, 1));//TODO flatten , meta));
			}
		}
	}

	/* ITEM MODELS */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		for (HiveType hiveType : HiveType.VALUES) {
			manager.registerItemModel(item, hiveType.getMeta(), "beehives/" + hiveType.getName());
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public String getNameFromMeta(int meta) {
		return HiveType.VALUES[meta].getName();
	}

	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}
}
