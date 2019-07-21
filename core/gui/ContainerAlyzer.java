package forestry.core.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;

import forestry.core.gui.slots.SlotFiltered;
import forestry.core.inventory.ItemInventoryAlyzer;

public class ContainerAlyzer extends ContainerItemInventory<ItemInventoryAlyzer> {

	public ContainerAlyzer(ItemInventoryAlyzer inventory, PlayerEntity player, int id) {
		super(inventory, player.inventory, 43, 156, ContainerType.BLAST_FURNACE, id);	//TODO - container types

		final int xPosLeftSlots = 223;

		addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ENERGY, xPosLeftSlots, 8));

		addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_SPECIMEN, xPosLeftSlots, 26));

		addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_1, xPosLeftSlots, 57));
		addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_2, xPosLeftSlots, 75));
		addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_3, xPosLeftSlots, 93));
		addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_4, xPosLeftSlots, 111));
		addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_5, xPosLeftSlots, 129));
	}
}
