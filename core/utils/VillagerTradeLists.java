package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class VillagerTradeLists {
	//TODO find where this class is now and copy it
	/**
	 * Copy of {@link VillagerEntity.ItemAndEmeraldToItem}
	 * that takes ItemStacks as parameters and has emerald price info
	 */
	public static class GiveItemForItemAndEmerald implements VillagerEntity.ITradeList {
		public final ItemStack buyingItemStack;
		public final ItemStack sellingItemstack;

		@Nullable
		public final VillagerEntity.PriceInfo buyingPriceInfo;
		@Nullable
		public final VillagerEntity.PriceInfo emeraldPriceInfo;
		@Nullable
		public final VillagerEntity.PriceInfo sellingPriceInfo;

		public GiveItemForItemAndEmerald(
			ItemStack buyingItemStack,
			@Nullable VillagerEntity.PriceInfo buyingPriceInfo,
			@Nullable VillagerEntity.PriceInfo emeraldPriceInfo,
			ItemStack sellingItemstack,
			@Nullable VillagerEntity.PriceInfo sellingPriceInfo) {
			this.buyingItemStack = buyingItemStack;
			this.buyingPriceInfo = buyingPriceInfo;
			this.emeraldPriceInfo = emeraldPriceInfo;
			this.sellingItemstack = sellingItemstack;
			this.sellingPriceInfo = sellingPriceInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			int buyAmount = 1;
			if (this.buyingPriceInfo != null) {
				buyAmount = this.buyingPriceInfo.getPrice(random);
			}

			int emeraldAmount = 1;
			if (this.emeraldPriceInfo != null) {
				emeraldAmount = this.emeraldPriceInfo.getPrice(random);
			}

			int sellAmount = 1;
			if (this.sellingPriceInfo != null) {
				sellAmount = this.sellingPriceInfo.getPrice(random);
			}

			ItemStack buyItemStack = this.buyingItemStack.copy();
			buyItemStack.setCount(buyAmount);
			ItemStack sellItemStack = this.sellingItemstack.copy();
			sellItemStack.setCount(sellAmount);
			recipeList.add(new MerchantRecipe(buyItemStack, new ItemStack(Items.EMERALD, emeraldAmount, 0), sellItemStack));
		}
	}

	/**
	 * Copy of {@link VillagerEntity.EmeraldForItems}
	 * that takes itemStack as a parameter
	 */
	public static class GiveEmeraldForItems implements VillagerEntity.ITradeList {
		public final ItemStack buyingItem;
		@Nullable
		public final VillagerEntity.PriceInfo price;

		public GiveEmeraldForItems(ItemStack itemIn, @Nullable VillagerEntity.PriceInfo priceIn) {
			this.buyingItem = itemIn;
			this.price = priceIn;
		}

		/**
		 * Affects the given MerchantRecipeList to possibly add or remove MerchantRecipes.
		 */
		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			int buyAmount = 1;
			if (this.price != null) {
				buyAmount = this.price.getPrice(random);
			}

			ItemStack itemToBuy = this.buyingItem.copy();
			itemToBuy.setCount(buyAmount);
			recipeList.add(new MerchantRecipe(itemToBuy, Items.EMERALD));
		}
	}

	/**
	 * Copy of {@link VillagerEntity.ListItemForEmeralds}
	 * that copies itemStacks properly
	 */
	public static class GiveItemForEmeralds implements VillagerEntity.ITradeList {
		public final ItemStack itemToSell;
		@Nullable
		public final VillagerEntity.PriceInfo emeraldPriceInfo;
		@Nullable
		public final VillagerEntity.PriceInfo sellInfo;

		public GiveItemForEmeralds(@Nullable VillagerEntity.PriceInfo emeraldPriceInfo, ItemStack itemToSell, @Nullable VillagerEntity.PriceInfo sellInfo) {
			this.emeraldPriceInfo = emeraldPriceInfo;
			this.itemToSell = itemToSell;
			this.sellInfo = sellInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			int i = 1;
			if (this.sellInfo != null) {
				i = this.sellInfo.getPrice(random);
			}

			int j = 1;
			if (this.emeraldPriceInfo != null) {
				j = this.emeraldPriceInfo.getPrice(random);
			}

			ItemStack sellStack = this.itemToSell.copy();
			sellStack.setCount(i);

			ItemStack emeralds = new ItemStack(Items.EMERALD, j);

			recipeList.add(new MerchantRecipe(emeralds, sellStack));
		}
	}

	public static class GiveItemForLogsAndEmeralds implements VillagerEntity.ITradeList {

		public final ItemStack itemToSell;

		public final VillagerEntity.PriceInfo itemInfo;

		public final VillagerEntity.PriceInfo logsInfo;

		public final VillagerEntity.PriceInfo emeraldsInfo;

		public GiveItemForLogsAndEmeralds(ItemStack itemToSell, VillagerEntity.PriceInfo itemInfo, VillagerEntity.PriceInfo logsInfo, VillagerEntity.PriceInfo emeraldsInfo) {
			this.itemToSell = itemToSell;
			this.itemInfo = itemInfo;
			this.logsInfo = logsInfo;
			this.emeraldsInfo = emeraldsInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			int itemAmount = this.itemInfo.getPrice(random);
			int emeraldsAmount = this.emeraldsInfo.getPrice(random);
			int logsAmount = this.logsInfo.getPrice(random);

			ItemStack itemToSell = this.itemToSell.copy();
			itemToSell.setCount(itemAmount);

			int logMeta = random.nextInt(6);
			Block log;
			if (logMeta < 4) {
				log = Blocks.LOG;
			} else {
				log = Blocks.LOG2;
				logMeta -= 4;
			}
			ItemStack randomLog = new ItemStack(log, logsAmount, logMeta);

			recipeList.add(new MerchantRecipe(randomLog, new ItemStack(Items.EMERALD, emeraldsAmount), itemToSell));
		}
	}

	public static class GiveItemForTwoItems implements VillagerEntity.ITradeList {

		public final ItemStack buyingItemStack;
		@Nullable
		public final VillagerEntity.PriceInfo buyingPriceInfo;

		public final ItemStack buyingItemStackTwo;
		@Nullable
		public final VillagerEntity.PriceInfo buyingPriceItemTwoInfo;

		public final ItemStack sellingItemstack;
		@Nullable
		public final VillagerEntity.PriceInfo sellingPriceInfo;

		public GiveItemForTwoItems(
			ItemStack buyingItemStack,
			@Nullable VillagerEntity.PriceInfo buyingPriceInfo,
			ItemStack buyingItemStackTwo,
			@Nullable VillagerEntity.PriceInfo buyingPriceItemTwoInfo,
			ItemStack sellingItemstack,
			@Nullable VillagerEntity.PriceInfo sellingPriceInfo) {
			this.buyingItemStack = buyingItemStack;
			this.buyingPriceInfo = buyingPriceInfo;
			this.buyingItemStackTwo = buyingItemStackTwo;
			this.buyingPriceItemTwoInfo = buyingPriceItemTwoInfo;
			this.sellingItemstack = sellingItemstack;
			this.sellingPriceInfo = sellingPriceInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			int buyAmount = 1;
			if (this.buyingPriceInfo != null) {
				buyAmount = this.buyingPriceInfo.getPrice(random);
			}

			int buyTwoAmount = 1;
			if (this.buyingPriceItemTwoInfo != null) {
				buyTwoAmount = this.buyingPriceItemTwoInfo.getPrice(random);
			}

			int sellAmount = 1;
			if (this.sellingPriceInfo != null) {
				sellAmount = this.sellingPriceInfo.getPrice(random);
			}

			ItemStack buyItemStack = this.buyingItemStack.copy();
			buyItemStack.setCount(buyAmount);
			ItemStack buyItemStackTwo = this.buyingItemStackTwo.copy();
			buyItemStackTwo.setCount(buyTwoAmount);
			ItemStack sellItemStack = this.sellingItemstack.copy();
			sellItemStack.setCount(sellAmount);
			recipeList.add(new MerchantRecipe(buyItemStack, buyItemStackTwo, sellItemStack));
		}
	}
}
