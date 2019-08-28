/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

/**
 * Basic species allele.
 */
public interface IAlleleForestrySpecies extends IAlleleSpecies {

	/* RESEARCH */

	/**
	 * Complexity determines the difficulty researching a species. The values of primary and secondary are
	 * added together (and rounded) to determine the amount of pairs needed for successful research.
	 *
	 * @return Values between 3 - 11 are useful.
	 */
	int getComplexity();

	/**
	 * @return A float signifying the chance for the passed itemstack to yield a research success.
	 */
	float getResearchSuitability(ItemStack itemstack);

	/**
	 * @return itemstacks representing the bounty for this research success.
	 */
	NonNullList<ItemStack> getResearchBounty(World world, GameProfile gameProfile, IIndividual individual, int bountyLevel);

	/* CLIMATE */

	/**
	 * @return Preferred temperature
	 */
	EnumTemperature getTemperature();

	/**
	 * @return Preferred humidity
	 */
	EnumHumidity getHumidity();

	/* MISC */

	/**
	 * @return true if the species icon should have a glowing effect.
	 */
	boolean hasEffect();

	/**
	 * @return true if the species should not be displayed in NEI or creative inventory.
	 */
	boolean isSecret();

	/**
	 * @return true to have the species count against the species total.
	 */
	boolean isCounted();

	/* APPEARANCE */

	/**
	 * @param renderPass Render pass to getComb the colour for.
	 * @return Colour to use for the render pass.
	 */
	int getSpriteColour(int renderPass);
}
