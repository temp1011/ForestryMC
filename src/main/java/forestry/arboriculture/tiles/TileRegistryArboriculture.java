package forestry.arboriculture.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.blocks.BlockRegistryArboriculture;
import forestry.core.tiles.TileRegistry;

public class TileRegistryArboriculture extends TileRegistry {


	public final TileEntityType<TileArboristChest> ARBORIST_CHEST;
	public final TileEntityType<TileFruitPod> FRUIT_POD;
	public final TileEntityType<TileLeaves> LEAVES;
	public final TileEntityType<TileSapling> SAPLING;

	public TileRegistryArboriculture() {
		BlockRegistryArboriculture blocks = ModuleArboriculture.getBlocks();

		ARBORIST_CHEST = registerTileEntityType(TileArboristChest::new, "arborist_chest", blocks.treeChest);
		FRUIT_POD = registerTileEntityType(TileFruitPod::new, "fruit_pod", blocks.podsMap.values());
		LEAVES = registerTileEntityType(TileLeaves::new, "leaves", blocks.leaves.values());
		SAPLING = registerTileEntityType(TileSapling::new, "sapling", blocks.saplingGE);
	}
}
