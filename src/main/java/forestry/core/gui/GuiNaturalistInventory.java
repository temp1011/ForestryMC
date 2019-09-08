/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui;

import javax.annotation.Nullable;
import java.util.HashMap;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.components.ComponentKeys;

import forestry.api.apiculture.IApiaristTracker;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.config.Constants;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.gui.buttons.GuiBetterButton;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.render.ColourProperties;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;

public class GuiNaturalistInventory extends GuiForestry<ContainerNaturalistInventory> {
	private final IForestrySpeciesRoot<IIndividual> speciesRoot;
	private final IBreedingTracker breedingTracker;
	private final HashMap<String, ItemStack> iconStacks = new HashMap<>();
	private final int pageCurrent, pageMax;

	public GuiNaturalistInventory(ContainerNaturalistInventory container, PlayerInventory playerInv, ITextComponent name) {
		super(Constants.TEXTURE_PATH_GUI + "/apiaristinventory.png", container, playerInv, name);

		this.speciesRoot = container.tile.getSpeciesRoot();

		this.pageCurrent = container.getPage();
		this.pageMax = container.getMaxPage();

		xSize = 196;
		ySize = 202;

		for (IIndividual individual : speciesRoot.getIndividualTemplates()) {
			iconStacks.put(individual.getIdentifier(), speciesRoot.getTypes().createStack(individual, speciesRoot.getIconType()));
		}

		breedingTracker = speciesRoot.getBreedingTracker(playerInv.player.world, playerInv.player.getGameProfile());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		super.drawGuiContainerBackgroundLayer(f, i, j);
		String header = Translator.translateToLocal("for.gui.page") + " " + (pageCurrent + 1) + "/" + pageMax;
		getFontRenderer().drawString(header, guiLeft + 95 + textLayout.getCenteredOffset(header, 98), guiTop + 10, ColourProperties.INSTANCE.get("gui.title"));

		IIndividual individual = getIndividualAtPosition(i, j);
		if (individual == null) {
			displayBreedingStatistics(10);
		}

		if (individual != null) {
			RenderHelper.enableGUIStandardItemLighting();
			textLayout.startPage();

			displaySpeciesInformation(true, individual.getGenome().getPrimary(), iconStacks.get(individual.getIdentifier()), 10);
			if (!individual.isPureBred(TreeChromosomes.SPECIES)) {
				displaySpeciesInformation(individual.isAnalyzed(), individual.getGenome().getSecondary(), iconStacks.get(individual.getGenome().getSecondary().getRegistryName().toString()), 10);
			}

			textLayout.endPage();
		}
	}

	@Override
	public void init() {
		super.init();

		buttons.add(new GuiBetterButton(guiLeft + 99, guiTop + 7, StandardButtonTextureSets.LEFT_BUTTON_SMALL, b -> {
			if(pageCurrent > 0) {
				flipPage(pageCurrent - 1);
			}
		}));
		buttons.add(new GuiBetterButton(guiLeft + 180, guiTop + 7, StandardButtonTextureSets.RIGHT_BUTTON_SMALL, b -> {
			if(pageCurrent < pageMax - 1) {
				flipPage(pageCurrent + 1);
			}
		}));
	}

	private static void flipPage(int page) {
		NetworkUtil.sendToServer(new PacketGuiSelectRequest(page, 0));
	}

	@Nullable
	private IIndividual getIndividualAtPosition(int x, int y) {
		Slot slot = getSlotAtPosition(x, y);
		if (slot == null) {
			return null;
		}

		if (!slot.getHasStack()) {
			return null;
		}

		if (!slot.getStack().hasTag()) {
			return null;
		}

		if (!speciesRoot.isMember(slot.getStack())) {
			return null;
		}

		return speciesRoot.getTypes().createIndividual(slot.getStack()).orElse(null);
	}

