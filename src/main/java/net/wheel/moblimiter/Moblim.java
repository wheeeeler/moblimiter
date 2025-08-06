package net.wheel.moblimiter;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

import net.wheel.moblimiter.command.MLCommands;
import net.wheel.moblimiter.config.MLConfig;
import net.wheel.moblimiter.handler.MLClearHandler;
import net.wheel.moblimiter.handler.MLHandler;

@Mod(Moblim.MODID)
public class Moblim {
    public static final String MODID = "moblim";
    public static final String VERSION = "0.2";

    public Moblim(ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(new MLHandler());
        NeoForge.EVENT_BUS.register(new MLCommands());
        NeoForge.EVENT_BUS.register(new MLClearHandler());

        modContainer.registerConfig(ModConfig.Type.COMMON, MLConfig.SPEC);
        MINT();
    }

    private void MINT() {
        System.out.println(
                "\n" +
                        "  _____ ______   ___          \n" +
                        "|\\   _ \\  _   \\|\\  \\         \n" +
                        "\\ \\  \\\\\\__\\ \\  \\ \\  \\        \n" +
                        " \\ \\  \\\\|__| \\  \\ \\  \\       \n" +
                        "  \\ \\  \\    \\ \\  \\ \\  \\____  \n" +
                        "   \\ \\__\\    \\ \\__\\ \\_______\\\n" +
                        "    \\|__|     \\|__|\\|_______|\n" +
                        "\nInitializing MobLimit v" + VERSION + "\n");
    }
}
