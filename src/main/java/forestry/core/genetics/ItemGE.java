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
package forestry.core.genetics;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;

import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.core.items.ItemForestry;

public abstract class ItemGE extends ItemForestry {
	protected ItemGE(ItemGroup creativeTab) {
		super((new Item.Properties()).group(creativeTab).setNoRepair());
		//TODO - properties
//		setHasSubtypes(true);
	}

	protected abstract IAlleleForestrySpecies getSpecies(ItemStack itemStack);

	@Override
	public boolean isDamageable() {
		return false;
	}

	//TODO - what is this now
//	@Override
//	public boolean getShareTag() {
//		return true;
//	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		if (!stack.hasTag()) { // villager trade wildcard bees
			return false;
		}
		IAlleleForestrySpecies species = getSpecies(stack);
		return species.hasEffect();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		if (itemstack.getTag() == null) {
			return;
		}

		Optional<IIndividual> optionalIndividual = GeneticHelper.getIndividual(itemstack);

		if (optionalIndividual.isPresent()) {
			IIndividual individual = optionalIndividual.get();
			if (individual.isAnalyzed()) {
				if (Screen.hasShiftDown()) {
					individual.addTooltip(list);
				} else {
					list.add(new TranslationTextComponent("for.gui.tooltip.tmi", "< %s >").setStyle((new Style()).setItalic(true)));
				}
			}
		} else {
			list.add(new TranslationTextComponent("for.gui.unknown",  "< %s >"));
		}
	}

	@Nullable
	@Override
	public String getCreatorModId(ItemStack itemStack) {
		IAlleleForestrySpecies species = getSpecies(itemStack);
		return species.getRegistryName().getNamespace();
	}
}
