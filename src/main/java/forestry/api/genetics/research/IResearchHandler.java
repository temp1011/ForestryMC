package forestry.api.genetics.research;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;
import genetics.api.root.components.IRootComponent;

public interface IResearchHandler extends IRootComponent {

	/**
	 * @return List of generic catalysts which should be accepted for research by species of this class.
	 */
	Map<ItemStack, Float> getResearchCatalysts();

	/**
	 * @return A float signifying the chance for the passed itemstack to yield a research success.
	 */
	float getResearchSuitability(ItemStack itemstack);

	/**
	 * @return ItemStacks representing the bounty for this research success.
	 */
	NonNullList<ItemStack> getResearchBounty(IAlleleSpecies species, World world, GameProfile gameProfile, IIndividual individual, int bountyLevel);
}
