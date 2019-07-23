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
package forestry.apiculture.gui;

import net.minecraft.entity.player.PlayerInventory;

import forestry.apiculture.inventory.InventoryHygroregulator;
import forestry.apiculture.multiblock.TileAlvearyHygroregulator;
import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotLiquidIn;

public class ContainerAlvearyHygroregulator extends ContainerLiquidTanks<TileAlvearyHygroregulator> {

	public ContainerAlvearyHygroregulator(PlayerInventory playerInventory, TileAlvearyHygroregulator tile, int id) {	//TODO windowid
		super(tile, playerInventory, 8, 84, id);

		this.addSlot(new SlotLiquidIn(tile, InventoryHygroregulator.SLOT_INPUT, 56, 38));
	}

}
