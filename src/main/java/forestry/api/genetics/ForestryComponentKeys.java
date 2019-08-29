package forestry.api.genetics;

import genetics.api.root.components.ComponentKey;

import forestry.api.genetics.research.IResearchBuilder;
import forestry.api.genetics.research.IResearchHandler;

public class ForestryComponentKeys {
	public static final ComponentKey<IResearchHandler, IResearchBuilder> RESEARCH = ComponentKey.create("research", IResearchHandler.class, IResearchBuilder.class);

	private ForestryComponentKeys() {
	}
}
