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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.GuiHandler;
import forestry.core.gui.IGuiHandlerItem;

public abstract class ItemWithGui extends ItemForestry implements IGuiHandlerItem {
	public ItemWithGui() {
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isRemote) {
			openGui(playerIn);
		}

		ItemStack stack = playerIn.getHeldItem(handIn);
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	protected void openGui(PlayerEntity PlayerEntity) {
		GuiHandler.openGui(PlayerEntity, this);
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack itemstack, PlayerEntity player) {
		if (itemstack != null &&
			player instanceof ServerPlayerEntity &&
			player.openContainer instanceof ContainerItemInventory) {
			player.closeScreen();
		}

		return super.onDroppedByPlayer(itemstack, player);
	}
}
