package forestry.core.models;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import net.minecraftforge.client.model.IModel;

public class SimpleRetexturedModel extends BlankModel {

	public final IModel model;
	@Nullable
	public IBakedModel bakedModel;

	public SimpleRetexturedModel(IModel model) {
		this.model = model;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, long rand) {
		return getModel().getQuads(state, side, rand);
	}

	@Override
	protected ItemOverrideList createOverrides() {
		return new RetexturedOverrideList(model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, DefaultTextureGetter.INSTANCE));
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return getModel().getParticleTexture();
	}

	private IBakedModel getModel() {
		if (bakedModel == null) {
			return bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.BLOCK, DefaultTextureGetter.INSTANCE);
		}
		return bakedModel;
	}

	private static class RetexturedOverrideList extends ItemOverrideList {
		public final IBakedModel bakedModel;

		public RetexturedOverrideList(IBakedModel bakedModel) {
			super(Collections.emptyList());

			this.bakedModel = bakedModel;
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
			return bakedModel;
		}

	}

}
