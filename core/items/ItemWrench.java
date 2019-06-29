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
package forestry.core.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;

import forestry.core.config.Constants;

import buildcraft.api.tools.IToolWrench;

@Optional.Interface(iface = "buildcraft.api.tools.IToolWrench", modid = Constants.BCLIB_MOD_ID)
public class ItemWrench extends ItemForestry implements IToolWrench {

	public ItemWrench() {
		setHarvestLevel("wrench", 0);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		Block block = worldIn.getBlockState(pos).getBlock();
		if (block.rotateBlock(worldIn, pos, facing)) {
			player.swingArm(hand);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}

	@Override
	public boolean canWrench(PlayerEntity player, Hand hand, ItemStack wrench, RayTraceResult rayTrace) {
		return true;
	}

	@Override
	public void wrenchUsed(PlayerEntity player, Hand hand, ItemStack wrench, RayTraceResult rayTrace) {
	}
}
