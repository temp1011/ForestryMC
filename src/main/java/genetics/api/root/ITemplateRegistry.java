package genetics.api.root;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.root.components.IRootComponentBuilder;

public interface ITemplateRegistry<I extends IIndividual> extends IRootComponentBuilder<ITemplateContainer> {

	/**
	 * Registers a allele template using the UID of the first allele as identifier.
	 */
	ITemplateRegistry registerTemplate(IAllele[] template);

	/**
	 * Registers a allele template using the UID of the first allele as identifier.
	 */
	ITemplateRegistry registerTemplate(IAlleleTemplate template);

	/**
	 * @return The karyotype that defines the size of the allele array and which alleles it can contain.
	 */
	IKaryotype getKaryotype();
}
