package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;

public class BlockDecorativeWoodPile extends RotatedPillarBlock implements IItemModelRegister {
	public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.create("axis", Direction.Axis.class);

	public BlockDecorativeWoodPile() {
		super(Block.Properties.create(Material.WOOD)
		.sound(SoundType.WOOD)
		.hardnessAndResistance(1.5f));
//		setCreativeTab(ModuleCharcoal.getTag());
		//TODO creative tab
	}

	@Override
	public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
		return false;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 12;
	}

	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return true;
	}

	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 25;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < 3; i++) {
			manager.registerItemModel(item, i, "wood_pile");
		}
	}


	protected ItemStack getSilkTouchDrop(BlockState state) {
		return new ItemStack(Item.getItemFromBlock(this));
	}

}
