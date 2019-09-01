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
package forestry.arboriculture.blocks;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.genetics.AlleleRegisterEvent;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.arboriculture.items.ItemBlockDecorativeLeaves;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.arboriculture.items.ItemBlockWoodDoor;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.OreDictUtil;

public class BlockRegistryArboriculture extends BlockRegistry {
	//TODO mega table with WoodBlockKind and IWoodType?

	public final Map<EnumForestryWoodType, BlockForestryLog> logs = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestryLog> logsFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestryLog> logsVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestryPlank> planks = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestryPlank> planksFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestryPlank> planksVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestrySlab> slabs = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestrySlab> slabsFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestrySlab> slabsVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestryFence> fences = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestryFence> fencesFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestryFence> fencesVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestryFenceGate> fenceGates = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestryFenceGate> fenceGatesFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestryFenceGate> fenceGatesVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestryStairs> stairs = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumForestryWoodType, BlockForestryStairs> stairsFireproof = new EnumMap<>(EnumForestryWoodType.class);
	public final Map<EnumVanillaWoodType, BlockForestryStairs> stairsVanillaFireproof = new EnumMap<>(EnumVanillaWoodType.class);

	public final Map<EnumForestryWoodType, BlockForestryDoor> doors = new EnumMap<>(EnumForestryWoodType.class);

	public final BlockSapling saplingGE;
	public final Map<TreeDefinition, BlockForestryLeaves> leaves = new EnumMap<>(TreeDefinition.class);
	public final Map<TreeDefinition, BlockDefaultLeaves> leavesDefault = new EnumMap<>(TreeDefinition.class);
	public final Map<TreeDefinition, BlockDefaultLeavesFruit> leavesDefaultFruit = new EnumMap<>(TreeDefinition.class);
	public final Map<TreeDefinition, BlockDecorativeLeaves> leavesDecorative = new EnumMap<>(TreeDefinition.class);
	public final Map<String, BlockFruitPod> podsMap;

	public final BlockArboriculture treeChest;

