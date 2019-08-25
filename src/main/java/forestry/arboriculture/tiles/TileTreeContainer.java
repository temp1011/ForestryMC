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
package forestry.arboriculture.tiles;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.genetics.Tree;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.utils.NBTUtilForestry;

/**
 * This is the base TE class for any block that needs to contain tree genome information.
 *
 * @author SirSengir
 */
public abstract class TileTreeContainer extends TileEntity implements IStreamable, IOwnedTile {

	@Nullable
	private ITree containedTree;
	private final OwnerHandler ownerHandler = new OwnerHandler();

	/* SAVING & LOADING */
	@Override
	public void read(CompoundNBT compoundNBT) {
		super.read(compoundNBT);

		if (compoundNBT.contains("ContainedTree")) {
			containedTree = new Tree(compoundNBT.getCompound("ContainedTree"));
		}
		ownerHandler.read(compoundNBT);
	}

	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		compoundNBT = super.write(compoundNBT);

		if (containedTree != null) {
			CompoundNBT subcompound = new CompoundNBT();
			containedTree.write(subcompound);
			compoundNBT.put("ContainedTree", subcompound);
		}
		ownerHandler.write(compoundNBT);

		return compoundNBT;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		String speciesUID = "";
		ITree tree = getTree();
		if (tree != null) {
			speciesUID = tree.getIdent();
		}
		data.writeString(speciesUID);
	}

	@Override
	public void readData(PacketBufferForestry data) {
		String speciesUID = data.readString();
		ITree tree = getTree(speciesUID);
		setTree(tree);
	}

	private static ITree getTree(String speciesUID) {
		IAllele[] treeTemplate = TreeManager.treeRoot.getTemplate(speciesUID);
		Preconditions.checkArgument(treeTemplate != null, "There is no tree template for speciesUID %s", speciesUID);
		return TreeManager.treeRoot.templateAsIndividual(treeTemplate);
	}

	/* CLIENT INFORMATION */

	/* CONTAINED TREE */
	public void setTree(ITree tree) {
		this.containedTree = tree;
		if (world != null && world.isRemote) {
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	@Nullable
	public ITree getTree() {
		return this.containedTree;
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	/* UPDATING */

	/**
	 * Leaves and saplings will implement their logic here.
	 */
	public abstract void onBlockTick(World worldIn, BlockPos pos, BlockState state, Random rand);

	/**
	 * Called from Chunk.setBlockIDWithMetadata, determines if this tile entity should be re-created when the ID, or Metadata changes.
	 * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
		return !Block.isEqualTo(oldState.getBlock(), newSate.getBlock());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.getPos(), 0, getUpdateTag());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundNBT nbt = pkt.getNbtCompound();
		handleUpdateTag(nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		return NBTUtilForestry.writeStreamableToNbt(this, tag);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleUpdateTag(CompoundNBT tag) {
		super.handleUpdateTag(tag);
		NBTUtilForestry.readStreamableFromNbt(this, tag);
	}

}
