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
package forestry.core.items;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.ItemGroups;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.gui.GuiAlyzer;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.utils.Translator;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

public class ItemAlyzer extends ItemWithGui {
	public ItemAlyzer() {
		super((new Item.Properties())
		.group(ItemGroups.tabApiculture));
	}

	@Override
	public void openGui(PlayerEntity PlayerEntity) {
		super.openGui(PlayerEntity);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ContainerScreen getGui(PlayerEntity player, ItemStack heldItem, int data) {
		return new GuiAlyzer(player, new ItemInventoryAlyzer(player, heldItem), data);	//TODO windowid
	}

	@Override
	public Container getContainer(PlayerEntity player, ItemStack heldItem, int data) {
		return new ContainerAlyzer(new ItemInventoryAlyzer(player, heldItem), player, data);	//TODO windowid
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		int charges = 0;
		CompoundNBT compound = stack.getTag();
		if (compound != null) {
			charges = compound.getInt("Charges");
		}
		tooltip.add(new TranslationTextComponent(stack.getTranslationKey() + ".charges", charges).setStyle((new Style()).setColor(TextFormatting.GOLD)));
	}
}
