package genetics.api.individual;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.events.RootBuilderEvents.BuildComponent;
import genetics.api.events.RootBuilderEvents.GatherDefinitions;
import genetics.api.root.IIndividualRootBuilder;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.IRootComponentBuilder;

/**
 * Help interface that can be used to define genetic species.
 * It provides method to get the an instance of the default template, the default genome or default individual
 * of the defined species.
 * It also can be used to register mutations or other species related data to the {@link IRootComponentBuilder}s of the
 * {@link IIndividualRootBuilder} of the root that the species belongs to.
 */
public interface ISpeciesDefinition extends ITemplateProvider {
	/**
	 * This method is called for every {@link IRootComponentBuilder} that was registered for the
	 * {@link IIndividualRootBuilder} of the root that the species belongs to.
	 * <p>
	 * This method gets called for every definition that was added to the {@link GatherDefinitions}
	 * event.
	 * <p>
	 * As an alternative for this method you can use the {@link BuildComponent} event.
	 * <p>
	 * It can be used to register mutations, templates, translators, etc.
	 *
	 * @param key     The key of the given builder.
	 * @param builder The builder that is associated to the given key.
	 * @param <B>     The type of the given builder.
	 */
	default <B extends IRootComponentBuilder> void onComponent(ComponentKey<?, B> key, B builder) {
	}

	/**
	 * @return An instance of the genome that contains the default template of this species.
	 */
	IGenome getGenome();

	/**
	 * @return The species that is defined by this interface.
	 */
	IAlleleSpecies getSpecies();

	/**
	 * @return Creates a instance of the {@link IIndividual} that contains the {@link #getGenome()}.
	 */
	IIndividual createIndividual();
}
