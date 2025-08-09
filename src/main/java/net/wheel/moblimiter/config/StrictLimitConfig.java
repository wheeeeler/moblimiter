package net.wheel.moblimiter.config;

import java.io.File;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wheel.api.config.Configurable;
import net.wheel.api.util.FileUtil;
import net.wheel.api.util.PathUtil;

public final class StrictLimitConfig extends Configurable {
    public StrictLimitConfig() {
        this(PathUtil.getBaseDir());
    }

    public StrictLimitConfig(File configDir) {
        super(FileUtil.createJsonFile(configDir, "ml-strict"));
    }

    @Override
    public void onLoad(JsonObject json) {
        super.onLoad(json);
        JsonObject obj = getJsonObject();
        if (obj == null)
            obj = new JsonObject();

        MLConfig.getEntityStrictSpawn().clear();
        MLConfig.getModStrictSpawn().clear();
        MLConfig.getEntityStrictClear().clear();
        MLConfig.getModStrictClear().clear();

        JsonObject entitylimit = obj.has("entitylimit") && obj.get("entitylimit").isJsonObject()
                ? obj.getAsJsonObject("entitylimit")
                : new JsonObject();
        JsonObject autoclear = obj.has("autoclear") && obj.get("autoclear").isJsonObject()
                ? obj.getAsJsonObject("autoclear")
                : new JsonObject();

        JsonObject elEntities = entitylimit.has("entities") && entitylimit.get("entities").isJsonObject()
                ? entitylimit.getAsJsonObject("entities")
                : new JsonObject();
        JsonObject elMods = entitylimit.has("mods") && entitylimit.get("mods").isJsonObject()
                ? entitylimit.getAsJsonObject("mods")
                : new JsonObject();
        JsonObject acEntities = autoclear.has("entities") && autoclear.get("entities").isJsonObject()
                ? autoclear.getAsJsonObject("entities")
                : new JsonObject();
        JsonObject acMods = autoclear.has("mods") && autoclear.get("mods").isJsonObject()
                ? autoclear.getAsJsonObject("mods")
                : new JsonObject();

        for (Map.Entry<String, JsonElement> e : elEntities.entrySet()) {
            if (e.getValue().isJsonPrimitive())
                MLConfig.getEntityStrictSpawn().put(e.getKey().toLowerCase(), e.getValue().getAsInt());
        }
        for (Map.Entry<String, JsonElement> e : elMods.entrySet()) {
            if (e.getValue().isJsonPrimitive())
                MLConfig.getModStrictSpawn().put(e.getKey().toLowerCase(), e.getValue().getAsInt());
        }
        for (Map.Entry<String, JsonElement> e : acEntities.entrySet()) {
            if (e.getValue().isJsonPrimitive())
                MLConfig.getEntityStrictClear().put(e.getKey().toLowerCase(), e.getValue().getAsInt());
        }
        for (Map.Entry<String, JsonElement> e : acMods.entrySet()) {
            if (e.getValue().isJsonPrimitive())
                MLConfig.getModStrictClear().put(e.getKey().toLowerCase(), e.getValue().getAsInt());
        }
    }

    @Override
    public void onSave() {
        JsonObject out = new JsonObject();

        JsonObject entitylimit = new JsonObject();
        JsonObject elEntities = new JsonObject();
        JsonObject elMods = new JsonObject();
        for (Map.Entry<String, Integer> e : MLConfig.getEntityStrictSpawn().entrySet())
            elEntities.addProperty(e.getKey(), e.getValue());
        for (Map.Entry<String, Integer> e : MLConfig.getModStrictSpawn().entrySet())
            elMods.addProperty(e.getKey(), e.getValue());
        entitylimit.add("entities", elEntities);
        entitylimit.add("mods", elMods);

        JsonObject autoclear = new JsonObject();
        JsonObject acEntities = new JsonObject();
        JsonObject acMods = new JsonObject();
        for (Map.Entry<String, Integer> e : MLConfig.getEntityStrictClear().entrySet())
            acEntities.addProperty(e.getKey(), e.getValue());
        for (Map.Entry<String, Integer> e : MLConfig.getModStrictClear().entrySet())
            acMods.addProperty(e.getKey(), e.getValue());
        autoclear.add("entities", acEntities);
        autoclear.add("mods", acMods);

        out.add("entitylimit", entitylimit);
        out.add("autoclear", autoclear);

        saveJsonObjectToFile(out);
    }
}
