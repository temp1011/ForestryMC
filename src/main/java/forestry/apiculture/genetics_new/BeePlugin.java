package forestry.apiculture.genetics_new;

import genetics.api.GeneticPlugin;
import genetics.api.GeneticsAPI;
import genetics.api.IGeneticApiInstance;
import genetics.api.IGeneticFactory;
import genetics.api.IGeneticPlugin;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.classification.IClassificationRegistry;
import genetics.api.root.IRootDefinition;
import genetics.api.root.IRootManager;
import genetics.api.root.components.IRootComponentRegistry;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.apiculture.genetics.BeeHelper;
import forestry.apiculture.genetics.BeeRoot;

@GeneticPlugin
public class BeePlugin implements IGeneticPlugin {
	public static final IRootDefinition<BeeRoot> ROOT = GeneticsAPI.apiInstance.getRoot(BeeRoot.UID);

	@Override
	public void registerClassifications(IClassificationRegistry registry) {

	}

	@Override
	public void registerAlleles(IAlleleRegistry registry) {

	}

	@Override
	public void registerComponents(IRootComponentRegistry componentRegistry) {

	}

	@Override
	public void createRoot(IRootManager rootManager, IGeneticFactory geneticFactory) {
		rootManager.createRoot(BeeRoot.UID).setSpeciesType(BeeChromosomes.SPECIES).setDefaultTemplate(BeeHelper::createDefaultTemplate);
	}

	@Override
	public void initRoots(IRootManager manager) {

	}

	@Override
	public void onFinishRegistration(IRootManager manager, IGeneticApiInstance instance) {

	}
}
