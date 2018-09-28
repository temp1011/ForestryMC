/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ILocatable;

public interface IClimateListener extends ILocatable, IClimateProvider {

	IClimateState getClimateState();

	float getExactTemperature();

	float getExactHumidity();

	/* CLIENT */
	@SideOnly(Side.CLIENT)
	void updateClientSide();

	@SideOnly(Side.CLIENT)
	void setClimateState(IClimateState climateState);

	void syncToClient();

	void syncToClient(EntityPlayerMP player);
}