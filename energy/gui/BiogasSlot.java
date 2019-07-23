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
package forestry.energy.gui;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.ReservoirWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.Translator;

public class BiogasSlot extends ReservoirWidget {
	public BiogasSlot(WidgetManager manager, int xPos, int yPos, int slot) {
		super(manager, xPos, yPos, slot);
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		ToolTip toolTip = new ToolTip();
		IFluidTank tank = getTank();
		if (tank != null) {
			FluidStack fluid = tank.getFluid();
			if (fluid == null) {
				toolTip.add(Translator.translateToLocal("for.gui.empty"));
			} else {
				toolTip.add(fluid.getLocalizedName());
			}
		}
		return toolTip;
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
		// do not allow pipette
	}
}
