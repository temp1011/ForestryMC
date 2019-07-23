package forestry.core.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.loading.FMLEnvironment;

import forestry.core.tiles.TileForestry;

public class MachinePropertiesTesr<T extends TileForestry> extends MachineProperties<T> implements IMachinePropertiesTesr<T> {
	@Nullable
	@OnlyIn(Dist.CLIENT)
	private TileEntityRenderer<? super T> renderer;

	private final String particleTextureLocation;
	private final boolean isFullCube;

	public MachinePropertiesTesr(Class<T> teClass, String name, String particleTextureLocation) {
		this(teClass, name, particleTextureLocation, true);
	}

	public MachinePropertiesTesr(Class<T> teClass, String name, AxisAlignedBB boundingBox, String particleTextureLocation) {
		this(teClass, name, boundingBox, particleTextureLocation, true);
	}

	public MachinePropertiesTesr(Class<T> teClass, String name, String particleTextureLocation, boolean isFullCube) {
		super(teClass, name);
		this.particleTextureLocation = particleTextureLocation;
		this.isFullCube = isFullCube;
	}

	public MachinePropertiesTesr(Class<T> teClass, String name, AxisAlignedBB boundingBox, String particleTextureLocation, boolean isFullCube) {
		super(teClass, name, boundingBox);
		this.particleTextureLocation = particleTextureLocation;
		this.isFullCube = isFullCube;
	}

	@OnlyIn(Dist.CLIENT)
	public void setRenderer(TileEntityRenderer<? super T> renderer) {
		this.renderer = renderer;
	}

	//TODO sides
	@Override
	public void registerTileEntity() {
		super.registerTileEntity();
		Block block = getBlock();
		if (FMLEnvironment.dist == Dist.CLIENT && renderer != null && block != null) {
			ClientRegistry.bindTileEntitySpecialRenderer(getTeClass(), renderer);
			Item item = Item.getItemFromBlock(block);
			if (item != Items.AIR) {
				//TODO - how to register
//				ForgeHooksClient.registerTESRItemStack(item, 0, getTeClass());
			}
		}
	}

	@Override
	public String getParticleTextureLocation() {
		return particleTextureLocation;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return isFullCube;
	}
}
