package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

import forestry.core.ModuleCore;
import forestry.core.gui.slots.SlotLockable;
import forestry.core.tiles.TileUtil;

public class ContainerAnalyzerProvider<T extends TileEntity> extends ContainerTile<T> implements IContainerAnalyzerProvider {
	/* Attributes - Final*/
	private final ContainerAnalyzerProviderHelper providerHelper;

	//TODO - check if this constructor is needed. It seems like this may just be a common superclass?
	public static <T extends TileEntity> ContainerAnalyzerProvider<T> fromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
		T tile = (T) TileUtil.getTile(playerInv.player.world, extraData.readBlockPos());
		return new ContainerAnalyzerProvider<>(windowId, playerInv, tile, extraData.readVarInt(), extraData.readVarInt());	//TODO writing packets.
	}

	/* Constructors */
	public ContainerAnalyzerProvider(int windowId, PlayerInventory playerInventory, T tile, int xInv, int yInv) {
		super(windowId, ModuleCore.getContainerTypes().ANALYZER_PROVIDER, playerInventory, tile, xInv, yInv);
		//TODO maybe analyzer container type can be reused?

		providerHelper = new ContainerAnalyzerProviderHelper(this, playerInventory);
	}

	//TODO maybe this is the constructor I need?
	public ContainerAnalyzerProvider(int windowId, ContainerType<?> type, PlayerInventory playerInventory, T tile, int xInv, int yInv) {
		super(windowId, type, playerInventory, tile, xInv, yInv);
		//TODO maybe analyzer container type can be reused?

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
