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
package forestry.arboriculture.genetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.config.Config;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.gui.GuiAlyzer;
import forestry.core.gui.TextLayoutHelper;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

public class TreeAlyzerPlugin implements IAlyzerPlugin {
	public static final TreeAlyzerPlugin INSTANCE = new TreeAlyzerPlugin();

	protected final Map<String, ItemStack> iconStacks = new HashMap<>();

	private TreeAlyzerPlugin() {
		NonNullList<ItemStack> treeList = NonNullList.create();
		ModuleArboriculture.getItems().sapling.addCreativeItems(treeList, false);
		for (ItemStack treeStack : treeList) {
			IAlleleTreeSpecies species = TreeGenome.getSpecies(treeStack);
			iconStacks.put(species.getUID(), treeStack);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawAnalyticsPage1(Screen gui, ItemStack itemStack) {
		if (gui instanceof GuiAlyzer) {
			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
			ITree tree = TreeManager.treeRoot.getMember(itemStack);
			if (tree == null) {
				return;
			}
			EnumGermlingType type = TreeManager.treeRoot.getType(itemStack);
			if (type == null) {
				return;
			}

			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

			textLayout.drawLine(Translator.translateToLocal("for.gui.active"), GuiAlyzer.COLUMN_1);
			textLayout.drawLine(Translator.translateToLocal("for.gui.inactive"), GuiAlyzer.COLUMN_2);

			textLayout.newLine();
			textLayout.newLine();

			{
				String customPrimaryTreeKey = "trees.custom.treealyzer." + type.getName() + "." + tree.getGenome().getPrimary().getUnlocalizedName().replace("trees.species.", "");
				String customSecondaryTreeKey = "trees.custom.treealyzer." + type.getName() + "." + tree.getGenome().getSecondary().getUnlocalizedName().replace("trees.species.", "");

				guiAlyzer.drawSpeciesRow(Translator.translateToLocal("for.gui.species"), tree, TreeChromosomes.SPECIES, GuiAlyzer.checkCustomName(customPrimaryTreeKey), GuiAlyzer.checkCustomName(customSecondaryTreeKey));
				textLayout.newLine();
			}

			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.saplings"), tree, TreeChromosomes.FERTILITY);
			textLayout.newLineCompressed();
			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.maturity"), tree, TreeChromosomes.MATURATION);
			textLayout.newLineCompressed();
			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.height"), tree, TreeChromosomes.HEIGHT);
			textLayout.newLineCompressed();

			IAlleleInteger activeGirth = (IAlleleInteger) tree.getGenome().getActiveAllele(TreeChromosomes.GIRTH);
			IAlleleInteger inactiveGirth = (IAlleleInteger) tree.getGenome().getInactiveAllele(TreeChromosomes.GIRTH);
			textLayout.drawLine(Translator.translateToLocal("for.gui.girth"), GuiAlyzer.COLUMN_0);
			guiAlyzer.drawLine(String.format("%sx%s", activeGirth.getValue(), activeGirth.getValue()), GuiAlyzer.COLUMN_1, tree, TreeChromosomes.GIRTH, false);
			guiAlyzer.drawLine(String.format("%sx%s", inactiveGirth.getValue(), inactiveGirth.getValue()), GuiAlyzer.COLUMN_2, tree, TreeChromosomes.GIRTH, true);

			textLayout.newLineCompressed();

			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.yield"), tree, TreeChromosomes.YIELD);
			textLayout.newLineCompressed();
			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.sappiness"), tree, TreeChromosomes.SAPPINESS);
			textLayout.newLineCompressed();

			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.effect"), tree, TreeChromosomes.EFFECT);

			textLayout.endPage();
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawAnalyticsPage2(Screen gui, ItemStack itemStack) {
		if (gui instanceof GuiAlyzer) {
			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
			ITree tree = TreeManager.treeRoot.getMember(itemStack);
			if (tree == null) {
				return;
			}

			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

			int speciesDominance0 = guiAlyzer.getColorCoding(tree.getGenome().getPrimary().isDominant());
			int speciesDominance1 = guiAlyzer.getColorCoding(tree.getGenome().getSecondary().isDominant());

			textLayout.drawLine(Translator.translateToLocal("for.gui.active"), GuiAlyzer.COLUMN_1);
			textLayout.drawLine(Translator.translateToLocal("for.gui.inactive"), GuiAlyzer.COLUMN_2);

			textLayout.newLine();
			textLayout.newLine();

			String yes = Translator.translateToLocal("for.yes");
			String no = Translator.translateToLocal("for.no");

			String fireproofActive = StringUtil.readableBoolean(tree.getGenome().getFireproof(), yes, no);
			String fireproofInactive = StringUtil.readableBoolean(((AlleleBoolean) tree.getGenome().getInactiveAllele(TreeChromosomes.FIREPROOF)).getValue(), yes, no);

			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.fireproof"), fireproofActive, fireproofInactive, tree, TreeChromosomes.FIREPROOF);

			textLayout.newLine();

			textLayout.drawLine(Translator.translateToLocal("for.gui.native"), GuiAlyzer.COLUMN_0);
			textLayout.drawLine(Translator.translateToLocal("for.gui." + tree.getGenome().getPrimary().getPlantType().toString().toLowerCase(Locale.ENGLISH)), GuiAlyzer.COLUMN_1,
				speciesDominance0);
			textLayout.drawLine(Translator.translateToLocal("for.gui." + tree.getGenome().getSecondary().getPlantType().toString().toLowerCase(Locale.ENGLISH)), GuiAlyzer.COLUMN_2,
				speciesDominance1);

			textLayout.newLine();

			// FRUITS
			textLayout.drawLine(Translator.translateToLocal("for.gui.supports"), GuiAlyzer.COLUMN_0);
			List<IFruitFamily> families0 = new ArrayList<>(tree.getGenome().getPrimary().getSuitableFruit());
			List<IFruitFamily> families1 = new ArrayList<>(tree.getGenome().getSecondary().getSuitableFruit());

			int max = Math.max(families0.size(), families1.size());
			for (int i = 0; i < max; i++) {
				if (i > 0) {
					textLayout.newLineCompressed();
				}

				if (families0.size() > i) {
					textLayout.drawLine(families0.get(i).getName(), GuiAlyzer.COLUMN_1, speciesDominance0);
				}
				if (families1.size() > i) {
					textLayout.drawLine(families1.get(i).getName(), GuiAlyzer.COLUMN_2, speciesDominance1);
				}

			}

			textLayout.newLine();

			int fruitDominance0 = guiAlyzer.getColorCoding(tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS).isDominant());
			int fruitDominance1 = guiAlyzer.getColorCoding(tree.getGenome().getInactiveAllele(TreeChromosomes.FRUITS).isDominant());

			textLayout.drawLine(Translator.translateToLocal("for.gui.fruits"), GuiAlyzer.COLUMN_0);
			String strike = "";
			IAllele fruit0 = tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS);
			if (!tree.canBearFruit() && fruit0 != AlleleFruits.fruitNone) {
				strike = TextFormatting.STRIKETHROUGH.toString();
			}
			textLayout.drawLine(strike + tree.getGenome().getFruitProvider().getDescription(), GuiAlyzer.COLUMN_1, fruitDominance0);

			strike = "";
			IAlleleFruit fruit1 = tree.getGenome().getInactiveAllele(TreeChromosomes.FRUITS);
			if (!tree.getGenome().getSecondary().getSuitableFruit().contains(fruit1.getProvider().getFamily()) && fruit1 != AlleleFruits.fruitNone) {
				strike = TextFormatting.STRIKETHROUGH.toString();
			}
			textLayout.drawLine(strike + fruit1.getProvider().getDescription(), GuiAlyzer.COLUMN_2, fruitDominance1);

			textLayout.newLine();

			textLayout.drawLine(Translator.translateToLocal("for.gui.family"), GuiAlyzer.COLUMN_0);
			IFruitFamily primary = tree.getGenome().getFruitProvider().getFamily();
			IFruitFamily secondary = tree.getGenome().getInactiveAllele(TreeChromosomes.FRUITS).getProvider().getFamily();

			if (primary != null && !primary.getUID().equals(EnumFruitFamily.NONE.getUID())) {
				textLayout.drawLine(primary.getName(), GuiAlyzer.COLUMN_1, fruitDominance0);
			}
			if (secondary != null && !secondary.getUID().equals(EnumFruitFamily.NONE.getUID())) {
				textLayout.drawLine(secondary.getName(), GuiAlyzer.COLUMN_2, fruitDominance1);
			}

			textLayout.endPage();
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawAnalyticsPage3(Screen gui, ItemStack itemStack) {
		if (gui instanceof GuiAlyzer) {
			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
			ITree tree = TreeManager.treeRoot.getMember(itemStack);
			if (tree == null) {
				return;
			}

			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();
			WidgetManager widgetManager = guiAlyzer.getWidgetManager();

			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

			textLayout.drawLine(Translator.translateToLocal("for.gui.beealyzer.produce") + ":", GuiAlyzer.COLUMN_0);
			textLayout.newLine();

			int x = GuiAlyzer.COLUMN_0;
			for (ItemStack stack : tree.getProducts().keySet()) {
				widgetManager.add(new ItemStackWidget(widgetManager, x, textLayout.getLineY(), stack));
				x += 18;
				if (x > 148) {
					x = GuiAlyzer.COLUMN_0;
					textLayout.newLine();
				}
			}

			textLayout.newLine();
			textLayout.newLine();
			textLayout.newLine();
			textLayout.newLine();

			textLayout.drawLine(Translator.translateToLocal("for.gui.beealyzer.specialty") + ":", GuiAlyzer.COLUMN_0);
			textLayout.newLine();

			x = GuiAlyzer.COLUMN_0;
			for (ItemStack stack : tree.getSpecialties().keySet()) {
				Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, guiAlyzer.getGuiLeft() + x, guiAlyzer.getGuiTop() + textLayout.getLineY());
				x += 18;
				if (x > 148) {
					x = GuiAlyzer.COLUMN_0;
					textLayout.newLine();
				}
			}

			textLayout.endPage();
		}
	}

	@Override
	public Map<String, ItemStack> getIconStacks() {
		return iconStacks;
	}

	@Override
	public List<String> getHints() {
		return Config.hints.get("treealyzer");
	}

}
