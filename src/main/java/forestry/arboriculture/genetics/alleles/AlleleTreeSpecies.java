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
package forestry.arboriculture.genetics.alleles;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.PlantType;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ILeafProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.IAlleleTreeSpeciesBuilder;
import forestry.api.arboriculture.genetics.ITreeRoot;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.core.IModelManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.genetics.ClimateGrowthProvider;
import forestry.arboriculture.genetics.LeafProvider;
import forestry.core.genetics.alleles.AlleleForestrySpecies;

public class AlleleTreeSpecies extends AlleleForestrySpecies implements IAlleleTreeSpeciesBuilder, IAlleleTreeSpecies {
	private final ITreeGenerator generator;
	private final IGermlingModelProvider germlingModelProvider;
	private final ILeafSpriteProvider leafSpriteProvider;
	private final List<IFruitFamily> fruits = new ArrayList<>();
	private PlantType nativeType = PlantType.Plains;
	private final ILeafProvider leafProvider;
	private IGrowthProvider growthProvider = new ClimateGrowthProvider();
	private float rarity = 0.0F;

	public AlleleTreeSpecies(
		String uid,
		String unlocalizedName,
		String authority,
		String unlocalizedDescription,
		boolean isDominant,
		IClassification branch,
		String binomial,
		String modID,
		ILeafSpriteProvider leafIconProvider,
		IGermlingModelProvider germlingModelProvider,
		ITreeGenerator generator,
		@Nullable ILeafProvider leafProvider) {
		super(modID, uid, unlocalizedName, authority, unlocalizedDescription, isDominant, branch, binomial);

		this.generator = generator;
		this.germlingModelProvider = germlingModelProvider;
		this.leafSpriteProvider = leafIconProvider;
		if (leafProvider == null) {
			this.leafProvider = new LeafProvider();
		} else {
			this.leafProvider = leafProvider;
		}
	}

	@Override
	public IAlleleTreeSpecies build() {
		AlleleManager.alleleRegistry.registerAllele(this, TreeChromosomes.SPECIES);
		leafProvider.init(this);
		return this;
	}

	@Override
	public ITreeRoot getRoot() {
		return TreeManager.treeRoot;
	}

	@Override
	public AlleleTreeSpecies setPlantType(PlantType type) {
		nativeType = type;
		return this;
	}

	@Override
	public AlleleTreeSpecies addFruitFamily(IFruitFamily family) {
		fruits.add(family);
		return this;
	}

	@Override
	public IAlleleTreeSpeciesBuilder setRarity(float rarity) {
		this.rarity = rarity;
		return this;
	}

	@Override
	public float getRarity() {
		return rarity;
	}

	@Override
	public IAlleleTreeSpeciesBuilder setGrowthProvider(IGrowthProvider growthProvider) {
		this.growthProvider = growthProvider;
		return this;
	}

	@Override
	public IGrowthProvider getGrowthProvider() {
		return growthProvider;
	}

	/* OTHER */
	@Override
	public PlantType getPlantType() {
		return nativeType;
	}

	@Override
	public List<IFruitFamily> getSuitableFruit() {
		return fruits;
	}

	@Override
	public ITreeGenerator getGenerator() {
		return generator;
	}

	@Override
	public ILeafSpriteProvider getLeafSpriteProvider() {
		return leafSpriteProvider;
	}

	@Override
	public int getSpriteColour(int renderPass) {
		return leafSpriteProvider.getColor(false);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ModelResourceLocation getGermlingModel(EnumGermlingType type) {
		return germlingModelProvider.getModel(type);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getGermlingColour(EnumGermlingType type, int renderPass) {
		return germlingModelProvider.getSpriteColor(type, renderPass);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModels(Item item, IModelManager manager, EnumGermlingType type) {
		germlingModelProvider.registerModels(item, manager, type);
	}

	@Override
	public ILeafProvider getLeafProvider() {
		return leafProvider;
	}

	@Override
	public float getResearchSuitability(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return 0f;
		}

		List<IFruitFamily> suitableFruit = getSuitableFruit();
		for (IFruitFamily fruitFamily : suitableFruit) {
			Collection<IFruitProvider> fruitProviders = TreeManager.treeRoot.getFruitProvidersForFruitFamily(fruitFamily);
			for (IFruitProvider fruitProvider : fruitProviders) {
				Map<ItemStack, Float> products = fruitProvider.getProducts();
				for (ItemStack stack : products.keySet()) {
					if (stack.isItemEqual(itemstack)) {
						return 1.0f;
					}
				}
				Map<ItemStack, Float> specialtyChances = fruitProvider.getSpecialty();
				for (ItemStack stack : specialtyChances.keySet()) {
					if (stack.isItemEqual(itemstack)) {
						return 1.0f;
					}
				}
			}
		}

		return super.getResearchSuitability(itemstack);
	}

	@Override
	public int compareTo(IAlleleTreeSpecies o) {
		return 0;
	}
}