	public BlockRegistryArboriculture() {
		// Wood blocks

		//TODO tags
		for(EnumForestryWoodType woodType : EnumForestryWoodType.VALUES) {
			//logs
			BlockForestryLog log = new BlockForestryLog(false, woodType);
			registerBlock(log, new ItemBlockWood<>(log), woodType.toString() + "_log");
			logs.put(woodType, log);

			//logs fireproof
			BlockForestryLog fireproofLog = new BlockForestryLog(true, woodType);
			registerBlock(fireproofLog, new ItemBlockWood<>(fireproofLog), woodType.toString() + "_fireproof_log");
			logsFireproof.put(woodType, fireproofLog);

			//planks
			BlockForestryPlank plank = new BlockForestryPlank(false, woodType);
			registerBlock(plank, new ItemBlockWood<>(plank), woodType.toString() + "_planks");
			planks.put(woodType, plank);

			//planks fireproof
			BlockForestryPlank fireproofPlank = new BlockForestryPlank(true, woodType);
			registerBlock(fireproofPlank, new ItemBlockWood<>(fireproofPlank), woodType.toString() + "_fireproof_planks");
			planksFireproof.put(woodType, fireproofPlank);

			//fences
			BlockForestryFence fence = new BlockForestryFence(false, woodType);
			registerBlock(fence, new ItemBlockWood<>(fence), woodType.toString() + "_fence");
			fences.put(woodType, fence);

			//fences fireproof
			BlockForestryFence fireproofFence = new BlockForestryFence(true, woodType);
			registerBlock(fireproofFence, new ItemBlockWood<>(fireproofFence), woodType.toString() + "_fireproof_fence");
			fencesFireproof.put(woodType, fireproofFence);

			//doors
			BlockForestryDoor door = new BlockForestryDoor(woodType);
			registerBlock(door, new ItemBlockWoodDoor(door), woodType.toString() + "_door");
			registerOreDictWildcard(OreDictUtil.DOOR_WOOD, door);	//TODO tag
			doors.put(woodType, door);
		}

		//TODO tags
		for(EnumVanillaWoodType woodType : EnumVanillaWoodType.VALUES) {
			//planks
			BlockForestryPlank fireproofPlank = new BlockForestryPlank(true, woodType);
			registerBlock(fireproofPlank, new ItemBlockWood<>(fireproofPlank), woodType.toString() + "_fireproof_planks");
			planksVanillaFireproof.put(woodType, fireproofPlank);

			//logs
			BlockForestryLog fireproofLog = new BlockForestryLog(true, woodType);
			registerBlock(fireproofLog, new ItemBlockWood<>(fireproofLog), woodType.toString() + "_fireproof_log");
			logsVanillaFireproof.put(woodType, fireproofLog);

			//fences
			BlockForestryFence fireproofFence = new BlockForestryFence(true, woodType);
			registerBlock(fireproofFence, new ItemBlockWood<>(fireproofFence), woodType.toString() + "_fireproof_fence");
			fencesVanillaFireproof.put(woodType, fireproofFence);
		}

		//TODO tags
		for(Map.Entry<EnumForestryWoodType, BlockForestryPlank> entry : planks.entrySet()) {
			EnumForestryWoodType woodType = entry.getKey();
			BlockForestryPlank plank = entry.getValue();

			//slabs
			BlockForestrySlab slab = new BlockForestrySlab(plank);
			registerBlock(slab, new ItemBlockWood<>(slab), woodType.toString() + "_slab");
			slabs.put(woodType, slab);

			//stairs
			BlockForestryStairs stair = new BlockForestryStairs(plank);
			registerBlock(stair, new ItemBlockWood<>(stair), woodType.toString() + "_stairs");
			stairs.put(woodType, stair);
		}

		for(Map.Entry<EnumForestryWoodType, BlockForestryPlank> entry : planksFireproof.entrySet()) {
			EnumForestryWoodType woodType = entry.getKey();
			BlockForestryPlank plank = entry.getValue();

			//slabs
			BlockForestrySlab slab = new BlockForestrySlab(plank);
			registerBlock(slab, new ItemBlockWood<>(slab), woodType.toString() + "_fireproof_slab");
			slabsFireproof.put(woodType, slab);

			//stairs
			BlockForestryStairs stair = new BlockForestryStairs(plank);
			registerBlock(stair, new ItemBlockWood<>(stair), woodType.toString() + "_fireproof_stairs");
			stairsFireproof.put(woodType, stair);
		}

		for(Map.Entry<EnumVanillaWoodType, BlockForestryPlank> entry : planksVanillaFireproof.entrySet()) {
			EnumVanillaWoodType woodType = entry.getKey();
			BlockForestryPlank plank = entry.getValue();

			//slabs
			BlockForestrySlab slab = new BlockForestrySlab(plank);
			registerBlock(slab, new ItemBlockWood<>(slab), woodType.toString() + "_fireproof_slab");
			slabsVanillaFireproof.put(woodType, slab);

			//stairs
			BlockForestryStairs stair = new BlockForestryStairs(plank);
			registerBlock(stair, new ItemBlockWood<>(stair), woodType.toString() + "_fireproof_stairs");
			stairsVanillaFireproof.put(woodType, stair);
		}

		for (EnumForestryWoodType woodType : EnumForestryWoodType.VALUES) {
			BlockForestryFenceGate fenceGate = new BlockForestryFenceGate(false, woodType);
			registerBlock(fenceGate, new ItemBlockWood<>(fenceGate), woodType.toString() + "_fence_gate");
			registerOreDictWildcard(OreDictUtil.FENCE_GATE_WOOD, fenceGate);	//TODO tags
			fenceGates.put(woodType, fenceGate);

			BlockForestryFenceGate fenceGateFireproof = new BlockForestryFenceGate(true, woodType);
			registerBlock(fenceGateFireproof, new ItemBlockWood<>(fenceGateFireproof), woodType.toString() + "_fence_gate_fireproof");
			registerOreDictWildcard(OreDictUtil.FENCE_GATE_WOOD, fenceGateFireproof);	//TODO tags
			fenceGatesFireproof.put(woodType, fenceGateFireproof);
		}

		for (EnumVanillaWoodType woodType : EnumVanillaWoodType.VALUES) {
			BlockForestryFenceGate fenceGateFireproof = new BlockForestryFenceGate(true, woodType);
			registerBlock(fenceGateFireproof, new ItemBlockWood<>(fenceGateFireproof), woodType.toString() + "_fence_gate_fireproof");
			registerOreDictWildcard(OreDictUtil.FENCE_GATE_WOOD, fenceGateFireproof);	//TODO tags
			fenceGatesVanillaFireproof.put(woodType, fenceGateFireproof);
		}

		// Saplings
		TreeDefinition.preInit();
		saplingGE = new BlockSapling();
		registerBlock(saplingGE, new ItemBlockForestry<>(saplingGE), "sapling_ge");
		registerOreDictWildcard(OreDictUtil.TREE_SAPLING, saplingGE);

		for(TreeDefinition definition : TreeDefinition.VALUES) {
			//leaves
			BlockForestryLeaves forestryLeaves = new BlockForestryLeaves(definition);
			registerBlock(forestryLeaves, new ItemBlockLeaves(forestryLeaves), definition.getName() + "_leaves");
			leaves.put(definition, forestryLeaves);

			//decorative
			BlockDecorativeLeaves decorativeLeaves = new BlockDecorativeLeaves(definition);
			//TODO block name might be a bit rubbish
			registerBlock(decorativeLeaves, new ItemBlockDecorativeLeaves(decorativeLeaves), definition.getName() + "_decorative_leaves");
			leavesDecorative.put(definition, decorativeLeaves);

			//default
			BlockDefaultLeaves defaultLeaves = new BlockDefaultLeaves(definition);
			registerBlock(defaultLeaves, new ItemBlockLeaves(defaultLeaves), definition.getName() + "_default_leaves");
			leavesDefault.put(definition, defaultLeaves);

			//default fruit leaves
			BlockDefaultLeavesFruit defaultLeavesFruit = new BlockDefaultLeavesFruit(definition);
			registerBlock(defaultLeavesFruit, new ItemBlockLeaves(defaultLeavesFruit), definition.getName() + "_default_leaves_fruit");
			leavesDefaultFruit.put(definition, defaultLeavesFruit);
		}

		// Pods
		AlleleFruits.registerAlleles();
		MinecraftForge.EVENT_BUS.post(new AlleleRegisterEvent<>(IAlleleFruit.class));
		podsMap = new HashMap<>();
		for (BlockFruitPod pod : BlockFruitPod.create()) {
			IAlleleFruit fruit = pod.getFruit();
			registerBlock(pod, "pods." + fruit.getModelName());
			podsMap.put(fruit.getUID(), pod);
		}

		// Machines
		treeChest = new BlockArboriculture(BlockTypeArboricultureTesr.ARB_CHEST);
		registerBlock(treeChest, new ItemBlockForestry<>(treeChest), "tree_chest");
	}

