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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.IInventory;

import forestry.core.gui.slots.SlotFilteredInventory;
import forestry.core.tiles.IFilterSlotDelegate;
import forestry.core.tiles.TileNaturalistChest;

public class ContainerNaturalistInventory extends ContainerTile<TileNaturalistChest> implements IGuiSelectable {

	public ContainerNaturalistInventory(PlayerInventory player, TileNaturalistChest tile, int page, int id) {	//TODO windowid
		super(tile, player, 18, 120, id);

		addInventory(this, tile, page);
	}

	public static <T extends IInventory & IFilterSlotDelegate> void addInventory(ContainerForestry container, T inventory, int selectedPage) {
		for (int page = 0; page < 5; page++) {
			for (int x = 0; x < 5; x++) {
				for (int y = 0; y < 5; y++) {
					int slot = y + page * 25 + x * 5;
					if (page == selectedPage) {
						container.addSlot(new SlotFilteredInventory(inventory, slot, 100 + y * 18, 21 + x * 18));
					} else {
						container.addSlot(new SlotFilteredInventory(inventory, slot, -10000, -10000));
					}
				}
			}
		}
	}

	@Override
	public void handleSelectionRequest(ServerPlayerEntity player, int primary, int secondary) {
		tile.flipPage(player, (short) primary);
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);

		tile.increaseNumPlayersUsing();
	}

	@Override
	public void onContainerClosed(PlayerEntity player) {
		super.onContainerClosed(player);
		tile.decreaseNumPlayersUsing();
	}
}
