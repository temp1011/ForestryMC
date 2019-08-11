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
package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.item.ItemStack;

public interface IGuiHandlerItem extends IGuiHandlerForestry, IContainerProvider {	//TODO INamedContainerProvider?
//	@Nullable	TODO inner class for ContainerProvider then register gui as normal.
//	@OnlyIn(Dist.CLIENT)
//	Screen getGui(PlayerEntity player, ItemStack heldItem, int data);

	@Nullable
	Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem);

	//TODO inline?
	@Nullable
	@Override
	default Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return getContainer(windowId, playerEntity, playerEntity.getHeldItem(playerEntity.getActiveHand()));
	}
}
