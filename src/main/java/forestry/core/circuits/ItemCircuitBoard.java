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
package forestry.core.circuits;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.IModelManager;
import forestry.core.ModuleCore;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemForestry;

public class ItemCircuitBoard extends ItemForestry implements IColoredItem {

	public ItemCircuitBoard() {
//		setHasSubtypes(true); TODO - flatten?
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (this.isInGroup(tab)) {
			subItems.add(createCircuitboard(EnumCircuitBoardType.BASIC, null, new ICircuit[]{}));
			subItems.add(createCircuitboard(EnumCircuitBoardType.ENHANCED, null, new ICircuit[]{}));
			subItems.add(createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[]{}));
			subItems.add(createCircuitboard(EnumCircuitBoardType.INTRICATE, null, new ICircuit[]{}));
		}
	}

	/* MODELS*/
	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < 4; i++) {
			manager.registerItemModel(item, i, "chipsets");
		}
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean shouldSyncTag() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColorFromItemstack(ItemStack itemstack, int tintIndex) {
		EnumCircuitBoardType type = EnumCircuitBoardType.values()[0];//TODO flatten itemstack.getItemDamage()];
		if (tintIndex == 0) {
			return type.getPrimaryColor();
		} else {
			return type.getSecondaryColor();
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		EnumCircuitBoardType type = EnumCircuitBoardType.values()[0];//TODO flatten stack.getItemDamage()];
		return "item.for.circuitboard." + type.toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);
		ICircuitBoard circuitboard = null;//TODO init order ChipsetManager.circuitRegistry.getCircuitBoard(itemstack);
		if (circuitboard != null) {
			circuitboard.addTooltip(list);
		}
	}

	public static ItemStack createCircuitboard(EnumCircuitBoardType type, @Nullable ICircuitLayout layout, ICircuit[] circuits) {
		CompoundNBT compoundNBT = new CompoundNBT();
		new CircuitBoard(type, layout, circuits).write(compoundNBT);
		return new ItemStack(ModuleCore.getItems().circuitboards, 1, compoundNBT);
	}

	public ItemStack get(EnumCircuitBoardType type) {
		return new ItemStack(this, 1);//TODO flatten , type.ordinal());
	}
}
