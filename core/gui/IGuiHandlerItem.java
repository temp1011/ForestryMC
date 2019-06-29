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

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
public interface IGuiHandlerItem extends IGuiHandlerForestry {
	@Nullable
	@OnlyIn(Dist.CLIENT)
	Screen getGui(PlayerEntity player, ItemStack heldItem, int data);

	@Nullable
	Container getContainer(PlayerEntity player, ItemStack heldItem, int data);
}
