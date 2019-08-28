package forestry.apiculture.genetics;

import java.util.Optional;
import java.util.function.Function;

import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.organism.IOrganismType;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.DatabaseMode;
import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IDatabaseElement;
import forestry.api.gui.style.ITextStyle;
import forestry.api.gui.style.TextStyleBuilder;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.render.ColourProperties;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

@OnlyIn(Dist.CLIENT)
public class BeeDatabaseTab implements IDatabaseTab<IBee> {
	private static final ITextStyle BINOMIAL = new TextStyleBuilder().color(() -> ColourProperties.INSTANCE.get("gui.beealyzer.binomial")).build();

	private final DatabaseMode mode;

	BeeDatabaseTab(DatabaseMode mode) {
		this.mode = mode;
	}

	@Override
	public DatabaseMode getMode() {
		return mode;
	}

	@Override
	public void createElements(IDatabaseElement container, IBee bee, ItemStack itemStack) {
		Optional<IOrganismType> optionalType = BeeManager.beeRoot.getTypes().getType(itemStack);
		if (!optionalType.isPresent()) {
			return;
		}
		IOrganismType type = optionalType.get();
		IAlleleBeeSpecies primarySpecies = bee.getGenome().getPrimary(IAlleleBeeSpecies.class);
		IAlleleBeeSpecies secondarySpecies = bee.getGenome().getSecondary(IAlleleBeeSpecies.class);

		container.label(Translator.translateToLocal("for.gui.database.tab." + (mode == DatabaseMode.ACTIVE ? "active" : "inactive") + "_species.name"), GuiElementAlignment.TOP_CENTER, GuiElementFactory.DATABASE_TITLE);

		container.addLine(Translator.translateToLocal("for.gui.species"), BeeChromosomes.SPECIES);

		Function<Boolean, String> toleranceText = a -> {
			IAlleleForestrySpecies species = a ? primarySpecies : secondarySpecies;
			return AlleleManager.climateHelper.toDisplay(species.getTemperature()).getFormattedText();	//TODO textcomponents
		};
		container.addLine(Translator.translateToLocal("for.gui.climate"), toleranceText, BeeChromosomes.TEMPERATURE_TOLERANCE);
		container.addToleranceLine(BeeChromosomes.TEMPERATURE_TOLERANCE);

		container.addLine(Translator.translateToLocal("for.gui.humidity"), toleranceText, BeeChromosomes.HUMIDITY_TOLERANCE);
		container.addToleranceLine(BeeChromosomes.HUMIDITY_TOLERANCE);

		container.addLine(Translator.translateToLocal("for.gui.lifespan"), BeeChromosomes.LIFESPAN);

		container.addLine(Translator.translateToLocal("for.gui.speed"), BeeChromosomes.SPEED);
		container.addLine(Translator.translateToLocal("for.gui.pollination"), BeeChromosomes.FLOWERING);
		container.addLine(Translator.translateToLocal("for.gui.flowers"), BeeChromosomes.FLOWER_PROVIDER);

		container.addFertilityLine(Translator.translateToLocal("for.gui.fertility"), BeeChromosomes.FERTILITY, 0);

		container.addLine(Translator.translateToLocal("for.gui.area"), BeeChromosomes.TERRITORY);
		container.addLine(Translator.translateToLocal("for.gui.effect"), BeeChromosomes.EFFECT);

		String yes = Translator.translateToLocal("for.yes");
		String no = Translator.translateToLocal("for.no");

		String diurnal, nocturnal;
		if (mode == DatabaseMode.ACTIVE) {
			if (bee.getGenome().getActiveValue(BeeChromosomes.NEVER_SLEEPS)) {
				nocturnal = diurnal = yes;
			} else {
				nocturnal = primarySpecies.isNocturnal() ? yes : no;
				diurnal = !primarySpecies.isNocturnal() ? yes : no;
			}
		} else {
			if (bee.getGenome().getInactiveValue(ButterflyChromosomes.NOCTURNAL)) {
				nocturnal = diurnal = yes;
			} else {
				nocturnal = secondarySpecies.isNocturnal() ? yes : no;
				diurnal = !secondarySpecies.isNocturnal() ? yes : no;
			}
		}

		container.addLine(Translator.translateToLocal("for.gui.diurnal"), diurnal, false);

		container.addLine(Translator.translateToLocal("for.gui.nocturnal"), nocturnal, false);

		Function<Boolean, String> flyer = active -> StringUtil.readableBoolean(active ? bee.getGenome().getActiveValue(BeeChromosomes.TOLERATES_RAIN) : bee.getGenome().getInactiveValue(BeeChromosomes.TOLERATES_RAIN), yes, no);
		container.addLine(Translator.translateToLocal("for.gui.flyer"), flyer, BeeChromosomes.TOLERATES_RAIN);

		Function<Boolean, String> cave = active -> StringUtil.readableBoolean(active ? bee.getGenome().getActiveValue(BeeChromosomes.CAVE_DWELLING) : bee.getGenome().getInactiveValue(BeeChromosomes.CAVE_DWELLING), yes, no);
		container.addLine(Translator.translateToLocal("for.gui.cave"), cave, BeeChromosomes.CAVE_DWELLING);

		String displayText;
		if (type == EnumBeeType.PRINCESS || type == EnumBeeType.QUEEN) {
			String displayTextKey = "for.bees.stock.pristine";
			if (!bee.isNatural()) {
				displayTextKey = "for.bees.stock.ignoble";
			}
			displayText = Translator.translateToLocal(displayTextKey);
			container.label(displayText, GuiElementAlignment.TOP_CENTER, BINOMIAL);
		}
	}

	@Override
	public ItemStack getIconStack() {
		return BeeDefinition.MEADOWS.getMemberStack(mode == DatabaseMode.ACTIVE ? EnumBeeType.PRINCESS : EnumBeeType.DRONE);
	}
}
