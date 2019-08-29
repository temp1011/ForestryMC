package genetics.api.events;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IGenericEvent;

import genetics.api.individual.IIndividual;
import genetics.api.individual.ISpeciesDefinition;
import genetics.api.root.IIndividualRootBuilder;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.IRootComponentBuilder;

/**
 * A collection of events that are fired by the {@link IIndividualRootBuilder}.
 *
 * @param <I> The type of the individual that the root builder represents.
 */
public class RootBuilderEvents<I extends IIndividual> extends Event {
	private final IIndividualRootBuilder<I> root;
	private final IRootDefinition definition;

	private RootBuilderEvents(IIndividualRootBuilder<I> root) {
		this.root = root;
		this.definition = root.getDefinition();
	}

	/**
	 * Checks if the given definition is the same like the definition of this event.
	 *
	 * @param rootDefinition The definition that should be checked.
	 * @return Checks if the given definition is the same like the definition of this event.
	 */
	public boolean isRoot(IRootDefinition rootDefinition) {
		return definition == rootDefinition;
	}

	/**
	 * The root builder that fired this event.
	 *
	 * @return The root builder that fired this event.
	 */
	public IIndividualRootBuilder<I> getRoot() {
		return root;
	}

	/**
	 * This event gets fired before the build phase of the {@link IIndividualRootBuilder}. For every {@link ISpeciesDefinition}
	 * that gets added with {@link #add(ISpeciesDefinition)} the {@link ISpeciesDefinition#onComponent(ComponentKey, IRootComponentBuilder)}
	 * method gets called later for every {@link IRootComponentBuilder} that was added to the {@link IIndividualRootBuilder}.
	 *
	 * @param <I> The type of the individual that the root builder represents.
	 */
	public static class GatherDefinitions<I extends IIndividual> extends RootBuilderEvents<I> {
		private final List<ISpeciesDefinition> definitions = new LinkedList<>();

		public GatherDefinitions(IIndividualRootBuilder<I> root) {
			super(root);
		}

		public void add(ISpeciesDefinition definition) {
			this.definitions.add(definition);
		}

		public void add(ISpeciesDefinition... definitions) {
			add(Arrays.asList(definitions));
		}

		public void add(Collection<ISpeciesDefinition> definitions) {
			this.definitions.addAll(definitions);
		}

		public List<ISpeciesDefinition> getDefinitions() {
			return Collections.unmodifiableList(definitions);
		}
	}


	/**
	 * This event gets fired before {@link ISpeciesDefinition#onComponent(ComponentKey, IRootComponentBuilder)} was
	 * called for every {@link ISpeciesDefinition} that was added with {@link GatherDefinitions#add(ISpeciesDefinition)}
	 * and before the {@link IRootComponentBuilder} is build.
	 *
	 * @param <I> The type of the individual that the root builder represents.
	 * @param <B> The type of the component builder.
	 */
	public static class BuildComponent<I extends IIndividual, B extends IRootComponentBuilder> extends RootBuilderEvents<I> implements IGenericEvent<B> {

		private final ComponentKey<?, B> key;
		private final B builder;

		public BuildComponent(IIndividualRootBuilder<I> root, ComponentKey<?, B> key, B builder) {
			super(root);
			this.key = key;
			this.builder = builder;
		}

		/**
		 * The component builder this event was for fired for.
		 *
		 * @return The component builder this was event for fired for.
		 */
		public B getBuilder() {
			return builder;
		}

		/**
		 * The component key of the component builder.
		 *
		 * @return The component key of the component builder.
		 */
		public ComponentKey<?, B> getKey() {
			return key;
		}

		@Override
		public Type getGenericType() {
			return key.getBuilderClass();
		}
	}
}
