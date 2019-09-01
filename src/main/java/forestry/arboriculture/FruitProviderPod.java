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
package forestry.arboriculture;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IFruitFamily;
import forestry.core.utils.BlockUtil;

//TODO this is horribly hacky. Cleanup once dependancy issues with AlleleFruits is fixed.
public class FruitProviderPod extends FruitProviderNone {

	public enum EnumPodType {
		COCOA, DATES, PAPAYA;//, COCONUT;

		public String getModelName() {
			return toString().toLowerCase(Locale.ENGLISH);
		}
	}

	private final EnumPodType type;

	private final Map<Supplier<ItemStack>, Float> drops;

	public FruitProviderPod(String unlocalizedDescription, IFruitFamily family, EnumPodType type, Supplier<ItemStack>... dropOnMature) {
		super(unlocalizedDescription, family);
		this.type = type;
		this.drops = new HashMap<>();
		for (Supplier<ItemStack> drop : dropOnMature) {
			this.drops.put(drop, 1.0f);
		}
	}

	@Override
	public boolean requiresFruitBlocks() {
		return true;
	}

	@Override
	public NonNullList<ItemStack> getFruits(@Nullable ITreeGenome genome, World world, BlockPos pos, int ripeningTime) {
		if (drops.isEmpty()) {
			return NonNullList.create();
		}

		if (ripeningTime >= 2) {
			NonNullList<ItemStack> products = NonNullList.create();
			for (Supplier<ItemStack> drop : this.drops.keySet()) {
				products.add(drop.get().copy());
			}
			return products;
		}

		return NonNullList.create();
	}

	@Override
	public boolean trySpawnFruitBlock(ITreeGenome genome, World world, Random rand, BlockPos pos) {
		if (rand.nextFloat() > getFruitChance(genome, world, pos)) {
			return false;
		}

		if (type == EnumPodType.COCOA) {
			return BlockUtil.tryPlantCocoaPod(world, pos);
		} else {
			IAlleleFruit activeAllele = (IAlleleFruit) genome.getActiveAllele(EnumTreeChromosome.FRUITS);
			return TreeManager.treeRoot.setFruitBlock(world, genome, activeAllele, genome.getYield(), pos);
		}
	}

	@Override
	public ResourceLocation getSprite(ITreeGenome genome, IBlockReader world, BlockPos pos, int ripeningTime) {
		return null;
	}

	@Override
	public ResourceLocation getDecorativeSprite() {
		return null;
	}

	@Override
	public Map<ItemStack, Float> getProducts() {
		return drops.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().get(), Map.Entry::getValue));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerSprites() {
	}

	@Override
	public String getModelName() {
		return type.getModelName();
	}
}
