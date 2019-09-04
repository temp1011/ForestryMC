package forestry.lepidopterology.genetics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.genetics.DatabaseMode;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.config.Config;
import forestry.core.genetics.analyzer.DatabasePlugin;
import forestry.core.genetics.analyzer.MutationsTab;
import forestry.lepidopterology.ModuleLepidopterology;

@OnlyIn(Dist.CLIENT)
public class ButterflyPlugin extends DatabasePlugin<IButterfly> {
	public static final ButterflyPlugin INSTANCE = new ButterflyPlugin();

	protected final Map<String, ItemStack> iconStacks = new HashMap<>();

	private ButterflyPlugin() {
		super(new ButterflyDatabaseTab(DatabaseMode.ACTIVE),
			new ButterflyDatabaseTab(DatabaseMode.INACTIVE),
			new ButterflyProductsTab(),
			new MutationsTab(() -> ButterflyDefinition.Glasswing.getMemberStack(EnumFlutterType.COCOON)));
		NonNullList<ItemStack> butterflyList = NonNullList.create();
		ModuleLepidopterology.getItems().butterflyGE.addCreativeItems(butterflyList, false);
		for (ItemStack butterflyStack : butterflyList) {
			IAlleleButterflySpecies species = ButterflyGenome.getSpecies(butterflyStack);
			iconStacks.put(species.getUID(), butterflyStack);
		}
	}

	@Override
	public Map<String, ItemStack> getIndividualStacks() {
		return iconStacks;
	}

	@Override
	public List<String> getHints() {
		return Config.hints.get("flutterlyzer");
	}
}
