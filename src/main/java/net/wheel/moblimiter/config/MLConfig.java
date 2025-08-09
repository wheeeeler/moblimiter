package net.wheel.moblimiter.config;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public final class MLConfig {
    private static boolean mobLimitingEnabled = true;
    private static boolean mobClearingEnabled = true;
    private static int mobLimit = 10;
    private static int clearLimit = 10;
    private static int clearTimer = 300;
    private static final LinkedHashSet<String> whitelist = new LinkedHashSet<>();

    private static final LinkedHashMap<String, Integer> entityStrictSpawn = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Integer> modStrictSpawn = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Integer> entityStrictClear = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Integer> modStrictClear = new LinkedHashMap<>();

    private static final LinkedHashSet<String> feedbackEnabledPlayers = new LinkedHashSet<>();

    public static boolean isMobLimitingEnabled() {
        return mobLimitingEnabled;
    }

    public static boolean isMobClearingEnabled() {
        return mobClearingEnabled;
    }

    public static int getMobLimit() {
        return mobLimit;
    }

    public static int getClearLimit() {
        return clearLimit;
    }

    public static int getClearTimer() {
        return clearTimer;
    }

    public static void enableMobLimiting(boolean v) {
        mobLimitingEnabled = v;
    }

    public static void enableMobClearing(boolean v) {
        mobClearingEnabled = v;
    }

    public static void setMobLimit(int v) {
        mobLimit = v;
    }

    public static void setClearLimit(int v) {
        clearLimit = v;
    }

    public static void setClearTimer(int v) {
        clearTimer = v;
    }

    public static Set<String> getWhiteList() {
        return whitelist;
    }

    public static Map<String, Integer> getEntityStrictSpawn() {
        return entityStrictSpawn;
    }

    public static Map<String, Integer> getModStrictSpawn() {
        return modStrictSpawn;
    }

    public static Map<String, Integer> getEntityStrictClear() {
        return entityStrictClear;
    }

    public static Map<String, Integer> getModStrictClear() {
        return modStrictClear;
    }

    public static int effectiveSpawnLimit(EntityType<?> type) {
        ResourceLocation id = EntityType.getKey(type);
        Integer byEntity = entityStrictSpawn.get(id.toString().toLowerCase());
        if (byEntity != null)
            return byEntity;
        Integer byMod = modStrictSpawn.get(id.getNamespace().toLowerCase());
        if (byMod != null)
            return byMod;
        return mobLimit;
    }

    public static int effectiveClearLimit(EntityType<?> type) {
        ResourceLocation id = EntityType.getKey(type);
        Integer byEntity = entityStrictClear.get(id.toString().toLowerCase());
        if (byEntity != null)
            return byEntity;
        Integer byMod = modStrictClear.get(id.getNamespace().toLowerCase());
        if (byMod != null)
            return byMod;
        return clearLimit;
    }

    public static Set<String> getFeedbackEnabledPlayers() {
        return feedbackEnabledPlayers;
    }

    public static boolean isFeedbackEnabled(UUID uuid) {
        return feedbackEnabledPlayers.contains(uuid.toString().toLowerCase());
    }

    public static void setFeedback(UUID uuid, boolean enabled) {
        String key = uuid.toString().toLowerCase();
        if (enabled) {
            feedbackEnabledPlayers.add(key);
        } else {
            feedbackEnabledPlayers.remove(key);
        }
    }
}
