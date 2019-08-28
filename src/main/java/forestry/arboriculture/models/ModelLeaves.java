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
package forestry.arboriculture.models;

import javax.annotation.Nullable;
import java.util.Objects;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.property.IExtendedBlockState;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeRoot;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.models.ModelBlockCached;
import forestry.core.models.baker.ModelBaker;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;

@OnlyIn(Dist.CLIENT)
public class ModelLeaves extends ModelBlockCached<BlockForestryLeaves, ModelLeaves.Key> {
	public static class Key {
		public final TextureAtlasSprite leafSprite;
		@Nullable
		public final TextureAtlasSprite fruitSprite;
		public final boolean fancy;
		private final int hashCode;

		public Key(TextureAtlasSprite leafSprite, @Nullable TextureAtlasSprite fruitSprite, boolean fancy) {
			this.leafSprite = leafSprite;
			this.fruitSprite = fruitSprite;
			this.fancy = fancy;
			this.hashCode = Objects.hash(leafSprite, fruitSprite, fancy);
		}

		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof Key)) {
				return false;
			} else {
				Key otherKey = (Key) other;
				return otherKey.leafSprite == leafSprite && otherKey.fruitSprite == fruitSprite && otherKey.fancy == fancy;
			}
		}

		@Override
		public int hashCode() {
			return hashCode;
		}
	}

	@Override
	protected Key getInventoryKey(ItemStack itemStack) {
		AtlasTexture map = Minecraft.getInstance().getTextureMapBlocks();

		TileLeaves leaves = new TileLeaves();
		if (itemStack.getTag() != null) {
			leaves.readFromNBT(itemStack.getTag());
		} else {
			leaves.setTree(TreeRoot.treeTemplates.get(0));
		}

		boolean fancy = Proxies.render.fancyGraphicsEnabled();
		ResourceLocation leafLocation = leaves.getLeaveSprite(fancy);
		ResourceLocation fruitLocation = leaves.getFruitSprite();

		return new Key(map.getAtlasSprite(leafLocation.toString()),
			fruitLocation != null ? map.getAtlasSprite(fruitLocation.toString()) : null,
			fancy);
	}

	@Override
	protected Key getWorldKey(BlockState state) {
		IExtendedBlockState stateExtended = (IExtendedBlockState) state;
		IBlockReader world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
		BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);

		boolean fancy = Proxies.render.fancyGraphicsEnabled();
		AtlasTexture map = Minecraft.getInstance().getTextureMapBlocks();

		if (world == null || pos == null) {
			return createEmptyKey(map, fancy);
		}

		TileLeaves tile = TileUtil.getTile(world, pos, TileLeaves.class);

		if (tile == null) {
			return createEmptyKey(map, fancy);
		}

		ResourceLocation leafLocation = tile.getLeaveSprite(fancy);
		ResourceLocation fruitLocation = tile.getFruitSprite();

		return new Key(map.getAtlasSprite(leafLocation.toString()),
			fruitLocation != null ? map.getAtlasSprite(fruitLocation.toString()) : null,
			fancy);
	}

	private Key createEmptyKey(AtlasTexture map, boolean fancy) {
		IAlleleTreeSpecies oakSpecies = TreeDefinition.Oak.getIndividual().getGenome().getPrimary();
		ResourceLocation spriteLocation = oakSpecies.getLeafSpriteProvider().getSprite(false, fancy);
		TextureAtlasSprite sprite = map.getAtlasSprite(spriteLocation.toString());
		return new Key(sprite, null, fancy);
	}

	@Override
	protected void bakeBlock(BlockForestryLeaves block, Key key, ModelBaker baker, boolean inventory) {
		// Render the plain leaf block.
		baker.addBlockModel(null, key.leafSprite, BlockAbstractLeaves.FOLIAGE_COLOR_INDEX);

		if (key.fruitSprite != null) {
			baker.addBlockModel(null, key.fruitSprite, BlockAbstractLeaves.FRUIT_COLOR_INDEX);
		}

		// Set the particle sprite
		baker.setParticleSprite(key.leafSprite);
	}

	public ModelLeaves() {
		super(BlockForestryLeaves.class);
	}
}
