package forestry.apiculture.blocks;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
//import net.minecraft.block.BlockStateContainer;
//import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.core.blocks.IBlockWithMeta;
import forestry.core.blocks.IColoredBlock;
import forestry.core.config.Config;

public class BlockHoneyComb extends Block implements IColoredBlock {
	public final EnumHoneyComb type;

	public BlockHoneyComb(EnumHoneyComb type) {
		super(Block.Properties.create(Material.WOOL)
		.hardnessAndResistance(1F));
//		setCreativeTab(ItemGroups.tabApiculture); TODO - done in item
		this.type = type;
	}

	public EnumHoneyComb getType() {
		return type;
	}

	@Override
	public String getTranslationKey() {
		return "tile.for.bee_combs";
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

//	@OnlyIn(Dist.CLIENT)
//	@Override
//	public void registerModel(Item item, IModelManager manager) {
//		for (EnumHoneyComb comb : getVariant().getAllowedValues()) {
//			manager.registerItemModel(item, comb.ordinal() - minMeta, "block_bee_combs");
//		}
//	}

	//TODO how this is used???
	@OnlyIn(Dist.CLIENT)
	@Override
	public int colorMultiplier(BlockState state, IBlockReader worldIn, BlockPos pos, int tintIndex) {
		EnumHoneyComb honeyComb = type;
		if (tintIndex == 1) {
			return honeyComb.primaryColor;
		} else {
			return honeyComb.secondaryColor;
		}
	}
}
