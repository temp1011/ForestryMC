package forestry.core.render;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import com.mojang.blaze3d.platform.TextureUtil;

import net.minecraftforge.fml.client.ClientHooks;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ProgressManager;

import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.core.utils.Log;

@OnlyIn(Dist.CLIENT)
public class TextureMapForestry extends AtlasTexture {
	public TextureMapForestry(String basePathIn) {
		super(basePathIn);
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) {
		this.initMissingImage();
		this.deleteGlTexture();
		this.loadTextureAtlas(resourceManager);
	}

	@Override
	public void loadTextureAtlas(IResourceManager resourceManager) {
		int i = Minecraft.getGLMaximumTextureSize();
		Stitcher stitcher = new Stitcher(i, i, 0);	//TODO check args correct
		this.mapUploadedSprites.clear();	//TODO - needs AT
		this.listAnimatedSprites.clear();	//TODO - needs AT

		ProgressManager.ProgressBar bar = ProgressManager.push("Texture stitching", this.mapRegisteredSprites.size());

		for (Map.Entry<String, TextureAtlasSprite> entry : this.mapRegisteredSprites.entrySet()) {
			TextureAtlasSprite textureatlassprite = entry.getValue();
			ResourceLocation resourcelocation = this.getResourceLocation(textureatlassprite);
			bar.step(resourcelocation.getPath());
			IResource iresource = null;

			if (textureatlassprite.hasCustomLoader(resourceManager, resourcelocation)) {
				if (textureatlassprite.load(resourceManager, resourcelocation, l -> mapRegisteredSprites.get(l.toString()))) {
					continue;
				}
			} else {
				try {
					PngSizeInfo pngsizeinfo = new PngSizeInfo("TEST FILE", resourceManager.getResource(resourcelocation).getInputStream());	//TODO - what to put as first param
					iresource = resourceManager.getResource(resourcelocation);
					boolean flag = iresource.getMetadata("animation") != null;
					textureatlassprite.loadSprite(pngsizeinfo, flag);
				} catch (RuntimeException runtimeexception) {
					ClientHooks.trackBrokenTexture(resourcelocation, runtimeexception.getMessage());
					continue;
				} catch (IOException ioexception) {
					ClientHooks.trackMissingTexture(resourcelocation);
					continue;
				} finally {
					IOUtils.closeQuietly(iresource);
				}
			}

			stitcher.addSprite(textureatlassprite);
		}

		ProgressManager.pop(bar);

		this.missingImage.generateMipmaps(0);
		stitcher.addSprite(this.missingImage);
		bar = ProgressManager.push("Texture creation", 2);

		bar.step("Stitching");
		stitcher.doStitch();

		Log.info("Created: {}x{} {}-atlas", stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), this.basePath);
		bar.step("Allocating GL texture");
		TextureUtil.prepareImage(this.getGlTextureId(), 0, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());	//TODO - check
		Map<String, TextureAtlasSprite> map = Maps.newHashMap(this.mapRegisteredSprites);

		ProgressManager.pop(bar);
		bar = ProgressManager.push("Texture mipmap and upload", stitcher.getStichSlots().size());

		for (TextureAtlasSprite textureatlassprite1 : stitcher.getStichSlots()) {
			bar.step(textureatlassprite1.getIconName());
			if (textureatlassprite1 == this.missingImage || this.generateMipmaps(resourceManager, textureatlassprite1)) {
				String s = textureatlassprite1.getIconName();
				map.remove(s);
				this.mapUploadedSprites.put(s, textureatlassprite1);

				try {
					TextureUtil.uploadTextureMipmap(textureatlassprite1.getFrameTextureData(0), textureatlassprite1.getIconWidth(), textureatlassprite1.getIconHeight(), textureatlassprite1.getOriginX(), textureatlassprite1.getOriginY(), false, false);
				} catch (Throwable throwable) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
					crashreportcategory.addCrashSection("Atlas path", this.basePath);
					crashreportcategory.addCrashSection("Sprite", textureatlassprite1);
					throw new ReportedException(crashreport);
				}

				if (textureatlassprite1.hasAnimationMetadata()) {
					this.listAnimatedSprites.add(textureatlassprite1);
				}
			}
		}

		for (TextureAtlasSprite textureatlassprite2 : map.values()) {
			textureatlassprite2.copyFrom(this.missingImage);
		}

		ProgressManager.pop(bar);
	}

	private boolean generateMipmaps(IResourceManager resourceManager, final TextureAtlasSprite texture) {
		ResourceLocation resourcelocation = this.getResourceLocation(texture);
		IResource iresource = null;
		label9:
		{
			boolean flag;
			if (texture.hasCustomLoader(resourceManager, resourcelocation)) {
				break label9;
			}
			try {
				iresource = resourceManager.getResource(resourcelocation);
				texture.loadSpriteFrames(iresource, 1);
				break label9;
			} catch (RuntimeException runtimeexception) {
				Log.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
				flag = false;
			} catch (IOException ioexception) {
				Log.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
				flag = false;
				return flag;
			} finally {
				IOUtils.closeQuietly(iresource);
			}

			return flag;
		}

		try {
			texture.generateMipmaps(0);
			return true;
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Applying mipmap");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
			crashreportcategory.addDetail("Sprite name", texture::getIconName);
			crashreportcategory.addDetail("Sprite size", () -> texture.getIconWidth() + " x " + texture.getIconHeight());
			crashreportcategory.addDetail("Sprite frames", () -> texture.getFrameCount() + " frames");
			crashreportcategory.addCrashSection("Mipmap levels", 0);
			throw new ReportedException(crashreport);
		}
	}

	private ResourceLocation getResourceLocation(TextureAtlasSprite p_184396_1_) {
		ResourceLocation resourcelocation = new ResourceLocation(p_184396_1_.getIconName());
		return new ResourceLocation(resourcelocation.getNamespace(), String.format("%s/%s%s", this.basePath, resourcelocation.getPath(), ".png"));	//TODO - AT
	}
}
