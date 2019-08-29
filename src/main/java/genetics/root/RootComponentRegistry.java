package genetics.root;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.ComponentKeys;
import genetics.api.root.components.IRootComponentFactory;
import genetics.api.root.components.IRootComponentRegistry;

import genetics.organism.OrganismTypesBuilder;

public enum RootComponentRegistry implements IRootComponentRegistry {
	INSTANCE;

	private final Map<ComponentKey, IRootComponentFactory> factoryByKey = new HashMap<>();

	RootComponentRegistry() {
		registerFactory(ComponentKeys.TEMPLATES, TemplateRegistry::new);
		registerFactory(ComponentKeys.TYPES, OrganismTypesBuilder::new);
		registerFactory(ComponentKeys.TRANSLATORS, IndividualTranslatorBuilder::new);
		registerFactory(ComponentKeys.MUTATIONS, MutationRegistry::new);
	}

	@Override
	public void registerFactory(ComponentKey key, IRootComponentFactory factory) {
		factoryByKey.put(key, factory);
	}

	@Nullable
	@Override
	public IRootComponentFactory getFactory(ComponentKey key) {
		return factoryByKey.get(key);
	}
}
