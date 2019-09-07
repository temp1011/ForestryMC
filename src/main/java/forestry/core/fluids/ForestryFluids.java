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
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraftforge.fml.loading.FMLEnvironment;

import forestry.core.items.DrinkProperties;
import forestry.core.render.ForestryResource;

//TODO - fluids
public enum ForestryFluids {

	BIO_ETHANOL(new Color(255, 111, 0), 790, 1000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 300, true);
		}
	},
	BIOMASS(new Color(100, 132, 41), 400, 6560) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 100, true);
		}
	},
	GLASS(new Color(164, 164, 164), 2400, 10000) {
		@Override
		public int getTemperature() {
			return 1200;
		}

		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 0, true);
		}
	},
	FOR_HONEY(new Color(255, 196, 35), 1420, 75600) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

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

		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}
	},
	JUICE(new Color(168, 201, 114)) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

		@Override
		public DrinkProperties getDrinkProperties() {
			return new DrinkProperties(2, 0.2f, 32);
		}
	},
	MILK(new Color(255, 255, 255), 1030, 3000) {
		@Override
		public Block makeBlock() {
			return  new BlockForestryFluid(this);
		}


		@Override
		public List<ItemStack> getOtherContainers() {
			return Collections.singletonList(
					new ItemStack(Items.MILK_BUCKET)
			);
		}
	},
	SEED_OIL(new Color(255, 255, 168), 885, 5000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 2, true);
		}
	},
	SHORT_MEAD(new Color(239, 154, 56), 1000, 1200) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 4, true);
		}

		@Override
		public DrinkProperties getDrinkProperties() {
			return new DrinkProperties(1, 0.2f, 32);
		}

	};

	private static final Map<ResourceLocation, ForestryFluids> tagToFluid = new HashMap<>();

	static {
		for (ForestryFluids fluidDefinition : ForestryFluids.values()) {
			tagToFluid.put(fluidDefinition.getFluid().getRegistryName(), fluidDefinition);
		}
	}

	private final ResourceLocation tag;
	private final int density, viscosity;

	private final Color particleColor;

	private final ResourceLocation[] resources = new ResourceLocation[2];

	ForestryFluids(Color particleColor) {
		this(particleColor, 1000, 1000);
	}

	ForestryFluids(Color particleColor, int density, int viscosity) {
		this.tag = name().toLowerCase(Locale.ENGLISH).replace('_', '.');
		this.particleColor = particleColor;
		this.density = density;
		this.viscosity = viscosity;

		resources[0] = new ForestryResource("blocks/liquid/" + getTag() + "_still");
		if (flowTextureExists()) {
			resources[1] = new ForestryResource("blocks/liquid/" + getTag() + "_flow");
		}
	}

	public int getTemperature() {
		return 295;
	}

	public final ResourceLocation getTag() {
		return tag;
	}

	public final int getDensity() {
		return density;
	}

	public final int getViscosity() {
		return viscosity;
	}

	@Nullable
	public final Fluid getFluid() {
		return ForgeRegistries.FLUIDS.getValue(getTag());
	}

	public final FluidStack getFluid(int mb) {
		Fluid fluid = getFluid();
		return fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, mb);
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
	public static ForestryFluids getFluidDefinition(@Nullable FluidStack fluidStack) {
		if (fluidStack != null) {
			Fluid fluid = fluidStack.getFluid();
			if (fluid != null) {
				ForestryFluids fluidDefinition = tagToFluid.get(fluid.getRegistryName());
				if (fluidDefinition != null) {
					return fluidDefinition;
				}
			}
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
	 */
	@Nullable
	public Block makeBlock() {
		return null;
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
