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

import javax.annotation.Nullable;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;

import forestry.apiculture.entities.MinecartEntityBeeHousingBase;
import forestry.core.gui.ContainerAnalyzerProviderHelper;
import forestry.core.gui.ContainerEntity;
import forestry.core.gui.slots.SlotLockable;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.packets.PacketGuiUpdateEntity;

public class ContainerMinecartBeehouse extends ContainerEntity<MinecartEntityBeeHousingBase> implements IContainerBeeHousing {
	/* Attributes - Final*/
	private final ContainerAnalyzerProviderHelper providerHelper;

	public ContainerMinecartBeehouse(PlayerInventory player, MinecartEntityBeeHousingBase entity, boolean hasFrames) {
		super(entity, player, 8, 108);
		providerHelper = new ContainerAnalyzerProviderHelper(this, player);

		ContainerBeeHelper.addSlots(this, entity, hasFrames);

		entity.getBeekeepingLogic().clearCachedValues();
	}

	private int beeProgress = -1;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		int beeProgress = entity.getBeekeepingLogic().getBeeProgressPercent();
		if (this.beeProgress != beeProgress) {
			this.beeProgress = beeProgress;
			IForestryPacketClient packet = new PacketGuiUpdateEntity(entity, entity);
			sendPacketToListeners(packet);
		}
	}

	/* Methods - Implement IContainerAnalyzerProvider */
	@Nullable
	public Slot getAnalyzerSlot() {
		return providerHelper.getAnalyzerSlot();
	}

	/* Methods - Implement ContainerForestry */
	@Override
	protected void addSlot(PlayerInventory playerInventory, int slot, int x, int y) {
		addSlotToContainer(new SlotLockable(playerInventory, slot, x, y));
	}

	@Override
	protected void addHotbarSlot(PlayerInventory playerInventory, int slot, int x, int y) {
		addSlotToContainer(new SlotLockable(playerInventory, slot, x, y));
	}

	/* Methods - Implement IGuiSelectable */
	@Override
	public void handleSelectionRequest(ServerPlayerEntity player, int primary, int secondary) {
		providerHelper.analyzeSpecimen(secondary);
	}

}
