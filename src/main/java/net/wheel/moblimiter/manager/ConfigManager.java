package net.wheel.moblimiter.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.wheel.api.config.Configurable;
import net.wheel.api.event.client.EventLoadConfig;
import net.wheel.api.event.client.EventSaveConfig;
import net.wheel.api.util.PathUtil;
import net.wheel.moblimiter.Moblim;
import net.wheel.moblimiter.config.FeedbackConfig;
import net.wheel.moblimiter.config.MobLimiterConfig;
import net.wheel.moblimiter.config.StrictLimitConfig;
import net.wheel.moblimiter.config.WhitelistConfig;

public final class ConfigManager {

    private File configDir;
    private boolean firstLaunch = false;
    private final List<Configurable> configurableList = new ArrayList<>();

    public ConfigManager() {
        generateDirectories();
        addConfigurable(new MobLimiterConfig(configDir));
        addConfigurable(new WhitelistConfig(configDir));
        addConfigurable(new StrictLimitConfig(configDir));
        addConfigurable(new FeedbackConfig(configDir));
    }

    private void generateDirectories() {
        this.configDir = PathUtil.getBaseDir();
        if (!this.configDir.exists()) {
            this.firstLaunch = true;
            this.configDir.mkdirs();
        }
    }

    public void init() {
        if (this.firstLaunch) {
            saveAll();
            Moblim.INSTANCE().getEventManager().dispatchEvent(new EventSaveConfig());
        } else {
            loadAll();
            Moblim.INSTANCE().getEventManager().dispatchEvent(new EventLoadConfig());
        }
    }

    public void save(Class configurableClassType) {
        for (Configurable cfg : configurableList) {
            if (cfg.getClass().isAssignableFrom(configurableClassType)) {
                cfg.onSave();
            }
        }
        Moblim.INSTANCE().getEventManager().dispatchEvent(new EventSaveConfig());
    }

    public void saveAll() {
        for (Configurable cfg : configurableList) {
            cfg.onSave();
        }
        Moblim.INSTANCE().getEventManager().dispatchEvent(new EventSaveConfig());
    }

    public void load(Class configurableClassType) {
        for (Configurable cfg : configurableList) {
            if (cfg.getClass().isAssignableFrom(configurableClassType)) {
                cfg.onLoad(null);
            }
        }
        Moblim.INSTANCE().getEventManager().dispatchEvent(new EventLoadConfig());
    }

    public void loadAll() {
        for (Configurable cfg : configurableList) {
            cfg.onLoad(null);
        }
        Moblim.INSTANCE().getEventManager().dispatchEvent(new EventLoadConfig());
    }

    public void addConfigurable(Configurable configurable) {
        this.configurableList.add(configurable);
    }

    public File getConfigDir() {
        return configDir;
    }
}
