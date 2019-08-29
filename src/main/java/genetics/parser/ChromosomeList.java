package genetics.parser;

import java.util.Collections;
import java.util.List;

import genetics.api.individual.IChromosomeType;

public class ChromosomeList {
	public final String name;
	public final List<IChromosomeType> chromosomes;

	public ChromosomeList(String name) {
		this.name = name;
		this.chromosomes = Collections.emptyList();
	}
}
