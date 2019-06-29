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
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;

public class GuiAlvearySwarmer extends GuiForestryTitled<ContainerAlvearySwarmer> {
	private final TileAlvearySwarmer tile;

	public GuiAlvearySwarmer(PlayerInventory inventory, TileAlvearySwarmer tile) {
		super(Constants.TEXTURE_PATH_GUI + "/swarmer.png", new ContainerAlvearySwarmer(inventory, tile), tile);
		this.tile = tile;
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
	}
}
