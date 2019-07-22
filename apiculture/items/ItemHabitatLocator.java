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
package forestry.apiculture.items;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.ITextureManager;
import forestry.api.core.ItemGroups;
import forestry.api.genetics.AlleleManager;
import forestry.apiculture.gui.ContainerHabitatLocator;
import forestry.apiculture.gui.GuiHabitatLocator;
import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.apiculture.render.TextureHabitatLocator;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.items.ItemWithGui;
import forestry.core.utils.Translator;

public class ItemHabitatLocator extends ItemWithGui implements ISpriteRegister {
	private static final String iconName = "forestry:items/biomefinder";

	private final HabitatLocatorLogic locatorLogic;

	public ItemHabitatLocator() {
		super((new Item.Properties()).group(ItemGroups.tabApiculture).maxStackSize(1));
		locatorLogic = new HabitatLocatorLogic();
	}

	public HabitatLocatorLogic getLocatorLogic() {
		return locatorLogic;
	}

	@Override
	public void inventoryTick(ItemStack p_77663_1_, World world, Entity player, int p_77663_4_, boolean p_77663_5_) {
		if (!world.isRemote) {
			locatorLogic.onUpdate(world, player);
		}
	}

	/* SPRITES */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerSprites(ITextureManager manager) {
		TextureAtlasSprite texture = new TextureHabitatLocator(iconName);
//		Minecraft.getInstance().getTextureMap().setTextureEntry(texture);
		//TODO textures
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);

		Minecraft minecraft = Minecraft.getInstance();
		if (world != null && minecraft.player != null) {
			ClientPlayerEntity player = minecraft.player;
			Biome currentBiome = player.world.getBiome(player.getPosition());

			EnumTemperature temperature = EnumTemperature.getFromBiome(currentBiome, player.getPosition());
			EnumHumidity humidity = EnumHumidity.getFromValue(currentBiome.getDownfall());

			list.add(new TranslationTextComponent("for.gui.currentBiome")
					.appendSibling(new StringTextComponent(": "))
					.appendSibling(new TranslationTextComponent(currentBiome.getTranslationKey())));

			list.add(new TranslationTextComponent("for.gui.temperature")
					.appendSibling(new StringTextComponent(": "))
					.appendSibling(AlleleManager.climateHelper.toDisplay(temperature)));

			list.add(new TranslationTextComponent("for.gui.humidity")
					.appendSibling(new StringTextComponent(": "))
					.appendSibling(AlleleManager.climateHelper.toDisplay(humidity)));
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ContainerScreen getGui(PlayerEntity player, ItemStack heldItem, int data) {
		return new GuiHabitatLocator(player, new ItemInventoryHabitatLocator(player, heldItem), data);	//TODO windowid
	}

	@Override
	public Container getContainer(PlayerEntity player, ItemStack heldItem, int data) {
		return new ContainerHabitatLocator(player, new ItemInventoryHabitatLocator(player, heldItem), data);	//TODO windowid
	}
}
