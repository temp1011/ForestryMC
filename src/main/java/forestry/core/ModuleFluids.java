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
package forestry.core;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluid;
import forestry.core.fluids.ForestryFluids;
import forestry.core.items.ItemRegistryCore;
import forestry.core.items.ItemRegistryFluids;
import forestry.core.proxy.Proxies;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.FLUIDS, name = "Fluids", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.module.fluids.description")
public class ModuleFluids extends BlankForestryModule {
	@Nullable
	private static ItemRegistryFluids items;

	private static void createFluids(ForestryFluids definition) {
		/*if (definition.getFluid() == null && Config.isFluidEnabled(definition)) {
			String fluidName = definition.getTag();
			if (false){//!FluidRegistry.isFluidRegistered(fluidName)) { TODO fluids
				ResourceLocation[] resources = definition.getResources();
				Fluid fluid = new Fluid(fluidName, resources[0], definition.flowTextureExists() ? resources[1] : resources[0]);
				fluid.setDensity(definition.getDensity());
				fluid.setViscosity(definition.getViscosity());
				fluid.setTemperature(definition.getTemperature());
//				FluidRegistry.registerFluid(fluid);
				createBlocks(definition);
			}
		}*/

		if (Config.isFluidEnabled(definition)) {
			IForgeRegistry<Fluid> registry = ForgeRegistries.FLUIDS;
			registry.registerAll(definition.getFluid(), definition.getFlowing());
		}
	}

	private static void createBlocks(ForestryFluids definition) {
		if (!Config.isFluidEnabled(definition)) {
			return;
		}
		Fluid sourceFluid = new ForestryFluid.Source(definition);
		Fluid flowingFluid = new ForestryFluid.Flowing(definition);
		definition.setSourceFluid(sourceFluid);
		definition.setFlowingFluid(flowingFluid);
		sourceFluid.setRegistryName(definition.getTag());
		flowingFluid.setRegistryName(Constants.MOD_ID, definition.getTag().getPath() + "_flowing");
		if (!Config.isBlockEnabled(definition.getTag())) {
			return;
		}
		Block sourceBlock = createBlock(definition, false);
		Block flowingBlock = createBlock(definition, true);
		definition.setSourceBlock(sourceBlock);
		definition.setFlowingBlock(flowingBlock);
		/*if (fluidBlock == null) {
			fluidBlock = definition.makeBlock(fluid);
			if (fluidBlock != null) {
				String name = "fluid." + definition.getTag();
//					fluidBlock.setTranslationKey("forestry." + name); TODO done by registry name?
				fluidBlock.setRegistryName(name);
				ForgeRegistries.BLOCKS.register(fluidBlock);

				BlockItem itemBlock = new BlockItem(fluidBlock, new Item.Properties());
				itemBlock.setRegistryName(name);
				ForgeRegistries.ITEMS.register(itemBlock);

				Proxies.render.registerFluidStateMapper(fluidBlock, definition);
				if (definition.getOtherContainers().isEmpty()) {
//						FluidRegistry.addBucketForFluid(fluid);
				}
			}
		} else {
			ResourceLocation resourceLocation = ForgeRegistries.BLOCKS.getKey(fluidBlock);
			Log.warning("Pre-existing {} fluid block detected, deferring to {}:{}, "
				+ "this may cause issues if the server/client have different mod load orders, "
				+ "recommended that you disable all but one instance of {} fluid blocks via your configs.", fluid.getRegistryName(), resourceLocation.getNamespace(), resourceLocation.getPath(), fluid.getRegistryName());
		}*/
	}

	private static Block createBlock(ForestryFluids definition, boolean flowing) {
		Block fluidBlock = definition.makeBlock(flowing);
		String name = "fluid." + definition.getTag().getPath() + (flowing ? "_flowing" : "");
		//fluidBlock.setTranslationKey("forestry." + name); TODO done by registry name?
		fluidBlock.setRegistryName(name);
		ForgeRegistries.BLOCKS.register(fluidBlock);

		BlockItem itemBlock = new BlockItem(fluidBlock, new Item.Properties());
		itemBlock.setRegistryName(name);
		ForgeRegistries.ITEMS.register(itemBlock);

		Proxies.render.registerFluidStateMapper(fluidBlock, definition);
		if (definition.getOtherContainers().isEmpty()) {
			//FluidRegistry.addBucketForFluid(fluid);
		}
		return fluidBlock;
	}

	public static ItemRegistryFluids getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	@Override
	public boolean canBeDisabled() {
		return false;
	}

	@Override
	public void registerBlocks() {
		for (ForestryFluids fluidType : ForestryFluids.values()) {
			createBlocks(fluidType);
		}
	}

	@Override
	public void registerItems() {
		items = new ItemRegistryFluids();
	}

	public static void registerFluids() {
		for (ForestryFluids fluidType : ForestryFluids.values()) {
			createFluids(fluidType);
		}
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void doInit() {
		if (RecipeManagers.squeezerManager != null) {
			ItemRegistryCore itemRegistryCore = ModuleCore.getItems();
			RecipeManagers.squeezerManager.addContainerRecipe(10, getItems().canEmpty.getItemStack(), itemRegistryCore.ingotTin.copy(), 0.05f);
			RecipeManagers.squeezerManager.addContainerRecipe(10, getItems().waxCapsuleEmpty.getItemStack(), itemRegistryCore.beeswax.getItemStack(), 0.10f);
			RecipeManagers.squeezerManager.addContainerRecipe(10, getItems().refractoryEmpty.getItemStack(), itemRegistryCore.refractoryWax.getItemStack(), 0.10f);
		}

		FluidStack ethanol = ForestryFluids.BIO_ETHANOL.getFluid(1);
		if (!ethanol.isEmpty()) {
			GeneratorFuel ethanolFuel = new GeneratorFuel(ethanol, (int) (32 * ForestryAPI.activeMode.getFloatSetting("fuel.ethanol.generator")), 4);
			FuelManager.generatorFuel.put(ethanol.getFluid(), ethanolFuel);
		}

		FluidStack biomass = ForestryFluids.BIOMASS.getFluid(1);
		if (!biomass.isEmpty()) {
			GeneratorFuel biomassFuel = new GeneratorFuel(biomass, (int) (8 * ForestryAPI.activeMode.getFloatSetting("fuel.biomass.generator")), 1);
			FuelManager.generatorFuel.put(biomass.getFluid(), biomassFuel);
		}
	}

	//TODO - register event handler
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void registerTextures(TextureStitchEvent.Pre event) {
		AtlasTexture map = event.getMap();
		for (ForestryFluids fluids : ForestryFluids.values()) {
			Fluid fluid = fluids.getFluid();
			if (fluid == Fluids.EMPTY) {
				//TODO fluid textures
//				map.registerSprite(fluid.getStill());
//				map.registerSprite(fluid.getFlowing());
			}
		}
	}
}