	private void displayBreedingStatistics(int x) {

		textLayout.startPage();

		textLayout.drawLine(Translator.translateToLocal("for.gui.speciescount") + ": " + breedingTracker.getSpeciesBred() + "/" + speciesRoot.getSpeciesCount(), x);
		textLayout.newLine();
		textLayout.newLine();

		if (breedingTracker instanceof IApiaristTracker) {
			IApiaristTracker tracker = (IApiaristTracker) breedingTracker;
			textLayout.drawLine(Translator.translateToLocal("for.gui.queens") + ": " + tracker.getQueenCount(), x);
			textLayout.newLine();

			textLayout.drawLine(Translator.translateToLocal("for.gui.princesses") + ": " + tracker.getPrincessCount(), x);
			textLayout.newLine();

			textLayout.drawLine(Translator.translateToLocal("for.gui.drones") + ": " + tracker.getDroneCount(), x);
			textLayout.newLine();
		}

		textLayout.endPage();
	}

	private void displaySpeciesInformation(boolean analyzed, IAlleleSpecies species, ItemStack iconStack, int x) {

		if (!analyzed) {
			textLayout.drawLine(Translator.translateToLocal("for.gui.unknown"), x);
			return;
		}

		textLayout.drawLine(species.getDisplayName().getFormattedText(), x);
		GuiUtil.drawItemStack(this, iconStack, guiLeft + x + 69, guiTop + textLayout.getLineY() - 2);

		textLayout.newLine();

		// Viable Combinations
		int columnWidth = 16;
		int column = 10;

		IMutationContainer<IIndividual, ? extends IMutation> container = speciesRoot.getComponent(ComponentKeys.MUTATIONS);
		for (IMutation combination : container.getCombinations(species)) {
			if (combination.isSecret()) {
				continue;
			}

			if (breedingTracker.isDiscovered(combination)) {
				drawMutationIcon(combination, species, column);
			} else {
				drawUnknownIcon(combination, column);
			}

			column += columnWidth;
			if (column > 75) {
				column = 10;
				textLayout.newLine(18);
			}
		}

		textLayout.newLine();
		textLayout.newLine();
	}

	private void drawMutationIcon(IMutation combination, IAlleleSpecies species, int x) {
		GuiUtil.drawItemStack(this, iconStacks.get(combination.getPartner(species).getRegistryName().toString()), guiLeft + x, guiTop + textLayout.getLineY());

		int line = 48;
		int column;
		EnumMutateChance chance = EnumMutateChance.rateChance(combination.getBaseChance());
		if (chance == EnumMutateChance.HIGHEST) {
			line += 16;
			column = 228;
		} else if (chance == EnumMutateChance.HIGHER) {
			line += 16;
			column = 212;
		} else if (chance == EnumMutateChance.HIGH) {
			line += 16;
			column = 196;
		} else if (chance == EnumMutateChance.NORMAL) {
			line += 0;
			column = 228;
		} else if (chance == EnumMutateChance.LOW) {
			line += 0;
			column = 212;
		} else {
			line += 0;
			column = 196;
		}

		bindTexture(textureFile);
		blit(guiLeft + x, guiTop + textLayout.getLineY(), column, line, 16, 16);

	}

	private void drawUnknownIcon(IMutation mutation, int x) {

		float chance = mutation.getBaseChance();

		int line;
		int column;
		if (chance >= 20) {
			line = 16;
			column = 228;
		} else if (chance >= 15) {
			line = 16;
			column = 212;
		} else if (chance >= 12) {
			line = 16;
			column = 196;
		} else if (chance >= 10) {
			line = 0;
			column = 228;
		} else if (chance >= 5) {
			line = 0;
			column = 212;
		} else {
			line = 0;
			column = 196;
		}

		bindTexture(textureFile);
		blit(guiLeft + x, guiTop + textLayout.getLineY(), column, line, 16, 16);
	}

	@Override
	protected void addLedgers() {
		addHintLedger("naturalist.chest");
	}
}
