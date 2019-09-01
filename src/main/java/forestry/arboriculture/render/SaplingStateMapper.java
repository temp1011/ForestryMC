package forestry.arboriculture.render;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.render.ForestryStateMapper;

@OnlyIn(Dist.CLIENT)
public class SaplingStateMapper extends ForestryStateMapper {

//	@Override
	//	public Map<BlockState, ModelResourceLocation> putStateModelLocations(Block block) {
	//		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
	//			if (allele instanceof IAlleleTreeSpecies) {
	//				IAlleleTreeSpecies tree = (IAlleleTreeSpecies) allele;
	//				BlockState state = block.getDefaultState().with(BlockSapling.TREE, tree);
	//				LinkedHashMap<IProperty<?>, Comparable<?>> linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
	//				String modID = tree.getModID();
	//				String s = String.format("%s:%s", modID, "germlings");
	//				mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
	//			}
	//		}
	//		return mapStateModelLocations;
	//	}

}
