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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
//TODO models
@OnlyIn(Dist.CLIENT)
public class MultipartModel implements IUnbakedModel {

	private final ResourceLocation location;
	private final Multipart multipart;
	private final ImmutableMap<Selector, IModel> partModels;

	public MultipartModel(ResourceLocation location, Multipart multipart) throws Exception {
		this.location = location;
		this.multipart = multipart;
		ImmutableMap.Builder<Selector, IModel> builder = ImmutableMap.builder();
		for (Selector selector : multipart.getSelectors()) {
			builder.put(selector, new SimpleModel(location, selector.getVariantList()));
		}
		partModels = builder.build();
	}

	private MultipartModel(ResourceLocation location, Multipart multipart, ImmutableMap<Selector, IModel> partModels) {
		this.location = location;
		this.multipart = multipart;
		this.partModels = partModels;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableSet.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
		return ImmutableSet.of();
	}

	@Nullable
	@Override
	public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format) {
//		MultipartBakedModel.Builder builder = new MultipartBakedModel.Builder();
//
//		for (Selector selector : multipart.getSelectors()) {
//			IModel model = partModels.get(selector);
//			IBakedModel bakedModel = model.bake(model.getDefaultState(), format, bakedTextureGetter);
//			builder.putModel(selector.getPredicate(multipart.getStateContainer()), bakedModel);
//		}
//
//		return builder.makeMultipartModel();
		return null;
	}

	@Override
	public IUnbakedModel retexture(ImmutableMap<String, String> textures) {
		try {
			ImmutableMap.Builder<Selector, IModel> builder = ImmutableMap.builder();
			for (Selector selector : multipart.getSelectors()) {
				IModel model = new SimpleModel(location, selector.getVariantList());
				model = model.retexture(textures);
				builder.put(selector, model);
			}
			return new MultipartModel(location, multipart, builder.build());
		} catch (Exception e) {
			return this;
		}
	}

	public Multipart getMultipart() {
		return multipart;
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

}
