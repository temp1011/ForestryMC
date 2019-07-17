package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.ModuleCore;
import forestry.core.gui.slots.SlotAnalyzer;
import forestry.core.gui.slots.SlotLockable;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.utils.GeneticsUtil;
//import forestry.database.inventory.InventoryDatabaseAnalyzer;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

public class ContainerAnalyzerProviderHelper {
	/* Attributes - Final*/
	private final PlayerEntity player;
	private final ContainerForestry container;
	@Nullable
	private final ItemInventoryAlyzer alyzerInventory;

	public ContainerAnalyzerProviderHelper(ContainerForestry container, PlayerInventory playerInventory) {
		this.player = playerInventory.player;
		this.container = container;

		ItemInventoryAlyzer alyzerInventory = null;
		int analyzerIndex = -1;
		for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
			ItemStack stack = playerInventory.getStackInSlot(i);
			if (stack.isEmpty() || stack.getItem() != ModuleCore.getItems().portableAlyzer) {
				continue;
			}
			analyzerIndex = i;
			alyzerInventory = new ItemInventoryAlyzer(playerInventory.player, stack);
			Slot slot = container.getSlot(i);	//TODO - probably not right
			if (slot instanceof SlotLockable) {
				SlotLockable lockable = (SlotLockable) slot;
				lockable.lock();
			}
			break;
		}
		int analyzerIndex1 = analyzerIndex;
		this.alyzerInventory = alyzerInventory;

		if (alyzerInventory != null) {
			container.addSlot(new SlotAnalyzer(alyzerInventory, ItemInventoryAlyzer.SLOT_ENERGY, -110, 20));
		}
	}

	@Nullable
	public Slot getAnalyzerSlot() {
		if (alyzerInventory == null) {
			return null;
		}
		return container.getSlot(0);	//TODO - not sure about this
	}

	public void analyzeSpecimen(int selectedSlot) {
		if (selectedSlot < 0 || alyzerInventory == null) {
			return;
		}
		Slot specimenSlot = container.getForestrySlot(selectedSlot);
		ItemStack specimen = specimenSlot.getStack();
		if (specimen.isEmpty()) {
			return;
		}

		ItemStack convertedSpecimen = GeneticsUtil.convertToGeneticEquivalent(specimen);
		if (!ItemStack.areItemStacksEqual(specimen, convertedSpecimen)) {
			specimenSlot.putStack(convertedSpecimen);
			specimen = convertedSpecimen;
		}

		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(specimen);

		// No individual, abort
		if (speciesRoot == null) {
			return;
		}

		IIndividual individual = speciesRoot.getMember(specimen);

		// Analyze if necessary
		if (individual != null && !individual.isAnalyzed()) {
			final boolean requiresEnergy = ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE);
			ItemStack energyStack = ItemStack.EMPTY;//alyzerInventory.getStackInSlot(InventoryDatabaseAnalyzer.SLOT_ENERGY);
			if (requiresEnergy && !ItemInventoryAlyzer.isAlyzingFuel(energyStack)) {
				return;
			}

			if (individual.analyze()) {
				IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.world, player.getGameProfile());
				breedingTracker.registerSpecies(individual.getGenome().getPrimary());
				breedingTracker.registerSpecies(individual.getGenome().getSecondary());

				CompoundNBT CompoundNBT = new CompoundNBT();
				individual.writeToNBT(CompoundNBT);
				specimen = specimen.copy();
				specimen.setTag(CompoundNBT);

				if (requiresEnergy) {
					// Decrease energy
					//TODO energy
//					alyzerInventory.decrStackSize(InventoryDatabaseAnalyzer.SLOT_ENERGY, 1);
				}
			}
			specimenSlot.putStack(specimen);
		}
		return;
	}
}