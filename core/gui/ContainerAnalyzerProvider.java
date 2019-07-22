package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.tileentity.TileEntity;

import forestry.core.gui.slots.SlotLockable;

public class ContainerAnalyzerProvider<T extends TileEntity> extends ContainerTile<T> implements IContainerAnalyzerProvider {
	/* Attributes - Final*/
	private final ContainerAnalyzerProviderHelper providerHelper;

	/* Constructors */
	public ContainerAnalyzerProvider(T tileForestry, PlayerInventory playerInventory, int xInv, int yInv, int id) {	//TODO windowid
		super(tileForestry, playerInventory, xInv, yInv, id);

		providerHelper = new ContainerAnalyzerProviderHelper(this, playerInventory);
	}

	/* Methods - Implement IContainerAnalyzerProvider */
	@Nullable
	public Slot getAnalyzerSlot() {
		return providerHelper.getAnalyzerSlot();
	}

	/* Methods - Implement ContainerForestry */
	@Override
	protected void addSlot(PlayerInventory playerInventory, int slot, int x, int y) {
		addSlot(new SlotLockable(playerInventory, slot, x, y));
	}

	@Override
	protected void addHotbarSlot(PlayerInventory playerInventory, int slot, int x, int y) {
		addSlot(new SlotLockable(playerInventory, slot, x, y));
	}

	/* Methods - Implement IGuiSelectable */
	@Override
	public void handleSelectionRequest(ServerPlayerEntity player, int primary, int secondary) {
		providerHelper.analyzeSpecimen(secondary);
	}
}
