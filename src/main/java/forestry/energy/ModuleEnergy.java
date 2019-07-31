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
package forestry.energy;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraftforge.fml.DistExecutor;

import forestry.api.core.ForestryAPI;
import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.tiles.TileRegistryCore;
import forestry.energy.blocks.BlockRegistryEnergy;
import forestry.energy.proxy.ProxyEnergy;
import forestry.energy.proxy.ProxyEnergyClient;
import forestry.energy.tiles.TileRegistryEnergy;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.ENERGY, name = "Energy", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.energy.description")
public class ModuleEnergy extends BlankForestryModule {

	@SuppressWarnings("NullableProblems")
	public static ProxyEnergy proxy;

	@Nullable
	public static BlockRegistryEnergy blocks;
	@Nullable
	public static TileRegistryEnergy tiles;

	public ModuleEnergy() {
		//set up proxies as early as possible
		proxy = DistExecutor.runForDist(() -> () -> new ProxyEnergyClient(), () -> () -> new ProxyEnergy());
	}

	public static BlockRegistryEnergy getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	public static TileRegistryEnergy getTiles() {
		Preconditions.checkNotNull(tiles);
		return tiles;
	}

	@Override
	public void registerBlocks() {
		blocks = new BlockRegistryEnergy();
	}

	@Override
	public void registerTiles() {
		tiles = new TileRegistryEnergy();
	}

	@Override
	public void doInit() {
		BlockRegistryEnergy blocks = getBlocks();
		blocks.peatEngine.init();
		blocks.biogasEngine.init();

		if (ForestryAPI.activeMode.getBooleanSetting("energy.engine.clockwork")) {
			blocks.clockworkEngine.init();
		}
	}
}
