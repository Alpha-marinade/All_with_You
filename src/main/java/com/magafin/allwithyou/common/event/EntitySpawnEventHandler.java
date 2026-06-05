package com.magafin.allwithyou.common.event;

import com.magafin.allwithyou.all_with_you.All_with_you;
import com.magafin.allwithyou.common.config.Config;
import com.magafin.allwithyou.common.register.ItemsReg;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import java.util.List;

@EventBusSubscriber(modid = All_with_you.MODID, bus = EventBusSubscriber.Bus.GAME)
public class EntitySpawnEventHandler {

    private static final ResourceKey<LootTable> BACKPACK_LOOT_ZOMBIE = ResourceKey.create(
            net.minecraft.core.registries.Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(All_with_you.MODID, "chests/zombie_backpack")
    );
    private static final ResourceKey<LootTable> BACKPACK_LOOT_SKELETON = ResourceKey.create(
            net.minecraft.core.registries.Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(All_with_you.MODID, "chests/skeleton_backpack")
    );

    @SubscribeEvent
    public static void onZombieSpawn(EntityJoinLevelEvent event) {
        if (!Config.ENABLE_ZOMBIE_SPAWN_WITH_BACKPACK.get()) {
            return;
        }

        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverLevel && event.getEntity() instanceof Zombie zombie) {

            if (zombie.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
                RandomSource random = zombie.getRandom();

                float spawnChance = Config.ZOMBIE_BACKPACK_SPAWN_CHANCE.get().floatValue();

                if (random.nextFloat() < spawnChance) {
                    LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(BACKPACK_LOOT_ZOMBIE);
                    LootParams lootParams = new Builder(serverLevel).create(LootContextParamSets.EMPTY);
                    List<ItemStack> generatedItems = lootTable.getRandomItems(lootParams, random.nextLong());

                    if (!generatedItems.isEmpty()) {
                        ItemStack backpack = new ItemStack(ItemsReg.BACKPACK.get());
                        BundleContents bundleContents = new BundleContents(generatedItems);
                        backpack.set(DataComponents.BUNDLE_CONTENTS, bundleContents);

                        backpack.set(DataComponents.DYED_COLOR, new DyedItemColor(9401679, false));
                        backpack.set(DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.translatable("item.all_with_you.zombie_backpack"));

                        zombie.setItemSlot(EquipmentSlot.CHEST, backpack);
                        zombie.setDropChance(EquipmentSlot.CHEST, 1.0f);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onSkeletonSpawn(EntityJoinLevelEvent event) {
        if (!Config.ENABLE_SKELETON_SPAWN_WITH_BACKPACK.get()) {
            return;
        }

        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverLevel && event.getEntity() instanceof Skeleton skeleton) {

            if (skeleton.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
                RandomSource random = skeleton.getRandom();

                float spawnChance = Config.SKELETON_BACKPACK_SPAWN_CHANCE.get().floatValue();

                if (random.nextFloat() < spawnChance) {
                    LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(BACKPACK_LOOT_SKELETON);
                    LootParams lootParams = new Builder(serverLevel).create(LootContextParamSets.EMPTY);
                    List<ItemStack> generatedItems = lootTable.getRandomItems(lootParams, random.nextLong());

                    if (!generatedItems.isEmpty()) {
                        ItemStack backpack = new ItemStack(ItemsReg.BACKPACK.get());
                        BundleContents bundleContents = new BundleContents(generatedItems);
                        backpack.set(DataComponents.BUNDLE_CONTENTS, bundleContents);

                        backpack.set(DataComponents.DYED_COLOR, new DyedItemColor(10119508, false));
                        backpack.set(DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.translatable("item.all_with_you.skeleton_backpack"));

                        skeleton.setItemSlot(EquipmentSlot.CHEST, backpack);
                        skeleton.setDropChance(EquipmentSlot.CHEST, 1.0f);
                    }
                }
            }
        }
    }
}
