package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;

//TODO at the moment this just seems to duplicate the logic from superclass?
public class BlockDecorativeWoodPile extends RotatedPillarBlock implements IItemModelRegister {
	//	public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.create("axis", Direction.Axis.class);

	public BlockDecorativeWoodPile() {
		super(Block.Properties.create(Material.WOOD)
				.sound(SoundType.WOOD)
				.hardnessAndResistance(1.5f));
		//		setCreativeTab(ModuleCharcoal.getTag());
		//TODO creative tab
	}

	//TODO hitbox
	//	@Override
	//	public boolean isOpaqueCube(BlockState state) {
	//		return false;
	//	}
	//
	//	@Override
	//	public boolean isNormalCube(BlockState state) {
	//		return false;
	//	}
	//
	//	@Override
	//	public boolean isFullBlock(BlockState state) {
	//		return false;
	//	}

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

	//TODO defer to super for now
	//	@Override
	//	public boolean rotateBlock(net.minecraft.world.World world, BlockPos pos, Direction axis) {
	//		net.minecraft.block.BlockState state = world.getBlockState(pos);
	//		for (net.minecraft.block.properties.IProperty<?> prop : state.getProperties().keySet()) {
	//			if (prop.getName().equals("axis")) {
	//				world.setBlockState(pos, state.cycleProperty(prop));
	//				return true;
	//			}
	//		}
	//		return false;
	//	}
	//
	//	public BlockState withRotation(BlockState state, Rotation rot) {
	//		switch (rot) {
	//			case COUNTERCLOCKWISE_90:
	//			case CLOCKWISE_90:
	//
	//				switch (state.get(AXIS)) {
	//					case X:
	//						return state.with(AXIS, Direction.Axis.Z);
	//					case Z:
	//						return state.with(AXIS, Direction.Axis.X);
	//					default:
	//						return state;
	//				}
	//
	//			default:
	//				return state;
	//		}
	//	}

	//	protected BlockStateContainer createBlockState() {
	//		return new BlockStateContainer(this, AXIS);
	//	}

	protected ItemStack getSilkTouchDrop(BlockState state) {
		return new ItemStack(Item.getItemFromBlock(this));
	}

	//	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
	//		return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).with(AXIS, facing.getAxis());
	//	}

}
