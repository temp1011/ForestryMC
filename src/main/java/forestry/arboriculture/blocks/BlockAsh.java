package forestry.arboriculture.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IntegerProperty;
import net.minecraft.block.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.models.IStateMapperRegister;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;

public class BlockAsh extends Block implements IStateMapperRegister, IItemModelRegister {

	public static final IntegerProperty AMOUNT = IntegerProperty.create("amount", 0, 15);

	private final int startAmount;

	public BlockAsh(int startAmount) {
		super(Block.Properties.create(Material.EARTH, MaterialColor.BLACK)
		.sound(SoundType.SAND));
//		setHarvestLevel("shovel", 0);	//TODO harvestlevel
		this.startAmount = startAmount;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AMOUNT);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < 16; i++) {
			manager.registerItemModel(item, i, "ash_block");
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		Random rand = world instanceof World ? ((World) world).rand : new Random();
		int amount = startAmount + state.get(AMOUNT);
		if (amount > 0) {
			if (fortune > 0) {
				amount += rand.nextInt(1 + fortune);
			}
			drops.add(new ItemStack(Items.CHARCOAL, amount));
			drops.add(new ItemStack(ModuleCore.getItems().ash, 1 + rand.nextInt(amount / 4)));
		}
	}
}
