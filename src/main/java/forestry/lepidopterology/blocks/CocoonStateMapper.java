package forestry.lepidopterology.blocks;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.lepidopterology.IAlleleButterflyCocoon;
import forestry.core.config.Constants;
import forestry.core.render.ForestryStateMapper;
import forestry.lepidopterology.genetics.alleles.AlleleButterflyCocoon;

@OnlyIn(Dist.CLIENT)
public class CocoonStateMapper extends ForestryStateMapper {

//	@Override
//	public Map<BlockState, ModelResourceLocation> putStateModelLocations(Block block) {
//		if (block instanceof BlockCocoon || block instanceof BlockSolidCocoon) {
//			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
//				if (allele instanceof IAlleleButterflyCocoon) {
//					for (int age = 0; age < 3; age++) {
//						IAlleleButterflyCocoon cocoon = (IAlleleButterflyCocoon) allele;
//						String resourcePath = Constants.MOD_ID + ":cocoons/cocoon_" + cocoon.getCocoonName();
//						BlockState state = block.getDefaultState().with(AlleleButterflyCocoon.COCOON, cocoon).with(AlleleButterflyCocoon.AGE, age);
//						String propertyString = "age=" + age;
//						mapStateModelLocations.put(state, new ModelResourceLocation(resourcePath, propertyString));
//					}
//				}
//			}
//		}
//		return mapStateModelLocations;
//	}

}
