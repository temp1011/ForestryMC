package forestry.apiculture.genetics;

import genetics.api.GeneticPlugin;
import genetics.api.GeneticsAPI;
import genetics.api.IGeneticFactory;
import genetics.api.IGeneticPlugin;
import genetics.api.root.IRootDefinition;
import genetics.api.root.IRootManager;

import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.genetics.TreeRoot;

@GeneticPlugin
public class TreePlugin implements IGeneticPlugin {
	public static final IRootDefinition<TreeRoot> ROOT = GeneticsAPI.apiInstance.getRoot(TreeRoot.UID);

	@Override
	public void createRoot(IRootManager rootManager, IGeneticFactory geneticFactory) {
		rootManager.createRoot(TreeRoot.UID).setSpeciesType(TreeChromosomes.SPECIES).setDefaultTemplate(TreeHelper::createDefaultTemplate);
	}
}
