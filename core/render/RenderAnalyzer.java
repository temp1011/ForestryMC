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
package forestry.core.render;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import com.mojang.blaze3d.platform.GlStateManager;

import forestry.apiculture.render.ModelAnalyzer;
import forestry.core.blocks.BlockBase;
import forestry.core.tiles.TileAnalyzer;

public class RenderAnalyzer extends TileEntityRenderer<TileAnalyzer> {

	private final ModelAnalyzer model;
	@Nullable
	private ItemEntity dummyEntityItem;
	private long lastTick;

	public RenderAnalyzer(String baseTexture) {
		this.model = new ModelAnalyzer(baseTexture);
	}

	private ItemEntity dummyItem(World world) {
		if (dummyEntityItem == null) {
			dummyEntityItem = new ItemEntity(world);
		} else {
			dummyEntityItem.world = world;
		}
		return dummyEntityItem;
	}

	/**
	 * @param analyzer If it null its render the item else it render the tile entity.
	 */
	@Override
	public void render(TileAnalyzer analyzer, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (analyzer != null) {
			World worldObj = analyzer.getWorldObj();
			if (worldObj.isBlockLoaded(analyzer.getPos())) {
				BlockState blockState = worldObj.getBlockState(analyzer.getPos());
				if (blockState.getBlock() instanceof BlockBase) {
					Direction facing = blockState.get(BlockBase.FACING);
					render(analyzer.getIndividualOnDisplay(), worldObj, facing, x, y, z);
					return;
				}
			}
		}
		render(ItemStack.EMPTY, null, Direction.WEST, x, y, z);
	}

	private void render(ItemStack itemstack, @Nullable World world, Direction orientation, double x, double y, double z) {

		model.render(orientation, (float) x, (float) y, (float) z);
		if (itemstack.isEmpty() || world == null) {
			return;
		}
		ItemEntity dummyItem = dummyItem(world);
		float renderScale = 1.0f;

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.translate(0.5f, 0.2f, 0.5f);
		GlStateManager.scale(renderScale, renderScale, renderScale);
		dummyItem.setItem(itemstack);

		if (world.getGameTime() != lastTick) {
			lastTick = world.getGameTime();
			dummyItem.onUpdate();
		}
		EntityRendererManager rendermanager = Minecraft.getInstance().getRenderManager();

		rendermanager.renderEntity(dummyItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
		GlStateManager.popMatrix();

	}

}
