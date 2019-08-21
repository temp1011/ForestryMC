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
package forestry.mail.items;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.network.NetworkHooks;

import forestry.api.core.IModelManager;
import forestry.api.mail.ILetter;
import forestry.core.ItemGroupForestry;
import forestry.core.items.ItemWithGui;
import forestry.core.utils.Translator;
import forestry.mail.Letter;
import forestry.mail.LetterProperties;
import forestry.mail.gui.ContainerCatalogue;
import forestry.mail.gui.ContainerLetter;
import forestry.mail.gui.GuiLetter;
import forestry.mail.inventory.ItemInventoryLetter;

public class ItemLetter extends ItemWithGui {

	public enum State {
		FRESH, STAMPED, OPENED, EMPTIED
	}

	public enum Size {
		EMPTY, SMALL, BIG
	}

	private Size size;
	private State state;

	public ItemLetter(Size size, State state) {
		super((new Item.Properties())
		.group(ItemGroupForestry.tabForestry)
		.maxStackSize(64));
		this.size = size;
		this.state = state;
	}

	public Size getSize() {
		return size;
	}

	public State getState() {
		return state;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack heldItem = playerIn.getHeldItem(handIn);
		if (heldItem.getCount() == 1) {
			return super.onItemRightClick(worldIn, playerIn, handIn);
		} else {
			playerIn.sendMessage(new TranslationTextComponent("for.chat.mail.wrongstacksize"));
			return ActionResult.newResult(ActionResultType.FAIL, heldItem);
		}
	}

	//TODO I think this is correct replacement
	@Override
	public boolean shouldSyncTag() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);

		CompoundNBT compoundNBT = itemstack.getTag();
		if (compoundNBT == null) {
			list.add(new StringTextComponent("<").appendSibling(new TranslationTextComponent("for.gui.blank").appendText(">")));
			return;
		}

		ILetter letter = new Letter(compoundNBT);
		letter.addTooltip(list);
	}

	@Override
	protected void openGui(ServerPlayerEntity playerEntity, ItemStack stack) {
		NetworkHooks.openGui(playerEntity, new ContainerProvider(stack), b -> b.writeBoolean(playerEntity.getActiveHand() == Hand.MAIN_HAND));
	}

	@Nullable
	@Override
	public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
		return new ContainerLetter(windowId, player, new ItemInventoryLetter(player, heldItem));
	}

	//TODO see if this can be deduped. Given we pass in the held item etc.
	public static class ContainerProvider implements INamedContainerProvider {

		private ItemStack heldItem;

		public ContainerProvider(ItemStack heldItem) {
			this.heldItem = heldItem;
		}

		@Override
		public ITextComponent getDisplayName() {
			return new StringTextComponent("ITEM_GUI_TITLE");    //TODO needs to be overriden individually
		}

		@Nullable
		@Override
		public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
			return new ContainerLetter(windowId, playerEntity, new ItemInventoryLetter(playerEntity, heldItem));
		}
	}
}
