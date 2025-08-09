package net.wheel.moblimiter;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import net.wheel.api.event.client.EventLoad;
import net.wheel.api.event.client.EventUnload;
import net.wheel.moblimiter.command.CommandReg;
import net.wheel.moblimiter.handler.MLClearHandler;
import net.wheel.moblimiter.handler.MLHandler;
import net.wheel.moblimiter.manager.ConfigManager;
import net.wheel.moblimiter.util.MLInit;
import net.wheel.moblimiter.util.Messages;

import handl.interactor.voodoo.EventManager;
import handl.interactor.voodoo.impl.annotated.AnnotatedEventManager;

@Mod(Moblim.MODID)
public final class Moblim {

    private static Moblim INSTANCE;

    public static final String MODID = "moblim";
    public static final String VERSION = "0.4";

    private EventManager eventManager;
    private ConfigManager configManager;

    @SuppressWarnings("unused")
    public Moblim(ModContainer modContainer) {
        INSTANCE = this;

        NeoForge.EVENT_BUS.register(new MLHandler());
        NeoForge.EVENT_BUS.register(new CommandReg());
        NeoForge.EVENT_BUS.register(new MLClearHandler());
        this.eventManager = new AnnotatedEventManager();

        this.configManager = new ConfigManager();
        this.configManager.init();

        Messages.init();

        eventManager.dispatchEvent(new EventLoad());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                configManager.saveAll();
            } catch (Throwable ignored) {
            }
            eventManager.dispatchEvent(new EventUnload());
        }));

        MLInit.MINT(VERSION);
    }

    public static Moblim INSTANCE() {
        return INSTANCE;
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }
}
