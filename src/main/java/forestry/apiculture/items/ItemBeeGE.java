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
import java.util.List;
import java.util.Optional;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import genetics.api.GeneticHelper;
import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.genetics.IBeeRoot;
import forestry.api.core.IModelManager;
import forestry.api.core.ItemGroups;
import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.apiculture.genetics.BeeGenome;
import forestry.apiculture.genetics.BeeHelper;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.items.IColoredItem;
import forestry.core.utils.Translator;

public class ItemBeeGE extends ItemGE implements IColoredItem {

	private final EnumBeeType type;

	public ItemBeeGE(EnumBeeType type) {
		super(ItemGroups.tabApiculture);
		this.type = type;
		if (type != EnumBeeType.DRONE) {
			//TODO - item properties
//			setMaxStackSize(1);
		}
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return GeneticHelper.createOrganism(stack, type, BeeHelper.getRoot().getDefinition());
	}

	@Override
	protected IAlleleForestrySpecies getSpecies(ItemStack itemStack) {
		return BeeGenome.getSpecies(itemStack);
	}

	//TODO - pretty sure this is still translating on the server atm
	@Override
	public ITextComponent getDisplayName(ItemStack itemstack) {
		if (itemstack.getTag() == null) {
			return super.getDisplayName(itemstack);
		}
		Optional<IBee> optionalIndividual = GeneticHelper.getIndividual(itemstack);
		if (!optionalIndividual.isPresent()) {
			return super.getDisplayName(itemstack);
		}

		IBee individual = optionalIndividual.get();
		String customBeeKey = "for.bees.custom." + type.getName() + "." + individual.getGenome().getPrimary().getLocalisationKey().replace("bees.species.", "");
		if (Translator.canTranslateToLocal(customBeeKey)) {
			return new TranslationTextComponent(customBeeKey);
		}
		String beeGrammar = Translator.translateToLocal("for.bees.grammar." + type.getName());
		String beeSpecies = individual.getGenome().getPrimary().getLocalizedName();
		String beeType = Translator.translateToLocal("for.bees.grammar." + type.getName() + ".type");
		return new TranslationTextComponent(beeGrammar.replaceAll("%SPECIES", beeSpecies).replaceAll("%TYPE", beeType));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		if (itemstack.getTag() == null) {
			return;
		}

		if (type != EnumBeeType.DRONE) {
			Optional<IBee> optionalIndividual = GeneticHelper.getIndividual(itemstack);
			if (!optionalIndividual.isPresent()) {
				return;
			}

			IBee individual = optionalIndividual.get();
			if (individual.isNatural()) {
				list.add(new TranslationTextComponent("for.bees.stock.pristine").setStyle((new Style()).setColor(TextFormatting.YELLOW).setItalic(true)));
			} else {
				list.add(new TranslationTextComponent("for.bees.stock.ignoble").setStyle((new Style()).setColor(TextFormatting.YELLOW)));
			}
		}

		super.addInformation(itemstack, world, list, flag);
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (this.isInGroup(tab)) {
			addCreativeItems(subItems, true);
		}
	}

	public void addCreativeItems(NonNullList<ItemStack> subItems, boolean hideSecrets) {
		//TODO beeRoot only set in setupAPI but this is called earlier
		//so need to adjust init sequence
		IBeeRoot root = BeeHelper.getRoot();
		for (IBee bee : root.getIndividualTemplates()) {//BeeManager.beeRoot.getIndividualTemplates()) {
			// Don't show secret bees unless ordered to.
			if (hideSecrets && bee.isSecret() && !Config.isDebug) {
				continue;
			}
			ItemStack stack = new ItemStack(this);
			GeneticHelper.setIndividual(stack, bee);
			subItems.add(stack);
		}
	}

	@Override
	public int getColorFromItemstack(ItemStack itemstack, int tintIndex) {
		if (itemstack.getTag() == null) {
			if (tintIndex == 1) {
				return 0xffdc16;
			} else {
				return 0xffffff;
			}
		}

		IAlleleBeeSpecies species = BeeGenome.getSpecies(itemstack);
		return species.getSpriteColour(tintIndex);
	}

	/* MODELS */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (IAllele allele : GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles(BeeChromosomes.SPECIES)) {
			if (allele instanceof IAlleleBeeSpecies) {
				((IAlleleBeeSpecies) allele).registerModels(item, manager);
			}
		}
		//TODO - flatten or something custom rendering I think
//		manager.registerItemModel(item, new BeeMeshDefinition(?));
	}

//	@OnlyIn(Dist.CLIENT)
//	private class BeeMeshDefinition implements ItemMeshDefinition {
//		@Override
//		public ModelResourceLocation getModelLocation(ItemStack stack) {
//			if (!stack.hasTag()) { // villager trade wildcard bees
//				return DefaultBeeModelProvider.instance.getModel(type);
//			}
//			IAlleleBeeSpecies species = (IAlleleBeeSpecies) getSpecies(stack);
//			return species.getModel(type);
//		}
//	}

	public final EnumBeeType getType() {
		return type;
	}
}
