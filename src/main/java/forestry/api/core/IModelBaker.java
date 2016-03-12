/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A model baker to make custom models
 */
@SideOnly(Side.CLIENT)
public interface IModelBaker {

	void setRenderBoundsFromBlock(Block block);

	void setRenderBounds(double d, double e, double f, double g, double h, double i );

	void setColorIndex(int color);

	void addBlockModel(Block block, BlockPos pos, TextureAtlasSprite[] sprites, int colorIndex);
	
	void addBlockModel(Block block, BlockPos pos, TextureAtlasSprite sprites, int colorIndex);
	
	void addFaceXNeg(TextureAtlasSprite sprite);
	
	void addFaceYNeg(TextureAtlasSprite sprite);

	void addFaceZNeg(TextureAtlasSprite sprite);

	void addFaceYPos(TextureAtlasSprite sprite);

	void addFaceZPos(TextureAtlasSprite sprite);

	void addFaceXPos(TextureAtlasSprite sprite);
	
	IModelBakerModel getCurrentModel();
	
	IModelBakerModel bakeModel(boolean flip);
	
	IModelBakerModel clear();
	
}