package forestry.core.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

public class CoreContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerAlyzer> ALYZER;
	public final ContainerType<ContainerAnalyzer> ANALYZER;
	public final ContainerType<ContainerAnalyzerProvider<?>> ANALYZER_PROVIDER;
	public final ContainerType<ContainerEscritoire> ESCRITOIRE;
	public final ContainerType<ContainerNaturalistInventory> NATURALIST_INVENTORY;

	public CoreContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);
		ALYZER = register(ContainerAlyzer::fromNetwork, "alyzer");
		ANALYZER = register(ContainerAnalyzer::fromNetwork, "analyzer");
		ANALYZER_PROVIDER = register(ContainerAnalyzerProvider::fromNetwork, "analyzer_provider");
		ESCRITOIRE = register(ContainerEscritoire::fromNetwork, "escritoire");
		NATURALIST_INVENTORY = register(ContainerNaturalistInventory::fromNetwork, "naturalist_inventory");
	}
}
