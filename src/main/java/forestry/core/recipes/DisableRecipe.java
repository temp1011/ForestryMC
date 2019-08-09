/*
 *******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 *******************************************************************************
 */
package forestry.core.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.function.BooleanSupplier;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.crafting.IConditionSerializer;

import forestry.api.core.ForestryAPI;

public class DisableRecipe implements IConditionSerializer
{

	@Override
	public BooleanSupplier parse(JsonObject json) {
		String module = json.get("module").getAsString();
		JsonElement conElement = json.get("container");
		String container = conElement == null ? "forestry" : conElement.getAsString();

		return () -> ForestryAPI.enabledModules.contains(new ResourceLocation(container, module));
	}
}
