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
package forestry.arboriculture.items;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.arboriculture.IToolGrafter;
import forestry.api.core.Tabs;
import forestry.core.items.ItemForestryTool;
import forestry.core.utils.Translator;

public class ItemGrafter extends ItemForestryTool implements IToolGrafter {
	public ItemGrafter(int maxDamage) {
		super(ItemStack.EMPTY);
		setMaxDamage(maxDamage);
		setMaxStackSize(1);
		setCreativeTab(Tabs.tabArboriculture);
		setHarvestLevel("grafter", 3);
		setEfficiencyOnProperMaterial(4.0f);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		if (!stack.isDamaged()) {
			tooltip.add(new TranslationTextComponent("item.for.uses", stack.getMaxDamage() + 1));
		}
	}

	@Override
	public boolean canHarvestBlock(BlockState state, ItemStack stack) {
		Block block = state.getBlock();
		return block instanceof LeavesBlock ||
			state.getMaterial() == Material.LEAVES ||
			super.canHarvestBlock(state, stack);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		return true;
	}

	@Override
	public float getSaplingModifier(ItemStack stack, World world, PlayerEntity player, BlockPos pos) {
		return 100f;
	}
}
