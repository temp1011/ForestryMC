package forestry.core.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.api.multiblock.MultiblockTileEntityBase;

public class ForestryTileEntityTypes {

	public static final TileEntityType<MultiblockTileEntityBase<?>> MULTIBLOCK_TILE_ENTITY_BASE_TILE_ENTITY_TYPE = TileEntityType.Builder.create( () -> MultiblockTileEntityBase);
}
