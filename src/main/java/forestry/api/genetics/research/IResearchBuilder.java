package forestry.api.genetics.research;

import net.minecraft.item.ItemStack;

import genetics.api.root.components.IRootComponentBuilder;

import forestry.core.genetics.root.IResearchPlugin;

public interface IResearchBuilder extends IRootComponentBuilder<IResearchHandler> {
	/**
	 * Sets an item stack as a valid (generic) research catalyst for this class.
	 *
	 * @param stack       ItemStack to set as suitable.
	 * @param suitability Float between 0 and 1 to indicate suitability.
	 */
	void setResearchSuitability(ItemStack stack, float suitability);

	void addPlugin(IResearchPlugin plugin);
}
