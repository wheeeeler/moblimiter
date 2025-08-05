package net.wheel.moblimiter.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MLConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MOB_LIMIT = BUILDER
            .comment("Max mobs allowed in a chunk")
            .defineInRange("mobLimit", 10, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Enable or disable mob limiting")
            .define("enabled", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int getMobLimit() {
        return MOB_LIMIT.get();
    }

    public static boolean isEnabled() {
        return ENABLED.get();
    }

    public static void setMobLimit(int value) {
        MOB_LIMIT.set(value);
    }

    public static void setEnabled(boolean value) {
        ENABLED.set(value);
    }
}
