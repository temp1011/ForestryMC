package genetics.api.root.components;

import genetics.api.root.IIndividualRoot;
import genetics.api.root.IIndividualRootBuilder;

/**
 * A key that is associated with a specific root component and its builder.
 * <p>
 * It can be used to add a component with {@link IIndividualRootBuilder#addComponent(ComponentKey)} or
 * {@link IIndividualRootBuilder#addComponent(ComponentKey, IRootComponentFactory)}.
 * <p>
 * Also it can be used to get a component with {@link IIndividualRoot#getComponent(ComponentKey)}.
 *
 * @param <C> The type of the component.
 * @param <B> The type of the component builder.
 */
public class ComponentKey<C extends IRootComponent, B extends IRootComponentBuilder> {

	public static <C extends IRootComponent, B extends IRootComponentBuilder> ComponentKey<C, B> create(String name, Class<C> componentClass, Class<B> builderClass) {
		return new ComponentKey<>(name, componentClass, builderClass);
	}

	private final String name;
	private final Class<C> componentClass;
	private final Class<B> builderClass;

	private ComponentKey(String name, Class<C> componentClass, Class<B> builderClass) {
		this.name = name;
		this.componentClass = componentClass;
		this.builderClass = builderClass;
	}

	@SuppressWarnings("unchecked")
	public <R> R cast(C instance) {
		return (R) instance;
	}

	public Class<C> getComponentClass() {
		return componentClass;
	}

	public Class<B> getBuilderClass() {
		return builderClass;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o == this || (o != null && o.toString().equals(name));
	}
}
