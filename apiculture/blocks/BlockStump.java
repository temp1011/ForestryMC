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

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.tiles.TileCandle;
import forestry.core.config.Constants;

public class BlockStump extends TorchBlock implements IItemModelRegister {

	public BlockStump() {
		this.setHardness(0.0F);
		this.setSoundType(SoundType.WOOD);
		setCreativeTab(Tabs.tabApiculture);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "stump");
	}

	@Override
	public void getSubBlocks(ItemGroup tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, 0));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if (BlockCandle.lightingItems.contains(heldItem.getItem())) {
			BlockState activatedState = ModuleApiculture.getBlocks().candle.getDefaultState().with(BlockCandle.STATE, BlockCandle.State.ON);
			worldIn.setBlockState(pos, activatedState, Constants.FLAG_BLOCK_SYNC);
			TileCandle tc = new TileCandle();
			tc.setColour(16777215); // default to white
			tc.setLit(true);
			worldIn.setTileEntity(pos, tc);
			return true;
		}

		return false;
	}

	@Override
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
	}
}
