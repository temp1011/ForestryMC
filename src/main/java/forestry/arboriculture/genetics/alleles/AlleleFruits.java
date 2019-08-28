package forestry.arboriculture.genetics.alleles;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.FruitProviderNone;
import forestry.arboriculture.FruitProviderPod;
import forestry.arboriculture.FruitProviderPod.EnumPodType;
import forestry.arboriculture.FruitProviderRipening;
import forestry.core.ModuleCore;
import forestry.core.items.ItemFruit.EnumFruit;
import forestry.core.items.ItemRegistryCore;

import static forestry.api.arboriculture.EnumFruitFamily.JUNGLE;
import static forestry.api.arboriculture.EnumFruitFamily.NONE;
import static forestry.api.arboriculture.EnumFruitFamily.NUX;
import static forestry.api.arboriculture.EnumFruitFamily.POMES;
import static forestry.api.arboriculture.EnumFruitFamily.PRUNES;

public class AlleleFruits {
	public static final IAlleleFruit fruitNone;
	public static final IAlleleFruit fruitApple;
	public static final IAlleleFruit fruitCocoa;
	public static final IAlleleFruit fruitChestnut;
	public static final IAlleleFruit fruitWalnut;
	public static final IAlleleFruit fruitCherry;
	public static final IAlleleFruit fruitDates;
	public static final IAlleleFruit fruitPapaya;
	public static final IAlleleFruit fruitLemon;
	public static final IAlleleFruit fruitPlum;
	private static final List<IAlleleFruit> fruitAlleles;
	@Nullable
	private static List<IAlleleFruit> fruitAllelesWithModels;

	static {
		ItemStack cocoaBean = new ItemStack(Items.COCOA_BEANS);

		ItemRegistryCore coreItems = ModuleCore.getItems();
		fruitAlleles = Arrays.asList(
			fruitNone = new AlleleFruit("none", new FruitProviderNone("for.fruits.none", NONE)),
			fruitApple = new AlleleFruit("apple", new FruitProviderRipening("for.fruits.apple", POMES, new ItemStack(Items.APPLE), 1.0f)
				.setColours(new Color(0xff2e2e), new Color(0xE3F49C))
				.setOverlay("pomes")),
			fruitCocoa = new AlleleFruit("cocoa", new FruitProviderPod("for.fruits.cocoa", JUNGLE, EnumPodType.COCOA, cocoaBean)),
			// .setColours(0xecdca5, 0xc4d24a), true)
			fruitChestnut = new AlleleFruit("chestnut", new FruitProviderRipening("for.fruits.chestnut", NUX, coreItems.getFruit(EnumFruit.CHESTNUT, 1), 1.0f)
				.setRipeningPeriod(6)
				.setColours(new Color(0x7f333d), new Color(0xc4d24a))
				.setOverlay("nuts"), true),
			fruitWalnut = new AlleleFruit("walnut", new FruitProviderRipening("for.fruits.walnut", NUX, coreItems.getFruit(EnumFruit.WALNUT,1 ), 1.0f)
				.setRipeningPeriod(8)
				.setColours(new Color(0xfba248), new Color(0xc4d24a))
				.setOverlay("nuts"), true),
			fruitCherry = new AlleleFruit("cherry", new FruitProviderRipening("for.fruits.cherry", PRUNES, coreItems.getFruit(EnumFruit.CHERRY, 1), 1.0f)
				.setColours(new Color(0xff2e2e), new Color(0xc4d24a))
				.setOverlay("berries"), true),
			fruitDates = new AlleleFruit("dates", new FruitProviderPod("for.fruits.dates", JUNGLE, EnumPodType.DATES, coreItems.getFruit(EnumFruit.DATES, 4))),
			fruitPapaya = new AlleleFruit("papaya", new FruitProviderPod("for.fruits.papaya", JUNGLE, EnumPodType.PAPAYA, coreItems.getFruit(EnumFruit.PAPAYA,1 ))),
			fruitLemon = new AlleleFruit("lemon", new FruitProviderRipening("for.fruits.lemon", PRUNES, coreItems.getFruit(EnumFruit.LEMON, 1), 1.0f)
				.setColours(new Color(0xeeee00), new Color(0x99ff00))
				.setOverlay("citrus"), true),
			fruitPlum = new AlleleFruit("plum", new FruitProviderRipening("for.fruits.plum", PRUNES, coreItems.getFruit(EnumFruit.PLUM, 1), 1.0f)
				.setColours(new Color(0x663446), new Color(0xeeff1a))
				.setOverlay("plums"), true)
		);
	}

	public static void registerAlleles() {
		for (IAlleleFruit fruitAllele : fruitAlleles) {
			AlleleManager.alleleRegistry.registerAllele(fruitAllele, EnumTreeChromosome.FRUITS);
		}
	}

	public static List<IAlleleFruit> getFruitAlleles() {
		return fruitAlleles;
	}

	public static List<IAlleleFruit> getFruitAllelesWithModels() {
		if (fruitAllelesWithModels == null) {
			fruitAllelesWithModels = new ArrayList<>();
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles(EnumTreeChromosome.FRUITS)) {
				if (allele instanceof IAlleleFruit) {
					IAlleleFruit alleleFruit = (IAlleleFruit) allele;
					if (alleleFruit.getModelName() != null) {
						fruitAllelesWithModels.add(alleleFruit);
					}
				}
			}
		}
		return fruitAllelesWithModels;
	}
}
