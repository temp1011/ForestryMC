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
package forestry.apiculture.genetics.alleles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.ItemStackUtil;

public class AlleleEffectResurrection extends AlleleEffectThrottled {

	public static class Resurrectable {
		public final ItemStack res;
		public final Class<? extends MobEntity> risen;
		public final Optional<Consumer<MobEntity>> risenTransformer;

		public Resurrectable(ItemStack res, Class<? extends MobEntity> risen) {
			this.res = res;
			this.risen = risen;
			this.risenTransformer = Optional.empty();
		}

		public <E extends MobEntity> Resurrectable(ItemStack res, Class<E> risen, Consumer<E> risenTransformer) {
			this.res = res;
			this.risen = risen;
			this.risenTransformer = Optional.of((Consumer<MobEntity>) risenTransformer);
		}
	}

	public static List<Resurrectable> getReanimationList() {
		ArrayList<Resurrectable> list = new ArrayList<>();
		list.add(new Resurrectable(new ItemStack(Items.BONE), SkeletonEntity.class));
		list.add(new Resurrectable(new ItemStack(Items.ARROW), SkeletonEntity.class));
		list.add(new Resurrectable(new ItemStack(Items.ROTTEN_FLESH), ZombieEntity.class));
		list.add(new Resurrectable(new ItemStack(Items.BLAZE_ROD), BlazeEntity.class));
		return list;
	}

	public static List<Resurrectable> getResurrectionList() {
		ArrayList<Resurrectable> list = new ArrayList<>();
		list.add(new Resurrectable(new ItemStack(Items.GUNPOWDER), CreeperEntity.class));
		list.add(new Resurrectable(new ItemStack(Items.ENDER_PEARL), EndermanEntity.class));
		list.add(new Resurrectable(new ItemStack(Items.STRING), SpiderEntity.class));
		list.add(new Resurrectable(new ItemStack(Items.SPIDER_EYE), SpiderEntity.class));
		list.add(new Resurrectable(new ItemStack(Items.STRING), CaveSpiderEntity.class));
		list.add(new Resurrectable(new ItemStack(Items.SPIDER_EYE), CaveSpiderEntity.class));
		list.add(new Resurrectable(new ItemStack(Items.GHAST_TEAR), GhastEntity.class));
		list.add(new Resurrectable(new ItemStack(Blocks.DRAGON_EGG), EnderDragonEntity.class, dragon -> dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN)));
		return list;
	}

	private final List<Resurrectable> resurrectables;

	public AlleleEffectResurrection(String name, List<Resurrectable> resurrectables) {
		super(name, true, 40, true, true);
		this.resurrectables = resurrectables;
	}

	@Override
	public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		List<ItemEntity> entities = getEntitiesInRange(genome, housing, ItemEntity.class);
		if (entities.isEmpty()) {
			return storedData;
		}

		Collections.shuffle(resurrectables);

		for (ItemEntity entity : entities) {
			if (resurrectEntity(entity)) {
				break;
			}
		}

		return storedData;
	}

	private boolean resurrectEntity(ItemEntity entity) {
		if (entity.isDead) {
			return false;
		}

		ItemStack contained = entity.getItem();
		for (Resurrectable entry : resurrectables) {
			if (ItemStackUtil.isIdenticalItem(entry.res, contained)) {
				MobEntity spawnedEntity = EntityUtil.spawnEntity(entity.world, entry.risen, entity.posX, entity.posY, entity.posZ);
				if (spawnedEntity != null) {
					entry.risenTransformer.ifPresent(transformer -> transformer.accept(spawnedEntity));
				}

				contained.shrink(1);

				if (contained.getCount() <= 0) {
					entity.setDead();
				}

				return true;
			}
		}

		return false;
	}
}
