package net.wheel.moblimiter.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MLConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue MOB_LIMITING_ENABLED = defineBool("mobLimitingEnabled",
            "Enable or disable mob limiting on spawn");
    public static final ModConfigSpec.BooleanValue MOB_CLEARING_ENABLED = defineBool("mobClearingEnabled",
            "Enable or disable background mob clearing task");

    public static final ModConfigSpec.IntValue MOB_LIMIT = defineInt("mobLimit", 10,
            "Max mobs allowed in a chunk");
    public static final ModConfigSpec.IntValue CLEAR_INTERVAL = defineInt("clearInterval", 600,
            "Scan interval in seconds for clearing excess mobs in loaded chunks");

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean isMobLimitingEnabled() {
        return MOB_LIMITING_ENABLED.get();
    }

    public static boolean isMobClearingEnabled() {
        return MOB_CLEARING_ENABLED.get();
    }

    public static int getMobLimit() {
        return MOB_LIMIT.get();
    }

    public static int getClearTimer() {
        return CLEAR_INTERVAL.get();
    }

    public static void enableMobLimiting(boolean value) {
        MOB_LIMITING_ENABLED.set(value);
    }

    public static void enableMobClearing(boolean value) {
        MOB_CLEARING_ENABLED.set(value);
    }

    public static void setMobLimit(int value) {
        MOB_LIMIT.set(value);
    }

    public static void setClearTimer(int value) {
        CLEAR_INTERVAL.set(value);
    }

    private static ModConfigSpec.BooleanValue defineBool(String name, String comment) {
        return BUILDER.comment(comment).define(name, true);
    }

    private static ModConfigSpec.IntValue defineInt(String name, int defaultValue, String comment) {
        return BUILDER.comment(comment).defineInRange(name, defaultValue, 1, Integer.MAX_VALUE);
    }
}
