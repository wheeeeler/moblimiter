package net.wheel.moblimiter.handler;

import java.util.Comparator;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.wheel.moblimiter.config.MLConfig;
import net.wheel.moblimiter.util.MLColor;

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
        final int limit = MLConfig.getMobLimit();
        final Object2ObjectOpenHashMap<ChunkPos, ObjectArrayList<LivingEntity>> chunkMobMap = new Object2ObjectOpenHashMap<>(
                256);
        int removed = 0;

        for (Entity entity : level.getEntities().getAll()) {
            if (!(entity instanceof LivingEntity living) || entity instanceof Player)
                continue;

            chunkMobMap
                    .computeIfAbsent(living.chunkPosition(), k -> new ObjectArrayList<>(CHUNK_INIT_CAP))
                    .add(living);
        }

        for (ObjectArrayList<LivingEntity> mobList : chunkMobMap.values()) {
            if (mobList.size() <= limit)
                continue;

            mobList.sort(Y_SORT_COMP);
            for (int i = limit, s = mobList.size(); i < s; i++) {
                mobList.get(i).discard();
                removed++;
            }
        }

        if (removed > 0) {
            sendClearMsg(level, removed);
        }
    }

    private static int clearLevel(ServerLevel level) {
        final int limit = MLConfig.getMobLimit();
        int removed = 0;
        COMBINED_MAP.clear();

        for (Entity entity : level.getEntities().getAll()) {
            if (!(entity instanceof LivingEntity living) || entity instanceof Player)
                continue;

            COMBINED_MAP
                    .computeIfAbsent(living.chunkPosition(), k -> new ObjectArrayList<>(CHUNK_INIT_CAP))
                    .add(living);
        }

        for (ObjectArrayList<LivingEntity> mobList : COMBINED_MAP.values()) {
            if (mobList.size() <= limit)
                continue;

            mobList.sort(Y_SORT_COMP);
            for (int i = limit, s = mobList.size(); i < s; i++) {
                mobList.get(i).discard();
                removed++;
            }
        }

        for (ObjectArrayList<LivingEntity> list : COMBINED_MAP.values()) {
            list.clear();
        }

        return removed;
    }

    private static void sendClearMsg(ServerLevel level, int removed) {
        String path = level.dimension().location().getPath();
        String colored = "&f[&cML&f] -> &6Cleared &b" + removed + "&6 mobs in &e" + path;
        var formatted = MLColor.parse(colored);
        String plain = colored.replaceAll("§.|&.", "");

        MinecraftServer server = level.getServer();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.hasPermissions(4)) {
                player.sendSystemMessage(formatted);
            }
        }

        System.out.println(plain);
    }

}
