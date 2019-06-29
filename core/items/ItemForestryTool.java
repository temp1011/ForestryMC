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
package forestry.core.items;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import forestry.core.ModuleCore;
import forestry.core.utils.ItemStackUtil;

public class ItemForestryTool extends ItemForestry {
	private final ItemStack remnants;
	private float efficiencyOnProperMaterial;

	public ItemForestryTool(ItemStack remnants) {
		this.maxStackSize = 1;
		efficiencyOnProperMaterial = 6F;
		setMaxDamage(200);
		this.remnants = remnants;
		if (!remnants.isEmpty()) {
			MinecraftForge.EVENT_BUS.register(this);
		}
	}

	public void setEfficiencyOnProperMaterial(float efficiencyOnProperMaterial) {
		this.efficiencyOnProperMaterial = efficiencyOnProperMaterial;
	}

	@Override
	public boolean canHarvestBlock(BlockState block) {
		if (this == ModuleCore.getItems().bronzePickaxe) {
			Material material = block.getMaterial();
			return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
		}
		return super.canHarvestBlock(block);
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, BlockState state) {
		for (String type : getToolClasses(itemstack)) {
			if (state.getBlock().isToolEffective(type, state)) {
				return efficiencyOnProperMaterial;
			}
		}
		if (this == ModuleCore.getItems().bronzePickaxe) {
			Material material = state.getMaterial();
			return material != Material.IRON && material != Material.ANVIL && material != Material.ROCK ? super.getDestroySpeed(itemstack, state) : this.efficiencyOnProperMaterial;
		}
		return super.getDestroySpeed(itemstack, state);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		if (this == ModuleCore.getItems().bronzeShovel) {
			ItemStack heldItem = player.getHeldItem(hand);
			if (!player.canPlayerEdit(pos.offset(facing), facing, heldItem)) {
				return ActionResultType.FAIL;
			} else {
				BlockState BlockState = worldIn.getBlockState(pos);
				Block block = BlockState.getBlock();

				if (facing != Direction.DOWN && worldIn.getBlockState(pos.up()).getMaterial() == Material.AIR && block == Blocks.GRASS) {
					BlockState BlockState1 = Blocks.GRASS_PATH.getDefaultState();
					worldIn.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

					if (!worldIn.isRemote) {
						worldIn.setBlockState(pos, BlockState1, 11);
						heldItem.damageItem(1, player);
					}

					return ActionResultType.SUCCESS;
				} else {
					return ActionResultType.PASS;
				}
			}
		}
		return ActionResultType.PASS;
	}

	@SubscribeEvent
	public void onDestroyCurrentItem(PlayerDestroyItemEvent event) {
		if (event.getOriginal().isEmpty() || event.getOriginal().getItem() != this) {
			return;
		}

		PlayerEntity player = event.getPlayerEntity();
		World world = player.world;

		if (!world.isRemote && !remnants.isEmpty()) {
			ItemStackUtil.dropItemStackAsEntity(remnants.copy(), world, player.posX, player.posY, player.posZ);
		}
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		if (state.getBlockHardness(worldIn, pos) != 0) {
			stack.damageItem(1, entityLiving);
		}
		return true;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}
}
