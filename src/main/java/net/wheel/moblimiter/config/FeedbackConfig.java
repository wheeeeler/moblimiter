package net.wheel.moblimiter.config;

import java.io.File;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.wheel.api.config.Configurable;
import net.wheel.api.util.FileUtil;
import net.wheel.api.util.PathUtil;

public final class FeedbackConfig extends Configurable {
    public FeedbackConfig() {
        this(PathUtil.getBaseDir());
    }

    public FeedbackConfig(File configDir) {
        super(FileUtil.createJsonFile(configDir, "ml-feedback"));
    }

    @Override
    public void onLoad(JsonObject json) {
        super.onLoad(json);
        JsonObject obj = getJsonObject();
        if (obj == null)
            obj = new JsonObject();

        MLConfig.getFeedbackEnabledPlayers().clear();
        if (obj.has("enabled") && obj.get("enabled").isJsonArray()) {
            JsonArray arr = obj.getAsJsonArray("enabled");
            for (int i = 0; i < arr.size(); i++) {
                String s = arr.get(i).getAsString();
                if (s != null)
                    MLConfig.getFeedbackEnabledPlayers().add(s.toLowerCase());
            }
        }
    }

    @Override
    public void onSave() {
        JsonObject out = new JsonObject();
        JsonArray arr = new JsonArray();
        Set<String> set = MLConfig.getFeedbackEnabledPlayers();
        for (String id : set)
            arr.add(id.toLowerCase());
        out.add("enabled", arr);
        saveJsonObjectToFile(out);
    }
}
