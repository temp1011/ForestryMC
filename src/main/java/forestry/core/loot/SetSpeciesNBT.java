package forestry.core.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.functions.ILootFunction;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;

//TODO - loot tables now different
public class SetSpeciesNBT implements ILootFunction {
	private final String speciesUid;

	public SetSpeciesNBT(String speciesUid) {
		this.speciesUid = speciesUid;
	}

	@Override
	public ItemStack apply(ItemStack stack, LootContext context) {
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(stack);
		if (speciesRoot != null) {
			ISpeciesType speciesType = speciesRoot.getType(stack);
			if (speciesType != null) {
				IAllele[] template = speciesRoot.getTemplate(speciesUid);
				if (template != null) {
					IIndividual individual = speciesRoot.templateAsIndividual(template);
					return speciesRoot.getMemberStack(individual, speciesType);
				}
			}
		}
		return stack;
	}

	public static class Serializer extends ILootFunction.Serializer<SetSpeciesNBT> {
		public Serializer() {
			super(new ResourceLocation("set_species_nbt"), SetSpeciesNBT.class);
		}

		@Override
		public void serialize(JsonObject object, SetSpeciesNBT functionClazz, JsonSerializationContext serializationContext) {
			object.addProperty("speciesUid", functionClazz.speciesUid);
		}

		@Override
		public SetSpeciesNBT deserialize(JsonObject object, JsonDeserializationContext deserializationContext) {
			String speciesUid = JSONUtils.getString(object, "speciesUid");
			return new SetSpeciesNBT(speciesUid);
		}
	}
}