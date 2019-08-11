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

import net.minecraft.inventory.container.INamedContainerProvider;


import forestry.api.core.ILocatable;

public interface IGuiHandlerTile extends IGuiHandlerForestry, ILocatable, INamedContainerProvider {
//	@Nullable
//	Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player);

//	//TODO inline this
//	@Nullable
//	@Override
//	default Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
//		return getContainer(windowId, playerInventory, playerEntity);
//	}
}
