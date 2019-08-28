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
package forestry.arboriculture.genetics;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IArboristTracker;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IForestryMutation;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.config.Config;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.Individual;

public class Tree extends Individual implements ITree, IPlantable {
	private final ITreeGenome genome;
	@Nullable
	private ITreeGenome mate;

	public Tree(ITreeGenome genome) {
		this.genome = genome;
	}

	public Tree(CompoundNBT compoundNBT) {
		super(compoundNBT);

		if (compoundNBT.contains("Genome")) {
			this.genome = new TreeGenome(compoundNBT.getCompound("Genome"));
		} else {
			throw new IllegalArgumentException("Nbt has no Genome " + compoundNBT);
		}

		if (compoundNBT.contains("Mate")) {
			mate = new TreeGenome(compoundNBT.getCompound("Mate"));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT CompoundNBT) {
		CompoundNBT = super.write(CompoundNBT);

		CompoundNBT nbtGenome = new CompoundNBT();
		genome.write(nbtGenome);
		CompoundNBT.put("Genome", nbtGenome);

		if (mate != null) {
			CompoundNBT nbtMate = new CompoundNBT();
			mate.write(nbtMate);
			CompoundNBT.put("Mate", nbtMate);
		}
		return CompoundNBT;
	}

	/* INTERACTION */
	@Override
	public void mate(ITree other) {
		mate = new TreeGenome(other.getGenome().getChromosomes());
	}

	/* EFFECTS */
	@Override
	public IEffectData[] doEffect(IEffectData[] storedData, World world, BlockPos pos) {
		IAlleleLeafEffect effect = (IAlleleLeafEffect) getGenome().getActiveAllele(EnumTreeChromosome.EFFECT);

		storedData[0] = doEffect(effect, storedData[0], world, pos);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable()) {
			return storedData;
		}

		IAlleleLeafEffect secondary = (IAlleleLeafEffect) getGenome().getInactiveAllele(EnumTreeChromosome.EFFECT);
		if (!secondary.isCombinable()) {
			return storedData;
		}

		storedData[1] = doEffect(secondary, storedData[1], world, pos);

		return storedData;
	}

	private IEffectData doEffect(IAlleleLeafEffect effect, IEffectData storedData, World world, BlockPos pos) {
		storedData = effect.validateStorage(storedData);
		return effect.doEffect(getGenome(), storedData, world, pos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IEffectData[] doFX(IEffectData[] storedData, World world, BlockPos pos) {
		return storedData;
	}

	/* GROWTH */
	@Override
	public Feature getTreeGenerator(World world, BlockPos pos, boolean wasBonemealed) {
		return genome.getPrimary().getGenerator().getWorldGenerator(this);
	}

	@Override
	public boolean canStay(IBlockReader world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);

		Block block = blockState.getBlock();
		return block.canSustainPlant(blockState, world, blockPos, Direction.UP, this);
	}

	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return genome.getPrimary().getPlantType();
	}

	@Override
	public BlockState getPlant(IBlockReader world, BlockPos pos) {
		return world.getBlockState(pos);
	}

	@Override
	@Nullable
	public BlockPos canGrow(World world, BlockPos pos, int expectedGirth, int expectedHeight) {
		return TreeGrowthHelper.canGrow(world, genome, pos, expectedGirth, expectedHeight);
	}

	@Override
	public int getRequiredMaturity() {
		return genome.getMaturationTime();
	}

	@Override
	public int getGirth() {
		return genome.getGirth();
	}

	@Override
	public int getResilience() {
		int base = (int) (getGenome().getFertility() * getGenome().getSappiness() * 100);
		return (base > 1 ? base : 1) * 10;
	}

	@Override
	public float getHeightModifier() {
		return genome.getHeight();
	}

	@Override
	public boolean setLeaves(World world, @Nullable GameProfile owner, BlockPos pos, Random rand) {
		return genome.getPrimary().getGenerator().setLeaves(genome, world, owner, pos, rand);
	}

	@Override
	public boolean setLogBlock(World world, BlockPos pos, Direction facing) {
		return genome.getPrimary().getGenerator().setLogBlock(genome, world, pos, facing);
	}

