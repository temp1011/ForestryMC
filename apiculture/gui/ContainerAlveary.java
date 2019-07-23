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

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import forestry.api.climate.IClimateListener;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.core.climate.ClimateRoot;
import forestry.core.gui.ContainerTile;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.packets.PacketGuiUpdate;

public class ContainerAlveary extends ContainerTile<TileAlveary> {

	public ContainerAlveary(PlayerInventory player, TileAlveary tile, int windowid) {	//TODO windowid
		super(tile, player, 8, 108, windowid);
		ContainerBeeHelper.addSlots(this, tile, false);

		tile.getBeekeepingLogic().clearCachedValues();
		IClimateListener listener = ClimateRoot.getInstance().getListener(tile.getWorld(), tile.getPos());
		if (listener != null && player.player instanceof ServerPlayerEntity) {
			listener.syncToClient((ServerPlayerEntity) player.player);
		}
	}

	private int beeProgress = -1;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		int beeProgress = tile.getBeekeepingLogic().getBeeProgressPercent();
		if (this.beeProgress != beeProgress) {
			this.beeProgress = beeProgress;
			IForestryPacketClient packet = new PacketGuiUpdate(tile);
			sendPacketToListeners(packet);
		}
	}
}
