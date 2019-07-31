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
package forestry.apiculture.blocks;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.ItemBlockCandle;
import forestry.apiculture.items.ItemBlockHoneyComb;
import forestry.core.blocks.BlockBase;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryApiculture extends BlockRegistry {
	public final BlockApiculture apiary;
	public final BlockApiculture beeHouse;
	public final BlockBase<BlockTypeApicultureTesr> beeChest;
	public final Map<IHiveRegistry.HiveType, BlockBeeHive> beehives = new EnumMap<>(IHiveRegistry.HiveType.class);
	public final BlockCandle candle;
	public final BlockStump stump;
	public final Map<EnumHoneyComb, BlockHoneyComb> beeCombs = new EnumMap<>(EnumHoneyComb.class);
	private final Map<BlockAlvearyType, BlockAlveary> alvearyBlockMap = new EnumMap<>(BlockAlvearyType.class);

	public BlockRegistryApiculture() {
		apiary = new BlockApiculture(BlockTypeApiculture.APIARY);
		registerBlock(apiary, new ItemBlockForestry<>(apiary), "apiary");

		beeHouse = new BlockApiculture(BlockTypeApiculture.BEE_HOUSE);
		registerBlock(beeHouse, new ItemBlockForestry<>(beeHouse), "bee_house");

		beeChest = new BlockBase<>(BlockTypeApicultureTesr.APIARIST_CHEST, Material.WOOD);
		registerBlock(beeChest, new ItemBlockForestry<>(beeChest), "bee_chest");
		//		beeChest.setCreativeTab(ItemGroups.tabApiculture);	//TODO done in item
		//		beeChest.setHarvestLevel("axe", 0);	//TODO done in item

		//TODO tag?
		for (IHiveRegistry.HiveType type : IHiveRegistry.HiveType.VALUES) {
			BlockBeeHive hive = new BlockBeeHive(type);
			registerBlock(hive, new ItemBlockForestry<>(hive), "beehive_" + type.getName());    //TODO would use type.getUUid() but seems that might cause issues with resourcelocations
			beehives.put(type, hive);
		}

		candle = new BlockCandle();
		registerBlock(candle, new ItemBlockCandle(candle), "candle");
		stump = new BlockStump();
		registerBlock(stump, new ItemBlockForestry<>(stump), "stump");

		for (EnumHoneyComb type : EnumHoneyComb.VALUES) {
			BlockHoneyComb block = new BlockHoneyComb(type);
			registerBlock(block, new ItemBlockHoneyComb(block), "block_bee_combs_" + type.getName());
		}    //TODO tag?

		for (BlockAlvearyType type : BlockAlvearyType.VALUES) {
			BlockAlveary block = new BlockAlveary(type);
			registerBlock(block, new ItemBlockForestry<>(block), "alveary_" + block.getType());
			alvearyBlockMap.put(type, block);
		}
	}

	public ItemStack getCombBlock(EnumHoneyComb honeyComb) {
		return new ItemStack(beeCombs.get(honeyComb));
	}

	public BlockAlveary getAlvearyBlock(BlockAlvearyType type) {
		return alvearyBlockMap.get(type);
	}

	public ItemStack getAlvearyBlockStack(BlockAlvearyType type) {
		BlockAlveary alvearyBlock = alvearyBlockMap.get(type);
		return new ItemStack(alvearyBlock);
	}
}
