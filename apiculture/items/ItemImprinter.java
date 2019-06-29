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
package forestry.apiculture.items;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.core.Tabs;
import forestry.apiculture.gui.ContainerImprinter;
import forestry.apiculture.gui.GuiImprinter;
import forestry.apiculture.inventory.ItemInventoryImprinter;
import forestry.core.items.ItemWithGui;

public class ItemImprinter extends ItemWithGui {
	public ItemImprinter() {
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ContainerScreen getGui(PlayerEntity player, ItemStack heldItem, int data) {
		return new GuiImprinter(player.inventory, new ItemInventoryImprinter(player, heldItem));
	}

	@Override
	public Container getContainer(PlayerEntity player, ItemStack heldItem, int data) {
		return new ContainerImprinter(player.inventory, new ItemInventoryImprinter(player, heldItem));
	}
}
