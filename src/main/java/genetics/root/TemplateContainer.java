package genetics.root;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IKaryotype;
import genetics.api.root.ITemplateContainer;

public class TemplateContainer implements ITemplateContainer {
	private final IKaryotype karyotype;
	private final ImmutableMap<String, IAllele[]> templates;

	TemplateContainer(IKaryotype karyotype, ImmutableMap<String, IAllele[]> templates) {
		this.karyotype = karyotype;
		this.templates = templates;
	}

	@Override
	public Map<String, IAllele[]> getGenomeTemplates() {
		return templates;
	}

	@Override
	public ImmutableCollection<IAllele[]> getTemplates() {
		return templates.values();
	}

	@Override
	public IAllele[] getRandomTemplate(Random rand) {
		Collection<IAllele[]> alleles = this.templates.values();
		int size = alleles.size();
		IAllele[][] templatesArray = alleles.toArray(new IAllele[size][]);
		return templatesArray[rand.nextInt(size)];
	}

	@Override
	public IAllele[] getTemplate(String identifier) {
		IAllele[] alleles = templates.get(identifier);
		if (alleles == null) {
			return new IAllele[0];
		}
		return Arrays.copyOf(alleles, alleles.length);
	}

	@Override
	public IKaryotype getKaryotype() {
		return karyotype;
	}
}
