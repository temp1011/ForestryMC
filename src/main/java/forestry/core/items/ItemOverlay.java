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

import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.core.IModelManager;
import forestry.core.config.Config;

//TODO needs refactoring for the flattened world
public class ItemOverlay extends ItemForestry implements IColoredItem {

	public interface IOverlayInfo {
		String getUid();

		int getPrimaryColor();

		int getSecondaryColor();

		boolean isSecret();
	}

	protected final IOverlayInfo[] overlays;

	public ItemOverlay(ItemGroup tab, IOverlayInfo[] overlays) {
		super((new Item.Properties()).maxDamage(0)
		.group(tab)
		.setNoRepair());
//		setHasSubtypes(true); TODO flatten?

		this.overlays = overlays;
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (this.isInGroup(tab)) {
			for (int i = 0; i < overlays.length; i++) {
				if (Config.isDebug || !overlays[i].isSecret()) {
//					subItems.add(new ItemStack(this, 1, i)); TODO flatten
				}
			}
		}
	}

	/* Models */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < overlays.length; i++) {
			manager.registerItemModel(item, i);
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
//		if (stack.getItemDamage() < 0 || stack.getItemDamage() >= overlays.length) {
//			return super.getTranslationKey(stack);
//		}
//
//		return super.getTranslationKey(stack) + "." + overlays[stack.getItemDamage()].getUid();
		return super.getTranslationKey(stack); //TODO flatten
	}

	@Override
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
		int meta = -1;//TODO flatten stack.getMetadata();
		if (meta < 0 || meta >= overlays.length) {
			return 0xffffff;
		}

		IOverlayInfo overlayInfo = overlays[meta];
		if (tintIndex == 0 || overlayInfo.getSecondaryColor() == 0) {
			return overlayInfo.getPrimaryColor();
		} else {
			return overlayInfo.getSecondaryColor();
		}
	}
}
