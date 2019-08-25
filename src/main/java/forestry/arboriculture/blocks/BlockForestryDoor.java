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

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.IModelManager;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.proxy.ProxyArboricultureClient;

//eg    public static final Block OAK_DOOR = register("oak_door", new DoorBlock(Block.Properties.create(Material.WOOD, OAK_PLANKS.materialColor).hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
public class BlockForestryDoor extends DoorBlock implements IWoodTyped {

	private final EnumForestryWoodType woodType;

	public BlockForestryDoor(EnumForestryWoodType woodType) {
		super(Block.Properties.create(Material.WOOD)
		.hardnessAndResistance(woodType.getHardness(), woodType.getHardness() * 1.5F)
		.sound(SoundType.WOOD));
		this.woodType = woodType;

		//		setHarvestLevel("axe", 0);	TODO harvest level
		//		setCreativeTab(Tabs.tabArboriculture);	TODO creative tab
	}

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.DOOR;
	}

	@Override
	public boolean isFireproof() {
		return false;
	}

	@Override
	public IWoodType getWoodType() {
		return woodType;
	}
}
