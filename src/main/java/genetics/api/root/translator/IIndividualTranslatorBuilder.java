package genetics.api.root.translator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganism;
import genetics.api.root.components.IRootComponentBuilder;

public interface IIndividualTranslatorBuilder<I extends IIndividual> extends IRootComponentBuilder<IIndividualTranslator<I>> {
	/**
	 * Registers a translator that translates a {@link BlockState} into a  {@link IIndividual} or an {@link ItemStack}
	 * that contains an {@link IOrganism}.
	 *
	 * @param translatorKeys The key of the translator the block of {@link BlockState} that you want to translate
	 *                       with the translator.
	 * @param translator     A translator that should be used to translate the data.
	 */
	IIndividualTranslatorBuilder<I> registerTranslator(IBlockTranslator<I> translator, Block... translatorKeys);

	/**
	 * Registers a translator that translates an {@link ItemStack} that does not contain an {@link IOrganism} into a
	 * {@link IIndividual} or another {@link ItemStack} that contains an {@link IOrganism}.
	 *
	 * @param translatorKeys The key of the translator it is the item of the {@link ItemStack} that you want to translate
	 *                       with the translator.
	 * @param translator     A translator that should be used to translate the data.
	 */
	IIndividualTranslatorBuilder<I> registerTranslator(IItemTranslator<I> translator, Item... translatorKeys);
}
