package net.wheel.moblimiter.handler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.moblimiter.config.MLConfig;
import net.wheel.moblimiter.util.MLColor;
import net.wheel.moblimiter.util.Messages;

public class MLClearHandler {
    private int TICK_COUNT = 0;
    private static final int CHUNK_INIT_CAP = 16;
    private static final Object2ObjectOpenHashMap<ChunkPos, ObjectArrayList<LivingEntity>> COMBINED_MAP = new Object2ObjectOpenHashMap<>(
            256);
    private static final Comparator<LivingEntity> Y_SORT_COMP = Comparator.comparingDouble(LivingEntity::getY);

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        if (!(event instanceof ServerTickEvent.Post) || !MLConfig.isMobClearingEnabled())
            return;

        int interval = MLConfig.getClearTimer() * 20;
        if (++TICK_COUNT < interval)
            return;
        TICK_COUNT = 0;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            int removed = clearLevel(level);
            if (removed > 0) {
                sendClearMsg(level, removed);
            }
        }
    }

    public static void forceClear(ServerLevel level) {
        int removed = clearLevel(level);
        if (removed > 0)
            sendClearMsg(level, removed);
    }

    private static int clearLevel(ServerLevel level) {
        int removed = 0;
        COMBINED_MAP.clear();

        for (Entity entity : level.getEntities().getAll()) {
            if (!(entity instanceof LivingEntity living) || entity instanceof Player
                    || MLWhiteListHandler.whiteListed(entity) || entity.hasCustomName())
                continue;

            COMBINED_MAP
                    .computeIfAbsent(living.chunkPosition(), k -> new ObjectArrayList<>(CHUNK_INIT_CAP))
                    .add(living);
        }

        for (ObjectArrayList<LivingEntity> mobList : COMBINED_MAP.values()) {
            if (mobList.isEmpty())
                continue;
            mobList.sort(Y_SORT_COMP);

            Map<String, ObjectArrayList<LivingEntity>> byEntity = new HashMap<>();
            Map<String, ObjectArrayList<LivingEntity>> byMod = new HashMap<>();

            for (LivingEntity le : mobList) {
                String eid = EntityType.getKey(le.getType()).toString().toLowerCase();
                String ns = EntityType.getKey(le.getType()).getNamespace().toLowerCase();
                byEntity.computeIfAbsent(eid, k -> new ObjectArrayList<>()).add(le);
                byMod.computeIfAbsent(ns, k -> new ObjectArrayList<>()).add(le);
            }

            for (Map.Entry<String, ObjectArrayList<LivingEntity>> e : byEntity.entrySet()) {
                Integer lim = MLConfig.getEntityStrictClear().get(e.getKey());
                if (lim == null)
                    continue;
                ObjectArrayList<LivingEntity> list = e.getValue();
                if (list.size() > lim) {
                    for (int i = lim, s = list.size(); i < s; i++) {
                        list.get(i).discard();
                        removed++;
                    }
                }
            }

            for (Map.Entry<String, ObjectArrayList<LivingEntity>> e : byMod.entrySet()) {
                Integer lim = MLConfig.getModStrictClear().get(e.getKey());
                if (lim == null)
                    continue;
                ObjectArrayList<LivingEntity> list = e.getValue();
                int survivors = 0;
                for (int i = 0, s = list.size(); i < s; i++) {
                    if (survivors < lim) {
                        survivors++;
                    } else {
                        list.get(i).discard();
                        removed++;
                    }
                }
            }

            int global = MLConfig.getClearLimit();
            if (global >= 1) {
                int alive = 0;
                for (int i = 0, s = mobList.size(); i < s; i++) {
                    LivingEntity le = mobList.get(i);
                    if (le.isRemoved())
                        continue;
                    if (alive < global) {
                        alive++;
                    } else {
                        le.discard();
                        removed++;
                    }
                }
            }
        }

        for (ObjectArrayList<LivingEntity> list : COMBINED_MAP.values()) {
            list.clear();
        }

        return removed;
    }

    private static void sendClearMsg(ServerLevel level, int removed) {
        String path = level.dimension().location().getPath();
        String colored = Messages.text("clear.done", removed, path);
        var formatted = MLColor.parse(colored);
        String unformatted = colored.replaceAll("§.|&.", "");

        MinecraftServer server = level.getServer();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (MLConfig.isFeedbackEnabled(player.getUUID())) {
                player.sendSystemMessage(formatted);
            }
        }

        System.out.println(unformatted);
    }
}
