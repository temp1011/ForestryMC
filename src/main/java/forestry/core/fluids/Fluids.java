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

import javax.annotation.Nullable;
import java.awt.Color;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;

import net.minecraftforge.fml.loading.FMLEnvironment;

import forestry.core.items.DrinkProperties;
import forestry.core.render.ForestryResource;

public enum Fluids {
	BIO_ETHANOL(new Color(255, 111, 0), 790, 1000, 300),
	BIOMASS(new Color(100, 132, 41), 400, 6560, 100),
	GLASS(new Color(164, 164, 164), 2400, 10000, 0),
	FOR_HONEY(new Color(255, 196, 35), 1420, 75600) {
		@Override
		public DrinkProperties getDrinkProperties() {
			return new DrinkProperties(2, 0.2f, 64);
		}
	},
	ICE(new Color(175, 242, 255), 520, 1000) {
		@Override
		public int getTemperature() {
			return 265;
		}
	},
	JUICE(new Color(168, 201, 114)) {
		@Override
		public DrinkProperties getDrinkProperties() {
			return new DrinkProperties(2, 0.2f, 32);
		}
	},
	MILK(new Color(255, 255, 255), 1030, 3000) {
		@Override
		public List<ItemStack> getOtherContainers() {
			return Collections.singletonList(
					new ItemStack(Items.MILK_BUCKET)
			);
		}
	},
	SEED_OIL(new Color(255, 255, 168), 885, 5000, 2),
	SHORT_MEAD(new Color(239, 154, 56), 1000, 1200, 4) {
		@Override
		public DrinkProperties getDrinkProperties() {
			return new DrinkProperties(1, 0.2f, 32);
		}

	};

	private static final Map<String, Fluids> tagToFluid = new HashMap<>();
	private static final Map<String, Fluids> tagToFluidFlowing = new HashMap<>();

	static {
		for (Fluids fluidDefinition : Fluids.values()) {
			tagToFluid.put(fluidDefinition.getTag(), fluidDefinition);
		}
		for (Fluids fluidDefinition : Fluids.values()) {
			tagToFluidFlowing.put(fluidDefinition.getTag() + "_flowing", fluidDefinition);
		}
	}

	private final String tag;
	private final int density, viscosity, flammability;
	Block sourceBlock = Blocks.AIR;
	Block flowingBlock = Blocks.AIR;
	Fluid sourceFluid = net.minecraft.fluid.Fluids.EMPTY;
	Fluid flowingFluid = net.minecraft.fluid.Fluids.EMPTY;


	private final Color particleColor;

	private final ResourceLocation[] resources = new ResourceLocation[2];

	Fluids(Color particleColor) {
		this(particleColor, 1000, 1000);
	}

	Fluids(Color particleColor, int density, int viscosity) {
		this(particleColor, density, viscosity, -1);
	}

	Fluids(Color particleColor, int density, int viscosity, int flammability) {
		this.tag = name().toLowerCase(Locale.ENGLISH).replace('_', '.');
		this.particleColor = particleColor;
		this.density = density;
		this.viscosity = viscosity;
		this.flammability = flammability;

		resources[0] = new ForestryResource("blocks/liquid/" + getTag() + "_still");
		if (flowTextureExists()) {
			resources[1] = new ForestryResource("blocks/liquid/" + getTag() + "_flow");
		}
	}

	public void setFlowingBlock(Block flowingBlock) {
		this.flowingBlock = flowingBlock;
	}

	public void setSourceBlock(Block sourceBlock) {
		this.sourceBlock = sourceBlock;
	}

	public void setSourceFluid(Fluid sourceFluid) {
		this.sourceFluid = sourceFluid;
	}

	public void setFlowingFluid(Fluid flowingFluid) {
		this.flowingFluid = flowingFluid;
	}

	public int getTemperature() {
		return 295;
	}

	public final String getTag() {
		return tag;
	}

	public final int getDensity() {
		return density;
	}

	public final int getViscosity() {
		return viscosity;
	}

	//@Nullable
	public final Fluid getFluid() {
		//return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(Constants.MOD_ID, getTag()));
		return sourceFluid;
	}

	//@Nullable
	public final Fluid getFlowing() {
		//return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(Constants.MOD_ID, getTag() + "_flowing"));
		return flowingFluid;
	}

	public final FluidStack getFluid(int mb) {
		Fluid fluid = getFluid();
		if (fluid == net.minecraft.fluid.Fluids.EMPTY) {
			return FluidStack.EMPTY;
		}
		return new FluidStack(fluid, mb);
	}

	public final Color getParticleColor() {
		return particleColor;
	}

	public final boolean is(Fluid fluid) {
		return getFluid() == fluid;
	}

	public final boolean is(FluidStack fluidStack) {
		return getFluid() == fluidStack.getFluid();
	}

	public static boolean areEqual(@Nullable Fluid fluid, FluidStack fluidStack) {
		return fluid == fluidStack.getFluid();
	}

	@Nullable
	public static Fluids getFluidDefinition(@Nullable Fluid fluid) {
		if (fluid instanceof ForestryFluid) {
			if (((ForestryFluid) fluid).flowing) {
				Fluids fluidDefinition = tagToFluidFlowing.get(fluid.getRegistryName().getPath());
				return fluidDefinition;
			} else {
				Fluids fluidDefinition = tagToFluid.get(fluid.getRegistryName().getPath());
				return fluidDefinition;
			}
		}

		return null;
	}

	@Nullable
	public static Fluids getFluidDefinition(@Nullable FluidStack fluidStack) {
		if (fluidStack != null) {
			return getFluidDefinition(fluidStack.getFluid());
		}

		return null;
	}

	/**
	 * Add non-forestry containers for this fluid.
	 */
	public List<ItemStack> getOtherContainers() {
		return Collections.emptyList();
	}

	/**
	 * Create a FluidBlock for this fluid.
	 * @param flowing
	 */
	@Nullable
	public Block makeBlock(boolean flowing) {
		return new BlockForestryFluid(this, Math.max(flammability, 0), flammability > 0);
	}

	/**
	 * Get the properties for an ItemFluidContainerForestry before it gets registered.
	 */
	@Nullable
	public DrinkProperties getDrinkProperties() {
		return null;
	}

	public boolean flowTextureExists() {
		if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
			return true;
		}
		try {
			ResourceLocation resourceLocation = new ForestryResource("blocks/liquid/" + getTag() + "_flow");
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft != null) {    //TODO - is it correct
				IResourceManager resourceManager = minecraft.getResourceManager();
				return resourceManager.getResource(resourceLocation) != null;
			}
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public ResourceLocation[] getResources() {
		return resources;
	}
}