	@Override
	public boolean allowsFruitBlocks() {
		IFruitProvider provider = getGenome().getFruitProvider();
		if (!provider.requiresFruitBlocks()) {
			return false;
		}

		Collection<IFruitFamily> suitable = genome.getPrimary().getSuitableFruit();
		return suitable.contains(provider.getFamily());
	}

	@Override
	public boolean trySpawnFruitBlock(World world, Random rand, BlockPos pos) {
		IFruitProvider provider = getGenome().getFruitProvider();
		Collection<IFruitFamily> suitable = genome.getPrimary().getSuitableFruit();
		return suitable.contains(provider.getFamily()) &&
			provider.trySpawnFruitBlock(getGenome(), world, rand, pos);
	}

	/* INFORMATION */
	@Override
	public ITreeGenome getGenome() {
		return genome;
	}

	@Override
	public ITree copy() {
		CompoundNBT CompoundNBT = new CompoundNBT();
		this.write(CompoundNBT);
		return new Tree(CompoundNBT);
	}

	@Nullable
	@Override
	public ITreeGenome getMate() {
		return this.mate;
	}

	@Override
	public boolean isPureBred(EnumTreeChromosome chromosome) {
		return genome.getActiveAllele(chromosome).getUID().equals(genome.getInactiveAllele(chromosome).getUID());
	}

	@Override
	public void addTooltip(List<ITextComponent> list) {

		// No info 4 u!
		if (!isAnalyzed) {
			list.add(new StringTextComponent("<").appendSibling(new TranslationTextComponent("for.gui.unknown")).appendText(">"));
			return;
		}

		// You analyzed it? Juicy tooltip coming up!
		IAlleleTreeSpecies primary = genome.getPrimary();
		IAlleleTreeSpecies secondary = genome.getSecondary();
		if (!isPureBred(EnumTreeChromosome.SPECIES)) {
			list.add(new TranslationTextComponent("for.trees.hybrid",primary.getAlleleName(), secondary.getAlleleName()).applyTextStyle(TextFormatting.BLUE));	//TODO formatting
		}

		String sappiness = TextFormatting.GOLD + "S: " + genome.getActiveAllele(EnumTreeChromosome.SAPPINESS).getAlleleName();
		String maturation = TextFormatting.RED + "M: " + genome.getActiveAllele(EnumTreeChromosome.MATURATION).getAlleleName();
		String height = TextFormatting.LIGHT_PURPLE + "H: " + genome.getActiveAllele(EnumTreeChromosome.HEIGHT).getAlleleName();
		String girth = TextFormatting.AQUA + "G: " + String.format("%sx%s", genome.getGirth(), genome.getGirth());
		String saplings = TextFormatting.YELLOW + "S: " + genome.getActiveAllele(EnumTreeChromosome.FERTILITY).getAlleleName();
		String yield = TextFormatting.WHITE + "Y: " + genome.getActiveAllele(EnumTreeChromosome.YIELD).getAlleleName();
		list.add(new StringTextComponent(String.format("%s, %s", saplings, maturation)));
		list.add(new StringTextComponent(String.format("%s, %s", height, girth)));
		list.add(new StringTextComponent(String.format("%s, %s", yield, sappiness)));

		IAlleleBoolean primaryFireproof = (IAlleleBoolean) genome.getActiveAllele(EnumTreeChromosome.FIREPROOF);
		if (primaryFireproof.getValue()) {
			list.add(new TranslationTextComponent("for.gui.fireresist").applyTextStyle(TextFormatting.RED));
		}

		IAllele fruit = getGenome().getActiveAllele(EnumTreeChromosome.FRUITS);
		if (fruit != AlleleFruits.fruitNone) {
			String strike = "";
			if (!canBearFruit()) {
				strike = TextFormatting.STRIKETHROUGH.toString();
			}
			list.add(new StringTextComponent(strike + TextFormatting.GREEN + "F: " + genome.getFruitProvider().getDescription()));
		}
	}

