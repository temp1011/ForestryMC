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

import java.util.Locale;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.registries.ForgeRegistries;

import forestry.core.proxy.Proxies;
import forestry.core.utils.Log;

//import net.minecraftforge.oredict.OreDictionary;

public abstract class ItemRegistry {
	protected static <T extends Item> T registerItem(T item, String name) {

		if (!name.equals(name.toLowerCase(Locale.ENGLISH))) {
			Log.error("Name must be lowercase");
		}

		//TODO - now done by name??
//		item.setTranslationKey("for." + name);
		item.setRegistryName(name);


		ForgeRegistries.ITEMS.register(item);
		Proxies.common.registerItem(item);
		return item;
	}

	protected static void registerOreDict(String oreDictName, ItemStack itemStack) {
//		OreDictionary.registerOre(oreDictName, itemStack);
	}
	//TODO tags

	public static ItemStack createItemForOreName(String oreName, String registryName) {
		ItemStack oreItem = new ItemStack(registerItem(new ItemForestry(), registryName));
//		OreDictionary.registerOre(oreName, oreItem);
		//TODO - tags
		return oreItem;
	}
}
