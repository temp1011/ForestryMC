package forestry.arboriculture;

import java.util.Collection;
import java.util.Map;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import genetics.api.GeneticPlugin;
import genetics.api.GeneticsAPI;
import genetics.api.IGeneticFactory;
import genetics.api.IGeneticPlugin;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;
import genetics.api.root.IRootDefinition;
import genetics.api.root.IRootManager;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.ForestryComponentKeys;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.genetics.TreeRoot;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.genetics.alleles.EnumAllele;
import forestry.core.genetics.root.IResearchPlugin;
import forestry.core.genetics.root.ResearchBuilder;

@GeneticPlugin
public class TreePlugin implements IGeneticPlugin {
	public static final IRootDefinition<TreeRoot> ROOT = GeneticsAPI.apiInstance.getRoot(TreeRoot.UID);

	@Override
	public void registerAlleles(IAlleleRegistry registry) {
		registry.registerAlleles(EnumAllele.Height.values(), TreeChromosomes.HEIGHT);
		registry.registerAlleles(EnumAllele.Saplings.values(), TreeChromosomes.FERTILITY);
		registry.registerAlleles(EnumAllele.Yield.values(), TreeChromosomes.YIELD);
		registry.registerAlleles(EnumAllele.Fireproof.values(), TreeChromosomes.FIREPROOF);
		registry.registerAlleles(EnumAllele.Maturation.values(), TreeChromosomes.MATURATION);
		registry.registerAlleles(EnumAllele.Sappiness.values(), TreeChromosomes.SAPPINESS);
		AlleleFruits.registerAlleles(registry);
	}

	@Override
	public void createRoot(IRootManager rootManager, IGeneticFactory geneticFactory) {
		//TODO tags?
		rootManager.createRoot(TreeRoot.UID)
			.setSpeciesType(TreeChromosomes.SPECIES)
			.addComponent(ForestryComponentKeys.RESEARCH, ResearchBuilder::new)
			.addListener(ForestryComponentKeys.RESEARCH, builder -> {
				builder.setResearchSuitability(new ItemStack(Blocks.OAK_SAPLING), 1.0f);
				builder.addPlugin(new IResearchPlugin() {
					@Override
					public float getResearchSuitability(IAlleleSpecies species, ItemStack itemstack) {
						if (itemstack.isEmpty() || !(species instanceof IAlleleTreeSpecies)) {
							return -1F;
						}
						IAlleleTreeSpecies treeSpecies = (IAlleleTreeSpecies) species;

						Collection<IFruitFamily> suitableFruit = treeSpecies.getSuitableFruit();
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
						return -1F;
					}

					@Override
					public NonNullList<ItemStack> getResearchBounty(IAlleleSpecies species, World world, GameProfile researcher, IIndividual individual, int bountyLevel) {
						return NonNullList.create();
					}
				});
			})
			.setDefaultTemplate(TreeHelper::createDefaultTemplate);
	}
}
