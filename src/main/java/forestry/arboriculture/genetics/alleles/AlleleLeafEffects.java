package forestry.arboriculture.genetics.alleles;

import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.AlleleManager;
import forestry.core.genetics.alleles.Allele;

public class AlleleLeafEffects {
	public static final Allele leavesNone = new AlleleLeafEffectNone();

	public static void registerAlleles() {
		AlleleManager.alleleRegistry.registerAllele(leavesNone, TreeChromosomes.EFFECT);
	}
}
