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
package forestry.core.fluids;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.core.gui.tooltips.ToolTip;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FilteredTank extends StandardTank {

	private final Set<String> filters = new HashSet<>(); // FluidNames

	public FilteredTank(int capacity) {
		super(capacity);
	}

	public FilteredTank(int capacity, boolean canFill, boolean canDrain) {
		super(capacity, canFill, canDrain);
	}

	public FilteredTank setFilters(Fluid... filters) {
		return setFilters(Arrays.asList(filters));
	}

	public FilteredTank setFilters(Collection<Fluid> filters) {
		this.filters.clear();
		for (Fluid fluid : filters) {
			this.filters.add(fluid.getName());
		}
		return this;
	}

	@Override
	public boolean canFillFluidType(FluidStack fluid) {
		return fluidMatchesFilter(fluid);
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluid) {
		return fluidMatchesFilter(fluid);
	}

	private boolean fluidMatchesFilter(FluidStack resource) {
		return resource != null && resource.getFluid() != null &&
			filters.contains(resource.getFluid().getName());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void refreshTooltip() {
		if (hasFluid()) {
			super.refreshTooltip();
			return;
		}

		ToolTip toolTip = getToolTip();
		toolTip.clear();
		if (Screen.hasShiftDown() || filters.size() < 5) {
			for (String filterName : filters) {
				//TODO fluids
//				Fluid fluidFilter = FluidRegistry.getFluid(filterName);
//				Rarity rarity = fluidFilter.getRarity();
//				if (rarity == null) {
//					rarity = Rarity.COMMON;
//				}
//				FluidStack filterFluidStack = FluidRegistry.getFluidStack(fluidFilter.getName(), 0);
//				toolTip.add(fluidFilter.getLocalizedName(filterFluidStack), rarity.color);
			}
		} else {
			//TODO can this be simplified
			ITextComponent tmiComponent = new StringTextComponent("<")
					.appendSibling(new TranslationTextComponent("for.gui.tooltip.tmi"))
					.appendSibling(new StringTextComponent(">"));
			toolTip.add(tmiComponent, TextFormatting.ITALIC);
		}
		toolTip.add(new TranslationTextComponent("for.gui.tooltip.liquid.amount", getFluidAmount(), getCapacity()));
	}

}
