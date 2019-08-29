package genetics.root;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.ITemplateContainer;
import genetics.api.root.ITemplateRegistry;
import genetics.api.root.components.RootComponentBuilder;

public class TemplateRegistry<I extends IIndividual> extends RootComponentBuilder<ITemplateContainer, I> implements ITemplateRegistry<I> {
	private final HashMap<String, IAllele[]> templates = new HashMap<>();
	private final IKaryotype karyotype;

	public TemplateRegistry(IIndividualRoot<I> root) {
		super(root);
		this.karyotype = root.getKaryotype();
	}

	@Override
	public ITemplateRegistry registerTemplate(IAllele[] template) {
		Preconditions.checkNotNull(template, "Tried to register null template");
		Preconditions.checkArgument(template.length > 0, "Tried to register empty template");

		IChromosomeType templateType = karyotype.getSpeciesType();
		IAllele templateAllele = template[templateType.getIndex()];
		String identifier = templateAllele.getRegistryName().toString();
		templates.put(identifier, template);

		return this;
	}

	@Override
	public ITemplateRegistry registerTemplate(IAlleleTemplate template) {
		return registerTemplate(template.alleles());
	}

	@Override
	public IKaryotype getKaryotype() {
		return karyotype;
	}

	@Override
	public ITemplateContainer create() {
		return new TemplateContainer(karyotype, ImmutableMap.copyOf(templates));
	}
}
