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

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.EnumHelper;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.arboriculture.ArboricultureCapabilities;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.apiculture.ModuleApiculture;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;

public class ItemArmorApiarist extends ArmorItem implements IItemModelRegister {

	public static final ArmorMaterial APIARIST_ARMOR = EnumHelper.addArmorMaterial("APIARIST_ARMOR", "forestry:textures/items", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F)
		.setRepairItem(ModuleCore.getItems().craftingMaterial.getWovenSilk());

	public ItemArmorApiarist(EquipmentSlotType equipmentSlotIn) {
		super(APIARIST_ARMOR, 0, equipmentSlotIn);
//		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		if (stack != null && stack.getItem() == ModuleApiculture.getItems().apiaristLegs) {
			return Constants.MOD_ID + ":" + Constants.TEXTURE_APIARIST_ARMOR_SECONDARY;
		} else {
			return Constants.MOD_ID + ":" + Constants.TEXTURE_APIARIST_ARMOR_PRIMARY;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}

	@Override
	public boolean hasColor(ItemStack itemstack) {
		return false;
	}

	@Override
	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ICapabilityProvider() {
			@Override
			public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
				if (capability == ArboricultureCapabilities.ARMOR_NATURALIST) {
					return armorType == EquipmentSlotType.HEAD;
				}
				return capability == ApicultureCapabilities.ARMOR_APIARIST;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
				if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
					return capability.getDefaultInstance();
				} else if (capability == ArboricultureCapabilities.ARMOR_NATURALIST &&
					armorType == EquipmentSlotType.HEAD) {
					return capability.getDefaultInstance();
				}
				return null;
			}
		};
	}
}