	//TODO probably slow etc
	public ItemStack getDecorativeLeaves(String speciesUid) {
		Optional<BlockDecorativeLeaves> block = leavesDecorative.entrySet().stream()
				.filter(e -> e.getKey().getUID().equals(speciesUid))
				.findFirst()
				.map(Map.Entry::getValue);

		return block.map(ItemStack::new).orElse(ItemStack.EMPTY);
	}

	//TODO probably slow etc
	@Nullable
	public BlockState getDefaultLeaves(String speciesUid) {
		Optional<BlockDefaultLeaves> block = leavesDefault.entrySet().stream()
				.filter(e -> e.getKey().getUID().equals(speciesUid))
				.findFirst()
				.map(Map.Entry::getValue);

		return block.map(Block::getDefaultState).orElse(null);
	}

	@Nullable
	public BlockState getDefaultLeavesFruit(String speciesUid) {
		Optional<BlockDefaultLeavesFruit> block = leavesDefaultFruit.entrySet().stream()
				.filter(e -> e.getKey().getUID().equals(speciesUid))
				.findFirst()
				.map(Map.Entry::getValue);

		return block.map(Block::getDefaultState).orElse(null);
	}

	@Nullable
	public BlockFruitPod getFruitPod(IAlleleFruit fruit) {
		return podsMap.get(fruit.getUID());
	}
}
