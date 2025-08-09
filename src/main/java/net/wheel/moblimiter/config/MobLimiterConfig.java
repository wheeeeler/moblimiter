package net.wheel.moblimiter.config;

import java.io.File;

import com.google.gson.JsonObject;

import net.wheel.api.config.Configurable;
import net.wheel.api.util.FileUtil;
import net.wheel.api.util.PathUtil;

public final class MobLimiterConfig extends Configurable {

    public MobLimiterConfig() {
        this(PathUtil.getBaseDir());
    }

    public MobLimiterConfig(File configDir) {
        super(FileUtil.createJsonFile(configDir, "ml-config"));
    }

    @Override
    public void onLoad(JsonObject json) {
        super.onLoad(json);
        JsonObject obj = getJsonObject();
        if (obj == null)
            obj = new JsonObject();

        boolean mobLimitingEnabled = obj.has("moblimiter.enabled") && obj.get("moblimiter.enabled").isJsonPrimitive()
                ? obj.get("moblimiter.enabled").getAsBoolean()
                : MLConfig.isMobLimitingEnabled();
        boolean mobClearingEnabled = obj.has("mobclear.enabled") && obj.get("mobclear.enabled").isJsonPrimitive()
                ? obj.get("mobclear.enabled").getAsBoolean()
                : MLConfig.isMobClearingEnabled();
        int mobLimit = obj.has("moblimiter.limit") && obj.get("moblimiter.limit").isJsonPrimitive()
                ? obj.get("moblimiter.limit").getAsInt()
                : MLConfig.getMobLimit();
        int clearLimit = obj.has("mobclear.limit") && obj.get("mobclear.limit").isJsonPrimitive()
                ? obj.get("mobclear.limit").getAsInt()
                : MLConfig.getClearLimit();
        int clearTimerSeconds = obj.has("mobclear.interval_seconds")
                && obj.get("mobclear.interval_seconds").isJsonPrimitive()
                        ? obj.get("mobclear.interval_seconds").getAsInt()
                        : MLConfig.getClearTimer();

        MLConfig.enableMobLimiting(mobLimitingEnabled);
        MLConfig.enableMobClearing(mobClearingEnabled);
        MLConfig.setMobLimit(mobLimit);
        MLConfig.setClearLimit(clearLimit);
        MLConfig.setClearTimer(clearTimerSeconds);
    }

    @Override
    public void onSave() {
        JsonObject out = new JsonObject();
        out.addProperty("moblimiter.enabled", MLConfig.isMobLimitingEnabled());
        out.addProperty("mobclear.enabled", MLConfig.isMobClearingEnabled());
        out.addProperty("moblimiter.limit", MLConfig.getMobLimit());
        out.addProperty("mobclear.limit", MLConfig.getClearLimit());
        out.addProperty("mobclear.interval_seconds", MLConfig.getClearTimer());
        saveJsonObjectToFile(out);
    }
}
