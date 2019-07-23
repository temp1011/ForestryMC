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

import forestry.apiculture.multiblock.TileAlvearySwarmer;
import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;

public class ContainerAlvearySwarmer extends ContainerTile<TileAlvearySwarmer> {

	public ContainerAlvearySwarmer(PlayerInventory player, TileAlvearySwarmer tile, int id) {
		super(tile, player, 8, 87, id);	//TODO windowid

		this.addSlot(new SlotFiltered(tile, 0, 79, 52));
		this.addSlot(new SlotFiltered(tile, 1, 100, 39));
		this.addSlot(new SlotFiltered(tile, 2, 58, 39));
		this.addSlot(new SlotFiltered(tile, 3, 79, 26));
	}
}
