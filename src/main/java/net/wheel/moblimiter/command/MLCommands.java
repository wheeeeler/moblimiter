package net.wheel.moblimiter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.wheel.moblimiter.config.MLConfig;
import net.wheel.moblimiter.util.MLColor;

public class MLCommands {
    private static final String ML_CMD = "moblimiter";
    private static final String ML_VALUE = "value";
    private static final String ML_TOGGLE = "moblimiter.toggle";
    private static final String ML_SET = "moblimiter.set";

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();

        d.register(Commands.literal(ML_CMD)
                .requires(cs -> hasPerm(cs.getPlayer(), ML_TOGGLE) || hasPerm(cs.getPlayer(), ML_SET))

                .then(Commands.literal("enable")
                        .requires(cs -> hasPerm(cs.getPlayer(), ML_TOGGLE))
                        .executes(
                                ctx -> send(ctx.getSource(), "&f[&cML&f] -> &aEnabled&r", MLConfig::setEnabled, true)))

                .then(Commands.literal("disable")
                        .requires(cs -> hasPerm(cs.getPlayer(), ML_TOGGLE))
                        .executes(ctx -> send(ctx.getSource(), "&f[&cML&f] -> &4Disabled&r", MLConfig::setEnabled,
                                false)))

                .then(Commands.literal("setlimit")
                        .requires(cs -> hasPerm(cs.getPlayer(), ML_SET))
                        .then(Commands.argument(ML_VALUE, IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                .executes(ctx -> {
                                    int value = IntegerArgumentType.getInteger(ctx, ML_VALUE);
                                    MLConfig.setMobLimit(value);
                                    ctx.getSource().sendSuccess(
                                            () -> MLColor.parse("&f[&cML&f] -> Mob limit set to &b" + value), false);
                                    return 1;
                                }))));
    }

    private boolean hasPerm(ServerPlayer p, String node) {
        if (p == null)
            return true;
        Player bukkit = Bukkit.getPlayerExact(p.getGameProfile().getName());
        return (bukkit != null && Bukkit.getPluginManager().isPluginEnabled("LuckPerms"))
                ? bukkit.hasPermission(node)
                : p.hasPermissions(4);
    }

    private int send(CommandSourceStack source, String msg, java.util.function.Consumer<Boolean> setter,
            boolean value) {
        setter.accept(value);
        source.sendSuccess(() -> MLColor.parse(msg), false);
        return 1;
    }
}
