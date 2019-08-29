package genetics.api.root.translator;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRootBuilder;
import genetics.api.root.components.IRootComponent;

/**
 * Translates a item or a block that does not contain any genetic information into a {@link ItemStack} or a
 * {@link IIndividual} if a {@link IItemTranslator} or {@link IBlockTranslator} was registered for it at the
 * {@link IIndividualRootBuilder}.
 */
public interface IIndividualTranslator<I extends IIndividual> extends IRootComponent {
	/**
	 * @param translatorKey The key of the translator, by default it is the item of the {@link ItemStack} that you want
	 *                      to translate with the translator.
	 */
	Optional<IItemTranslator<I>> getTranslator(Item translatorKey);

	/**
	 * @param translatorKey The key of the translator the block of the{@link BlockState} that you want to translate
	 *                      with the translator.
	 */
	Optional<IBlockTranslator<I>> getTranslator(Block translatorKey);

	/**
	 * Translates {@link BlockState}s into genetic data.
	 */
	Optional<I> translateMember(BlockState objectToTranslate);

	/**
	 * Translates {@link ItemStack}s into genetic data.
	 */
	Optional<I> translateMember(ItemStack objectToTranslate);

	/**
	 * Translates a {@link BlockState}s into genetic data and returns a {@link ItemStack} that contains this data.
	 */
	ItemStack getGeneticEquivalent(BlockState objectToTranslate);

	/**
	 * Translates {@link ItemStack}s into genetic data and returns a other {@link ItemStack} that contains this data.
	 */
	ItemStack getGeneticEquivalent(ItemStack objectToTranslate);
}
