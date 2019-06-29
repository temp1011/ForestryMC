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

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.core.ILocatable;

public interface IGuiHandlerTile extends IGuiHandlerForestry, ILocatable {
	@Nullable
	@OnlyIn(Dist.CLIENT)
	ContainerScreen getGui(PlayerEntity player, int data);

	@Nullable
	Container getContainer(PlayerEntity player, int data);
}
