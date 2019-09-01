package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.items.ItemHandlerHelper;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.proxy.Proxies;
import forestry.core.utils.NetworkUtil;

/**
 * Genetic leaves with no tile entity, used for worldgen trees.
 * Similar to decorative leaves, but these will drop saplings and can be used for pollination.
 */
public class BlockDefaultLeavesFruit extends BlockAbstractLeaves {

	private TreeDefinition definition;

	public BlockDefaultLeavesFruit(TreeDefinition definition) {
		super(Block.Properties.create(Material.LEAVES)
				.hardnessAndResistance(0.2F)
				.tickRandomly()
				.sound(SoundType.PLANT));
		this.definition = definition;
	}

	//TODO probably can be method in superclass
	public TreeDefinition getDefinition() {
		return definition;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack mainHand = player.getHeldItem(Hand.MAIN_HAND);
		ItemStack offHand = player.getHeldItem(Hand.OFF_HAND);
		if (mainHand.isEmpty() && offHand.isEmpty()) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, state);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			ITree tree = getTree(world, pos);
			if (tree == null) {
				return false;
			}
			IFruitProvider fruitProvider = tree.getGenome().getFruitProvider();
			NonNullList<ItemStack> products = tree.produceStacks(world, pos, fruitProvider.getRipeningPeriod());
			world.setBlockState(pos, ModuleArboriculture.getBlocks().getDefaultLeaves(tree.getIdent()), 2);
			for (ItemStack fruit : products) {
				ItemHandlerHelper.giveItemToPlayer(player, fruit);
			}
			return true;
		}

		return false;
	}

	//TODO loot table?
	@Override
	protected void getLeafDrop(NonNullList<ItemStack> drops, World world, @Nullable GameProfile playerProfile, BlockPos pos, float saplingModifier, int fortune) {
		ITree tree = getTree(world, pos);
		if (tree == null) {
			return;
		}

		if(world.isRemote) {	//TODO need server because this may access world saved data
			return;
		}

		// Add saplings
		List<ITree> saplings = tree.getSaplings((ServerWorld) world, playerProfile, pos, saplingModifier);
		for (ITree sapling : saplings) {
			if (sapling != null) {
				drops.add(TreeManager.treeRoot.getMemberStack(sapling, EnumGermlingType.SAPLING));
			}
		}

		// Add fruitsk
		ITreeGenome genome = tree.getGenome();
		IFruitProvider fruitProvider = genome.getFruitProvider();
		if (fruitProvider.isFruitLeaf(genome, world, pos)) {
			NonNullList<ItemStack> produceStacks = tree.produceStacks(world, pos, Integer.MAX_VALUE);
			drops.addAll(produceStacks);
		}
	}

	@Override
	protected ITree getTree(IBlockReader world, BlockPos pos) {
			return definition.getIndividual();
	}

	//TODO hitbox/rendering
//	/* RENDERING */
//	@Override
//	public final boolean isOpaqueCube(BlockState state) {
//		if (!Proxies.render.fancyGraphicsEnabled()) {
//			PropertyTreeTypeFruit.LeafVariant treeDefinition = state.getValue(getVariant());
//			return !TreeDefinition.Willow.equals(treeDefinition.definition);
//		}
//		return false;
//	}

	/* RENDERING */

	@Override
	@OnlyIn(Dist.CLIENT)
	public int colorMultiplier(BlockState state, @Nullable IBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) {
		ITreeGenome genome = definition.getGenome();
		if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			IFruitProvider fruitProvider = genome.getFruitProvider();
			return fruitProvider.getDecorativeColor();
		}

		ILeafSpriteProvider spriteProvider = genome.getPrimary().getLeafSpriteProvider();
		return spriteProvider.getColor(false);
	}
}
