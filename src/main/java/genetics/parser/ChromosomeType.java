package genetics.parser;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

public class ChromosomeType implements IChromosomeType {
	private final String name;

	public ChromosomeType(String name) {
		this.name = name;
	}

	@Override
	public int getIndex() {
		return 0;
	}

	@Override
	public IIndividualRoot getRoot() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}


	@Override
	public IRootDefinition getDefinition() {
		return null;
	}

	@Override
	public boolean isValid(IAllele allele) {
		return false;
	}
}
