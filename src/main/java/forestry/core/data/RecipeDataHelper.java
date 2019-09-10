package forestry.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.data.IFinishedRecipe;

import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;

import forestry.core.config.Constants;
import forestry.core.recipes.ModuleEnabledCondition;

//Useful when there is either a recipe, or it is disabled. Convenience from not having to provide an
//ID when building
//
//also contains convienience method for module condition
public class RecipeDataHelper {

	private Consumer<IFinishedRecipe> consumer;

	public RecipeDataHelper(Consumer<IFinishedRecipe> consumer) {
		this.consumer = consumer;
	}

	public void simpleConditionalRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, ICondition... conditions) {
		ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
		for (ICondition condition : conditions) {
			builder.addCondition(condition);
		}

		Holder<IFinishedRecipe> finishedRecipeHolder = new Holder<>();
		recipe.accept(finishedRecipeHolder::set);

		IFinishedRecipe finishedRecipe = finishedRecipeHolder.get();
		builder.addRecipe(finishedRecipe);
		builder.build(consumer, finishedRecipe.getID());
	}

	public void moduleConditionRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, String moduleUID) {
		simpleConditionalRecipe(recipe, new ModuleEnabledCondition(Constants.MOD_ID, moduleUID));
	}

	private static class Holder<T> {

		private T obj;

		private Holder() {

		}

		private void set(T obj) {
			this.obj = obj;
		}

		private T get() {
			return obj;
		}
	}
}
