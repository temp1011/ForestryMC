package forestry.arboriculture.items;

import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.IWoodType;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.blocks.BlockForestrySlab;

//TODO vanilla just uses a standard blockitem.
public class ItemBlockWoodSlab extends ItemSlab {
	public ItemBlockWoodSlab(BlockForestrySlab block, BlockForestrySlab slab, BlockForestrySlab doubleSlab) {
		super(block, slab, doubleSlab);
	}

	@Override
	public String getDisplayName(ItemStack itemstack) {
		BlockForestrySlab<?> wood = (BlockForestrySlab) getBlock();
		int meta = itemstack.getMetadata();
		IWoodType woodType = wood.getWoodType(meta);
		return WoodHelper.getDisplayName(wood, woodType);
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		BlockForestrySlab forestrySlab = (BlockForestrySlab) this.block;
		if (forestrySlab.isFireproof()) {
			return 0;
		} else {
			return 150;
		}
	}
}
