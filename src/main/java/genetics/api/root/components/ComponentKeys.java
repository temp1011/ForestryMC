package genetics.api.root.components;

import genetics.api.mutation.IMutationContainer;
import genetics.api.mutation.IMutationRegistry;
import genetics.api.organism.IOrganismTypes;
import genetics.api.organism.IOrganismTypesBuilder;
import genetics.api.root.ITemplateContainer;
import genetics.api.root.ITemplateRegistry;
import genetics.api.root.translator.IIndividualTranslator;
import genetics.api.root.translator.IIndividualTranslatorBuilder;

/**
 * Every default component key that the genetic api provides.
 */
public class ComponentKeys {
	/* Components that are added automatically at the creation of the IIndividualRootBuilder. */
	public static final ComponentKey<ITemplateContainer, ITemplateRegistry> TEMPLATES = ComponentKey.create("templates", ITemplateContainer.class, ITemplateRegistry.class);
	public static final ComponentKey<IOrganismTypes, IOrganismTypesBuilder> TYPES = ComponentKey.create("types", IOrganismTypes.class, IOrganismTypesBuilder.class);
	/* Components that are optional. */
	public static final ComponentKey<IIndividualTranslator, IIndividualTranslatorBuilder> TRANSLATORS = ComponentKey.create("translators", IIndividualTranslator.class, IIndividualTranslatorBuilder.class);
	public static final ComponentKey<IMutationContainer, IMutationRegistry> MUTATIONS = ComponentKey.create("mutations", IMutationContainer.class, IMutationRegistry.class);

	private ComponentKeys() {
	}
}