	/* REPRODUCTION */
	@Override
	public List<ITree> getSaplings(ServerWorld world, @Nullable GameProfile playerProfile, BlockPos pos, float modifier) {
		List<ITree> prod = new ArrayList<>();

		float chance = genome.getFertility() * modifier;

		if (world.rand.nextFloat() <= chance) {
			if (mate == null) {
				prod.add(TreeManager.treeRoot.getTree(world, new TreeGenome(genome.getChromosomes())));
			} else {
				prod.add(createOffspring(world, mate, playerProfile, pos));
			}
		}

		return prod;
	}

	private ITree createOffspring(ServerWorld world, ITreeGenome mate, @Nullable GameProfile playerProfile, BlockPos pos) {
		IChromosome[] chromosomes = new IChromosome[genome.getChromosomes().length];
		IChromosome[] parent1 = genome.getChromosomes();
		IChromosome[] parent2 = mate.getChromosomes();

		// Check for mutation. Replace one of the parents with the mutation
		// template if mutation occured.
		IChromosome[] mutated = mutateSpecies(world, playerProfile, pos, genome, mate);
		if (mutated == null) {
			mutated = mutateSpecies(world, playerProfile, pos, mate, genome);
		}

		if (mutated != null) {
			return new Tree(new TreeGenome(mutated));
		}

		for (int i = 0; i < parent1.length; i++) {
			if (parent1[i] != null && parent2[i] != null) {
				chromosomes[i] = Chromosome.inheritChromosome(world.rand, parent1[i], parent2[i]);
			}
		}

		return new Tree(new TreeGenome(chromosomes));
	}

	@Nullable
	private static IChromosome[] mutateSpecies(ServerWorld world, @Nullable GameProfile playerProfile, BlockPos pos, ITreeGenome genomeOne, ITreeGenome genomeTwo) {
		IChromosome[] parent1 = genomeOne.getChromosomes();
		IChromosome[] parent2 = genomeTwo.getChromosomes();

		ITreeGenome genome0;
		ITreeGenome genome1;
		IAlleleTreeSpecies allele0;
		IAlleleTreeSpecies allele1;

		if (world.rand.nextBoolean()) {
			allele0 = (IAlleleTreeSpecies) parent1[EnumTreeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = (IAlleleTreeSpecies) parent2[EnumTreeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeOne;
			genome1 = genomeTwo;
		} else {
			allele0 = (IAlleleTreeSpecies) parent2[EnumTreeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = (IAlleleTreeSpecies) parent1[EnumTreeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeTwo;
			genome1 = genomeOne;
		}

		IArboristTracker breedingTracker = null;
		if (playerProfile != null) {
			breedingTracker = TreeManager.treeRoot.getBreedingTracker(world, playerProfile);
		}

		List<IForestryMutation> combinations = TreeManager.treeRoot.getCombinations(allele0, allele1, true);
		for (IForestryMutation mutation : combinations) {
			ITreeMutation treeMutation = (ITreeMutation) mutation;
			// Stop blacklisted species.
			// if (BeeManager.breedingManager.isBlacklisted(mutation.getTemplate()[0].getUID())) {
			// continue;
			// }

			float chance = treeMutation.getChance(world, pos, allele0, allele1, genome0, genome1);
			if (chance <= 0) {
				continue;
			}

			// boost chance for researched mutations
			if (breedingTracker != null && breedingTracker.isResearched(treeMutation)) {
				float mutationBoost = chance * (Config.researchMutationBoostMultiplier - 1.0f);
				mutationBoost = Math.min(Config.maxResearchMutationBoostPercent, mutationBoost);
				chance += mutationBoost;
			}

			if (chance > world.rand.nextFloat() * 100) {
				return TreeManager.treeRoot.templateAsChromosomes(treeMutation.getTemplate());
			}
		}

		return null;
	}

	/* PRODUCTION */
	@Override
	public boolean canBearFruit() {
		return genome.getPrimary().getSuitableFruit().contains(genome.getFruitProvider().getFamily());
	}

	@Override
	public Map<ItemStack, Float> getProducts() {
		return genome.getFruitProvider().getProducts();
	}

	@Override
	public Map<ItemStack, Float> getSpecialties() {
		return genome.getFruitProvider().getSpecialty();
	}

	@Override
	public NonNullList<ItemStack> produceStacks(World world, BlockPos pos, int ripeningTime) {
		return genome.getFruitProvider().getFruits(genome, world, pos, ripeningTime);
	}
}
