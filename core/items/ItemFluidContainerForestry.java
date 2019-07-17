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
package forestry.core.items;

import javax.annotation.Nullable;
import java.util.Locale;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;


import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.api.distmarker.OnlyIn;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.Translator;

public class ItemFluidContainerForestry extends ItemForestry {
	private final EnumContainerType type;

	public ItemFluidContainerForestry(EnumContainerType type) {
		super(CreativeTabForestry.tabForestry);
		this.type = type;
	}

	/* Models */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		String identifier = "liquids/" + type.toString().toLowerCase(Locale.ENGLISH);
		manager.registerItemModel(item, 0, identifier + "_empty");
		ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, identifier), "inventory"));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getSubItems(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			// empty
			subItems.add(new ItemStack(this));

			// filled
			for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
				ItemStack itemStack = new ItemStack(this);
				IFluidHandlerItem fluidHandler = new FluidHandlerItemForestry(itemStack, type);
				if (fluidHandler.fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true) == Fluid.BUCKET_VOLUME) {
					ItemStack filled = fluidHandler.getContainer();
					subItems.add(filled);
				}
			}
		}
	}

	public EnumContainerType getType() {
		return type;
	}

	@Nullable
	protected FluidStack getContained(ItemStack itemStack) {
		if (itemStack.getCount() != 1) {
			itemStack = itemStack.copy();
			itemStack.setCount(1);
		}
		IFluidHandler fluidHandler = new FluidHandlerItemForestry(itemStack, type);
		return fluidHandler.drain(Integer.MAX_VALUE, false);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemFluidContainerForestry) {
			FluidStack fluid = getContained(stack);
			if (fluid != null) {
				String exactTranslationKey = "item.for." + type.getName() + '.' + fluid.getFluid().getName() + ".name";
				if (Translator.canTranslateToLocal(exactTranslationKey)) {
					return Translator.translateToLocal(exactTranslationKey);
				} else {
					String grammarKey = "item.for." + type.getName() + ".grammar";
					return Translator.translateToLocalFormatted(grammarKey, fluid.getLocalizedName());
				}
			} else {
				String unlocalizedname = "item.for." + type.getName() + ".empty.name";
				return Translator.translateToLocal(unlocalizedname);
			}
		}
		return super.getItemStackDisplayName(stack);
	}

	/**
	 * DRINKS
	 */
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		DrinkProperties drinkProperties = getDrinkProperties(stack);
		if (drinkProperties != null) {
			if (entityLiving instanceof PlayerEntity && !((PlayerEntity) entityLiving).capabilities.isCreativeMode) {
				PlayerEntity player = (PlayerEntity) entityLiving;
				if (!player.capabilities.isCreativeMode) {
					stack.shrink(1);
				}

				if (!worldIn.isRemote) {
					FoodStats foodStats = player.getFoodStats();
					foodStats.addStats(drinkProperties.getHealAmount(), drinkProperties.getSaturationModifier());
					worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
				}

				player.addStat(Stats.getObjectUseStats(this));
			}
		}
		return stack;
	}

	@Nullable
	protected DrinkProperties getDrinkProperties(ItemStack itemStack) {
		FluidStack contained = getContained(itemStack);
		if (contained != null) {
			Fluids definition = Fluids.getFluidDefinition(contained);
			if (definition != null) {
				return definition.getDrinkProperties();
			}
		}
		return null;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		DrinkProperties drinkProperties = getDrinkProperties(itemstack);
		if (drinkProperties != null) {
			return drinkProperties.getMaxItemUseDuration();
		} else {
			return super.getMaxItemUseDuration(itemstack);
		}
	}

	@Override
	public UseAction getItemUseAction(ItemStack itemstack) {
		DrinkProperties drinkProperties = getDrinkProperties(itemstack);
		if (drinkProperties != null) {
			return UseAction.DRINK;
		} else {
			return UseAction.NONE;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand handIn) {
		ItemStack heldItem = player.getHeldItem(handIn);
		DrinkProperties drinkProperties = getDrinkProperties(heldItem);
		if (drinkProperties != null) {
			if (player.canEat(false)) {
				player.setActiveHand(handIn);
				return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
			} else {
				return new ActionResult<>(ActionResultType.FAIL, heldItem);
			}
		} else {
			if (Config.CapsuleFluidPickup) {
				RayTraceResult target = this.rayTrace(world, player, true);
				if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK) {
					return ActionResult.newResult(ActionResultType.PASS, heldItem);
				}

				ItemStack singleBucket = heldItem.copy();
				singleBucket.setCount(1);

				FluidActionResult filledResult = FluidUtil.tryPickUpFluid(singleBucket, player, world, target.getBlockPos(), target.sideHit);
				if (filledResult.isSuccess()) {
					ItemHandlerHelper.giveItemToPlayer(player, filledResult.result);

					if (!player.capabilities.isCreativeMode) {
						// Remove consumed empty container
						heldItem.shrink(1);
					}

					return ActionResult.newResult(ActionResultType.SUCCESS, heldItem);
				}
			}
			return super.onItemRightClick(world, player, handIn);
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new FluidHandlerItemForestry(stack, type);
	}
}