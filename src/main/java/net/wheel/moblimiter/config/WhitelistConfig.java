package net.wheel.moblimiter.config;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.wheel.api.config.Configurable;
import net.wheel.api.util.FileUtil;
import net.wheel.api.util.PathUtil;

public final class WhitelistConfig extends Configurable {

    public WhitelistConfig() {
        this(PathUtil.getBaseDir());
    }

    public WhitelistConfig(File configDir) {
        super(FileUtil.createJsonFile(configDir, "ml-whitelist"));
    }

    @Override
    public void onLoad(JsonObject json) {
        super.onLoad(json);
        JsonObject obj = getJsonObject();
        if (obj == null)
            obj = new JsonObject();

        MLConfig.getWhiteList().clear();
        if (obj.has("whitelist") && obj.get("whitelist").isJsonArray()) {
            JsonArray arr = obj.getAsJsonArray("whitelist");
            for (int i = 0; i < arr.size(); i++) {
                String s = arr.get(i).getAsString();
                if (s != null)
                    MLConfig.getWhiteList().add(s.toLowerCase());
            }
        }
    }

    @Override
    public void onSave() {
        JsonObject out = new JsonObject();
        JsonArray arr = new JsonArray();
        for (String w : MLConfig.getWhiteList())
            arr.add(w.toLowerCase());
        out.add("whitelist", arr);
        saveJsonObjectToFile(out);
    }
}
