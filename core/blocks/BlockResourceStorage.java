package forestry.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.EnumProperty;
import net.minecraft.block.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.NonNullList;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;

public class BlockResourceStorage extends Block implements IItemModelRegister, IBlockWithMeta {
	public static final EnumProperty<EnumResourceType> STORAGE_RESOURCES = EnumProperty.create("resource", EnumResourceType.class);

	public BlockResourceStorage() {
		super(Block.Properties.create(Material.IRON)
				.hardnessAndResistance(3f, 5f));
//		setCreativeTab(CreativeTabForestry.tabForestry);
		setDefaultState(this.getStateContainer().getBaseState().with(STORAGE_RESOURCES, EnumResourceType.APATITE));
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> list) {
		for (EnumResourceType resourceType : EnumResourceType.VALUES) {
			list.add(get(resourceType));
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (EnumResourceType resourceType : EnumResourceType.VALUES) {
			manager.registerItemModel(item, resourceType.getMeta(), "storage/" + resourceType.getName());
		}
	}

	//TODO - flatten
	public ItemStack get(EnumResourceType type) {
		return new ItemStack(this, 1);
	}
}
