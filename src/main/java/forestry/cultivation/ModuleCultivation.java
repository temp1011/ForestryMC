package forestry.cultivation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.util.Set;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.cultivation.blocks.BlockRegistryCultivation;
import forestry.cultivation.gui.CultivationContainerTypes;
import forestry.cultivation.gui.GuiPlanter;
import forestry.cultivation.tiles.TileRegistryCultivation;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

import afu.org.checkerframework.checker.oigj.qual.O;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CULTIVATION, name = "Cultivation", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.cultivation.description")
public class ModuleCultivation extends BlankForestryModule {
	@Nullable
	private static BlockRegistryCultivation blocks;
	@Nullable
	private static TileRegistryCultivation tiles;
	@Nullable
	private static CultivationContainerTypes containerTypes;

	public static BlockRegistryCultivation getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	public static TileRegistryCultivation getTiles() {
		Preconditions.checkNotNull(tiles);
		return tiles;
	}

	public static CultivationContainerTypes getContainerTypes() {
		Preconditions.checkNotNull(containerTypes);
		return containerTypes;
	}

	@Override
	public void registerBlocks() {
		blocks = new BlockRegistryCultivation();
	}

	@Override
	public void registerTiles() {
		tiles = new TileRegistryCultivation();
	}

	@Override
	public void registerContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		containerTypes = new CultivationContainerTypes(registry);
	}

	@Override
	public void registerGuiFactories() {
		ScreenManager.registerFactory(getContainerTypes().PLANTER, GuiPlanter::new);
	}

	@Override
	public Set<ResourceLocation> getDependencyUids() {
		return ImmutableSet.of(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CORE),
				new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FARMING));
	}

	@Override
	public void doInit() {
		BlockRegistryCultivation blocks = getBlocks();

		blocks.arboretum.init();
		blocks.farmCrops.init();
		blocks.farmMushroom.init();
		blocks.farmNether.init();
		blocks.farmGourd.init();
		blocks.farmEnder.init();
		//blocks.plantation.init();
		blocks.peatBog.init();
	}
}