package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import genetics.api.individual.IGenome;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;

/**
 * Genetic leaves with no tile entity, used for worldgen trees.
 * Similar to decorative leaves, but these will drop saplings and can be used for pollination.
 */
public abstract class BlockDefaultLeavesFruit extends BlockAbstractLeaves {
	private static final int VARIANTS_PER_BLOCK = 4;

	public static List<BlockDefaultLeavesFruit> create() {
		List<BlockDefaultLeavesFruit> blocks = new ArrayList<>();
		final int blockCount = PropertyTreeTypeFruit.getBlockCount(VARIANTS_PER_BLOCK);
		for (int blockNumber = 0; blockNumber < blockCount; blockNumber++) {
			final PropertyTreeTypeFruit variant = PropertyTreeTypeFruit.create("variant", blockNumber, VARIANTS_PER_BLOCK);
			BlockDefaultLeavesFruit block = new BlockDefaultLeavesFruit(blockNumber) {
				@Override
				public PropertyTreeTypeFruit getVariant() {
					return variant;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	protected final int blockNumber;

	public BlockDefaultLeavesFruit(int blockNumber) {
		super(Block.Properties.create(Material.LEAVES)
			.hardnessAndResistance(0.2f)
			.sound(SoundType.PLANT)
			.tickRandomly());
		this.blockNumber = blockNumber;
		PropertyTreeTypeFruit variant = getVariant();
		setDefaultState(getStateContainer().getBaseState()
			.with(variant, variant.getFirstType())
			.with(DISTANCE, 7).with(PERSISTENT, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
		super.fillStateContainer(p_206840_1_);
		p_206840_1_.add(getVariant());
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
		ItemStack mainHand = player.getHeldItem(Hand.MAIN_HAND);
		ItemStack offHand = player.getHeldItem(Hand.OFF_HAND);
		if (mainHand.isEmpty() && offHand.isEmpty()) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, state);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			ITree tree = getTree(world, pos);
			if (tree == null) {
				return false;
			}
			IFruitProvider fruitProvider = tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			NonNullList<ItemStack> products = tree.produceStacks(world, pos, fruitProvider.getRipeningPeriod());
			world.setBlockState(pos, ModuleArboriculture.getBlocks().getDefaultLeaves(tree.getIdentifier()), 2);
			for (ItemStack fruit : products) {
				ItemHandlerHelper.giveItemToPlayer(player, fruit);
			}
			return true;
		}

		return false;
	}

	@Override
	protected void getLeafDrop(NonNullList<ItemStack> drops, World world, @Nullable GameProfile playerProfile, BlockPos pos, float saplingModifier, int fortune) {
		ITree tree = getTree(world, pos);
		if (tree == null) {
			return;
		}

		// Add saplings
		List<ITree> saplings = tree.getSaplings(world, playerProfile, pos, saplingModifier);
		for (ITree sapling : saplings) {
			if (sapling != null) {
				drops.add(TreeManager.treeRoot.getTypes().createStack(sapling, EnumGermlingType.SAPLING));
			}
		}

		// Add fruitsk
		IGenome genome = tree.getGenome();
		IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
		if (fruitProvider.isFruitLeaf(genome, world, pos)) {
			NonNullList<ItemStack> produceStacks = tree.produceStacks(world, pos, Integer.MAX_VALUE);
			drops.addAll(produceStacks);
		}
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	protected abstract PropertyTreeTypeFruit getVariant();

	@Nullable
	public PropertyTreeTypeFruit.LeafVariant getLeafVariant(BlockState blockState) {
		if (blockState.getBlock() == this) {
			return blockState.get(getVariant());
		} else {
			return null;
		}
	}

	public PropertyTreeTypeFruit.LeafVariant getTreeType(int meta) {
		int variantCount = getVariant().getAllowedValues().size();
		int variantMeta = (meta % variantCount) + blockNumber * VARIANTS_PER_BLOCK;
		return PropertyTreeTypeFruit.getVariant(variantMeta);
	}

	@Override
	public BlockState getStateForPlacement(BlockState state, Direction facing, BlockState state2, IWorld world, BlockPos pos1, BlockPos pos2, Hand hand) {
		PropertyTreeTypeFruit.LeafVariant type = getTreeType(0);
		return getDefaultState().with(getVariant(), type);
	}

	@Override
	protected ITree getTree(IBlockReader world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		PropertyTreeTypeFruit.LeafVariant treeDefinition = getLeafVariant(blockState);
		if (treeDefinition != null) {
			return treeDefinition.definition.createIndividual();
		} else {
			return null;
		}
	}

	/* RENDERING */
	//	@Override
	//	public final boolean isOpaqueCube(BlockState state) {
	//		if (!Proxies.render.fancyGraphicsEnabled()) {
	//			PropertyTreeTypeFruit.LeafVariant treeDefinition = state.getValue(getVariant());
	//			return !TreeDefinition.Willow.equals(treeDefinition.definition);
	//		}
	//		return false;
	//	}
	//
	//	@Override
	//	@OnlyIn(Dist.CLIENT)
	//	public void registerModel(Item item, IModelManager manager) {
	//		for (BlockState state : blockState.getValidStates()) {
	//			int meta = getMetaFromState(state);
	//			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("forestry:leaves.default." + blockNumber, "inventory"));
	//		}
	//	}

	/* RENDERING */

	@Override
	@OnlyIn(Dist.CLIENT)
	public int colorMultiplier(BlockState state, @Nullable IBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) {
		PropertyTreeTypeFruit.LeafVariant variant = getLeafVariant(state);
		TreeDefinition treeDefinition;
		if (variant != null) {
			treeDefinition = variant.definition;
		} else {
			treeDefinition = TreeDefinition.Oak;
		}
		IGenome genome = treeDefinition.getGenome();
		if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			return fruitProvider.getDecorativeColor();
		}

		ILeafSpriteProvider spriteProvider = genome.getPrimary(IAlleleTreeSpecies.class).getLeafSpriteProvider();
		return spriteProvider.getColor(false);
	}
}
