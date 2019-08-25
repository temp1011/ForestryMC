package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.blocks.IColoredBlock;
import forestry.core.proxy.Proxies;

public abstract class BlockDecorativeLeaves extends Block implements IItemModelRegister, IColoredBlock, IShearable {
	private static final int VARIANTS_PER_BLOCK = 16;

	public static List<BlockDecorativeLeaves> create() {
		List<BlockDecorativeLeaves> blocks = new ArrayList<>();
		final int blockCount = PropertyTreeType.getBlockCount(VARIANTS_PER_BLOCK);
		for (int blockNumber = 0; blockNumber < blockCount; blockNumber++) {
			final PropertyTreeType variant = PropertyTreeType.create("variant", blockNumber, VARIANTS_PER_BLOCK);
			BlockDecorativeLeaves block = new BlockDecorativeLeaves(blockNumber) {
				@Override
				public PropertyTreeType getVariant() {
					return variant;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	private final int blockNumber;

	public BlockDecorativeLeaves(int blockNumber) {
		super(Block.Properties.create(Material.LEAVES)
				.hardnessAndResistance(0.2f)
				.sound(SoundType.PLANT));
//		this.setCreativeTab(Tabs.tabArboriculture);
//		this.setLightOpacity(1);	//TODO block stuff
		this.blockNumber = blockNumber;
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	public abstract PropertyTreeType getVariant();

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getVariant());
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> list) {
		for (BlockState state : getStateContainer().getValidStates()) {
			int meta = getMetaFromState(state);
			ItemStack itemStack = new ItemStack(this, 1, meta);
			list.add(itemStack);
		}
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
		TreeDefinition treeDefinition = blockState.getValue(getVariant());
		if (TreeDefinition.Willow.equals(treeDefinition)) {
			return null;
		}
		return super.getCollisionBoundingBox(blockState, worldIn, pos);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		entityIn.motionX *= 0.4D;
		entityIn.motionZ *= 0.4D;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		if (!Proxies.render.fancyGraphicsEnabled()) {
			TreeDefinition treeDefinition = state.getValue(getVariant());
			return !TreeDefinition.Willow.equals(treeDefinition);
		}
		return false;
	}

	@Override
	public boolean causesSuffocation(BlockState state) {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return (Proxies.render.fancyGraphicsEnabled() || blockAccess.getBlockState(pos.offset(side)).getBlock() != this) && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED; // fruit overlays require CUTOUT_MIPPED, even in Fast graphics
	}

	/* MODELS */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		for (BlockState state : getStateContainer().getValidStates()) {
			int meta = getMetaFromState(state);
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("forestry:leaves.decorative." + blockNumber, "inventory"));
		}
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		TreeDefinition type = getTreeType(meta);
		return getDefaultState().with(getVariant(), type);
	}

	public TreeDefinition getTreeType(int meta) {
		int variantCount = getVariant().getAllowedValues().size();
		int variantMeta = (meta % variantCount) + blockNumber * VARIANTS_PER_BLOCK;
		return TreeDefinition.byMetadata(variantMeta);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return damageDropped(state);
	}

	@Override
	public int damageDropped(BlockState state) {
		return state.getValue(getVariant()).getMetadata() - blockNumber * VARIANTS_PER_BLOCK;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {

	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer, Hand hand) {
		TreeDefinition type = getTreeType(meta);
		return getDefaultState().with(getVariant(), type);
	}

	/* PROPERTIES */
	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public boolean isFlammable(IBlockReader world, BlockPos pos, Direction face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		if (face == Direction.DOWN) {
			return 20;
		} else if (face != Direction.UP) {
			return 10;
		} else {
			return 5;
		}
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockReader world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
		BlockState state = world.getBlockState(pos);
		return Collections.singletonList(new ItemStack(this, 1, damageDropped(state)));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int colorMultiplier(BlockState state, @Nullable IBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) {
		TreeDefinition treeDefinition = state.get(getVariant());
		ITreeGenome genome = treeDefinition.getGenome();

		if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			IFruitProvider fruitProvider = genome.getFruitProvider();
			return fruitProvider.getDecorativeColor();
		}
		ILeafSpriteProvider spriteProvider = genome.getPrimary().getLeafSpriteProvider();
		return spriteProvider.getColor(false);
	}
}
