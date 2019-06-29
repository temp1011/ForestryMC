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
package forestry.core.tiles;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.blocks.BlockBase;
import forestry.core.gui.GuiHandler;

public abstract class TileBase extends TileForestry {

	public void openGui(PlayerEntity player, ItemStack heldItem) {
		GuiHandler.openGui(player, this);
	}

	@Override
	public String getUnlocalizedTitle() {
		Block block = getBlockType();
		if (block instanceof BlockBase) {
			return block.getTranslationKey() + ".name";
		}
		return super.getUnlocalizedTitle();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState) {
		Block oldBlock = oldState.getBlock();
		Block newBlock = newState.getBlock();
		return oldBlock != newBlock || !(oldBlock instanceof BlockBase) || !(newBlock instanceof BlockBase);
	}

	@Nonnull
	public Direction getFacing() {
		return getWorld().getBlockState(getPos()).getValue(BlockBase.FACING);
	}

}
