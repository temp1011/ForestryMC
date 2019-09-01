//package forestry.arboriculture.items;
//
//import net.minecraft.item.BlockItem;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemSlab;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.text.ITextComponent;
//
//import forestry.api.arboriculture.IWoodType;
//import forestry.api.core.ItemGroups;
//import forestry.arboriculture.WoodHelper;
//import forestry.arboriculture.blocks.BlockForestrySlab;
////TODO unused???
////TODO vanilla just uses a standard blockitem.
//public class ItemBlockWoodSlab extends BlockItem {
//	public ItemBlockWoodSlab(BlockForestrySlab block, BlockForestrySlab slab, BlockForestrySlab doubleSlab) {
//		super(block, (new Item.Properties()).group(ItemGroups.tabArboriculture));	//TODO creative tabs
//	}
//
//	@Override
//	public ITextComponent getDisplayName(ItemStack itemstack) {
//		BlockForestrySlab wood = (BlockForestrySlab) getBlock();
//		IWoodType woodType = wood.getWoodType();
//		return WoodHelper.getDisplayName(wood, woodType);
//	}
//
//	@Override
//	public int getBurnTime(ItemStack itemStack) {
//		BlockForestrySlab forestrySlab = (BlockForestrySlab) this.block;
//		if (forestrySlab.isFireproof()) {
//			return 0;
//		} else {
//			return 150;
//		}
//	}
//}
