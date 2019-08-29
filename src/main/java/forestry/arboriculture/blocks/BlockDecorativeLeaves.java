package forestry.arboriculture.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IShearable;

import genetics.api.individual.IGenome;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.blocks.IColoredBlock;
import forestry.core.proxy.Proxies;

//TODO shearing
public class BlockDecorativeLeaves extends Block implements IColoredBlock, IShearable {
	private TreeDefinition definition;

	public BlockDecorativeLeaves(TreeDefinition definition) {
		super(Block.Properties.create(Material.LEAVES)
				.hardnessAndResistance(0.2f)
				.sound(SoundType.PLANT));
		//		this.setCreativeTab(Tabs.tabArboriculture);
		//		this.setLightOpacity(1);	//TODO block stuff);
		this.definition = definition;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (TreeDefinition.Willow.equals(definition)) {
			return VoxelShapes.empty();
		}
		return super.getCollisionShape(state, worldIn, pos, context);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		entityIn.motionX *= 0.4D;
		entityIn.motionZ *= 0.4D;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		if (!Proxies.render.fancyGraphicsEnabled()) {
			return !TreeDefinition.Willow.equals(definition);
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
		return (Proxies.render.fancyGraphicsEnabled() || blockAccess.getBlockState(pos.offset(side)).getBlock() != this) && Block.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED; // fruit overlays require CUTOUT_MIPPED, even in Fast graphics
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {

	}

	/* PROPERTIES */
	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		if (face == Direction.DOWN) {
			return 20;
		} else if (face != Direction.UP) {
			return 10;
		} else {
			return 5;
		}
	}

	@Override
	public boolean isShearable(@Nonnull ItemStack item, IWorldReader world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> onSheared(@Nonnull ItemStack item, IWorld world, BlockPos pos, int fortune) {
		BlockState state = world.getBlockState(pos);
		return Collections.singletonList(new ItemStack(this));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int colorMultiplier(BlockState state, @Nullable IBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) {
		IGenome genome = definition.getGenome();

		if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			return fruitProvider.getDecorativeColor();
		}
		ILeafSpriteProvider spriteProvider = genome.getPrimary(IAlleleTreeSpecies.class).getLeafSpriteProvider();
		return spriteProvider.getColor(false);
	}
}
