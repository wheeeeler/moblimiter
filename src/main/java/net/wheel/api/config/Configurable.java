package net.wheel.api.config;

import java.io.File;
import java.io.FileReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.wheel.api.util.FileUtil;

public abstract class Configurable {

    private final File file;

    private JsonObject jsonObject;

    public Configurable(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void onLoad(JsonObject jsonObject) {
        if (jsonObject != null) {
            this.jsonObject = jsonObject;
        } else {
            this.jsonObject = this.convertJsonObjectFromFile();
        }
    }

    public void onSave() {

    }

    public void saveJsonObjectToFile(JsonObject object) {
        FileUtil.saveJsonFile(FileUtil.recreateFile(this.getFile()), object);
    }

    public JsonObject convertJsonObjectFromFile() {
        if (!this.getFile().exists())
            return new JsonObject();

        FileReader reader = FileUtil.createReader(this.getFile());
        if (reader == null)
            return new JsonObject();

        JsonElement element = JsonParser.parseReader(reader);
        if (!element.isJsonObject())
            return new JsonObject();

        FileUtil.closeReader(reader);

        return element.getAsJsonObject();
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }
}
