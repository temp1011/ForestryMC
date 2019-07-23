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
package forestry;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;

import forestry.api.climate.ClimateManager;
import forestry.api.core.ForestryAPI;
import forestry.core.EventHandlerCore;
import forestry.core.TickHandlerCoreServer;
import forestry.core.advancements.AdvancementManager;
import forestry.core.climate.ClimateFactory;
import forestry.core.climate.ClimateRoot;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.GameMode;
import forestry.core.errors.EnumErrorCode;
import forestry.core.errors.ErrorStateRegistry;
import forestry.core.gui.GuiHandler;
import forestry.core.multiblock.MultiblockEventHandler;
import forestry.core.network.NetworkHandler;
import forestry.core.network.PacketHandlerServer;
import forestry.core.proxy.Proxies;
import forestry.core.worldgen.WorldGenerator;
import forestry.modules.ForestryModules;
import forestry.modules.ModuleManager;
//import forestry.plugins.ForestryCompatPlugins;
//import forestry.plugins.PluginBuildCraftFuels;
//import forestry.plugins.PluginIC2;
//import forestry.plugins.PluginNatura;
//import forestry.plugins.PluginTechReborn;

/**
 * Forestry Minecraft Mod
 *
 * @author SirSengir
 */
//@Mod(
//	modid = Constants.MOD_ID,
//	name = Constants.MOD_NAME,
//	version = Constants.VERSION,
//	guiFactory = "forestry.core.config.ForestryGuiConfigFactory",
//	acceptedMinecraftVersions = "[1.12.2,1.13.0)",
//	dependencies = "required-after:forge@[14.23.4.2749,);"
//		+ "after:jei@[4.12.0.0,);"
//		+ "after:" + PluginIC2.MOD_ID + ";"
//		+ "after:" + PluginNatura.MOD_ID + ";"
//		+ "after:toughasnails;"
//		+ "after:" + PluginTechReborn.MOD_ID + ";"
//		+ "after:" + PluginBuildCraftFuels.MOD_ID + ";"
//		+ "before:binniecore@[2.5.1.184,)"


//the big TODO - things have to be properly sided now, can't keep just using OnlyIn I think
@Mod("forestry")
public class Forestry {

	@SuppressWarnings("NullableProblems")
	public static Forestry instance;

	private static final Logger LOGGER = LogManager.getLogger();

	@Nullable
	private File configFolder;

	public Forestry() {
		ForestryAPI.instance = this;
		ForestryAPI.forestryConstants = new Constants();
		ForestryAPI.errorStateRegistry = new ErrorStateRegistry();
		ClimateManager.climateRoot = ClimateRoot.getInstance();
		ClimateManager.climateFactory = ClimateFactory.INSTANCE;
		ClimateManager.stateHelper = ClimateStateHelper.INSTANCE;
		EnumErrorCode.init();
		FluidRegistry.enableUniversalBucket();
		ModuleManager moduleManager = ModuleManager.getInstance();
		ForestryAPI.moduleManager = moduleManager;
		ModuleManager.runSetup();
		//		moduleManager.registerContainers(new ForestryModules(), new ForestryCompatPlugins());
		NetworkHandler networkHandler = new NetworkHandler();
//		DistExecutor.runForDist(()->()-> networkHandler.clientPacketHandler(), ()->()-> networkHandler.serverPacketHandler());
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the enqueueIMC method for modloading
//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// Register the processIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMCMessages);
		// Register the doClientStuff method for modloading
//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		FMLJavaModLoadingContext.get().getModEventBus().register(TickHandlerCoreServer.class);	//TODO - correct?
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nullable
	private static PacketHandlerServer packetHandler;

	public static PacketHandlerServer getPacketHandler() {
		Preconditions.checkNotNull(packetHandler);
		return packetHandler;
	}

	public void setup(FMLCommonSetupEvent event) {
		instance = this;
		packetHandler = new PacketHandlerServer();

		// Register event handler
		EventHandlerCore eventHandlerCore = new EventHandlerCore();
		MinecraftForge.EVENT_BUS.register(eventHandlerCore);
		MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());
		MinecraftForge.EVENT_BUS.register(Config.class);
//		Proxies.common.registerEventHandlers(); TODO DistExecutor
		configFolder = new File("."); //new File(event.getModConfigurationDirectory(), Constants.MOD_ID);
		//TODO - DistExecutor
		Config.load(Dist.DEDICATED_SERVER);

		ModuleManager.getInternalHandler().runSetup();

		String gameMode = Config.gameMode;
		Preconditions.checkNotNull(gameMode);
		ForestryAPI.activeMode = new GameMode(gameMode);


		//TODO - DistExecutor
		ModuleManager.getInternalHandler().runPreInit(Dist.DEDICATED_SERVER);
	}

	// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
	// Event bus for receiving Registry Events)
	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		//TODO use registry events (although how this works with bee combs etc is tricky)
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
			// register a new block here
			LOGGER.info("HELLO from Register Block");
		}

		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void registerItems(RegistryEvent.Register<Item> event) {
			ModuleManager.getInternalHandler().runRegisterBackpacksAndCrates();
		}
	}


	//client
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
//		Proxies.render.registerModels(); TODO DistExecutor
	}

	//split
	//TODO - when to run these events
//	@EventHandler
//	public void init(FMLInitializationEvent event) {
//		// Register gui handler
//		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
//
//		ModuleManager.getInternalHandler().runInit();
//
//		Proxies.render.registerItemAndBlockColors();
//		AdvancementManager.registerTriggers();
//	}
//
////	@EventHandler
//	public void postInit(FMLPostInitializationEvent event) {
//		ModuleManager.getInternalHandler().runPostInit();
//
//		// Register world generator
//		WorldGenerator worldGenerator = new WorldGenerator();
//		GameRegistry.registerWorldGenerator(worldGenerator, 0);
//
//		// Register tick handlers
//		Proxies.common.registerTickHandlers(worldGenerator);
//	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		ModuleManager.serverStarting(event.getServer());
	}

	@Nullable
	public File getConfigFolder() {
		return configFolder;
	}

	public void processIMCMessages(InterModProcessEvent event) {
		ModuleManager.getInternalHandler().processIMCMessages(event.getIMCStream());
	}
}
