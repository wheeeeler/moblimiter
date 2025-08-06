package net.wheel.moblimiter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.wheel.moblimiter.config.MLConfig;
import net.wheel.moblimiter.handler.MLClearHandler;
import net.wheel.moblimiter.util.MLColor;

public class MLCommands {
    private static final String ROOT = "moblimiter";
    private static final String PERM_UPDATE = "moblimiter.update";
    private static final String PERM_SET = "moblimiter.set";

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();

        d.register(Commands.literal(ROOT)
                .requires(cs -> hasPerm(cs.getPlayer(), PERM_UPDATE) || hasPerm(cs.getPlayer(), PERM_SET))

                .then(Commands.literal("moblimit")
                        .then(Commands.literal("enable")
                                .requires(cs -> hasPerm(cs.getPlayer(), PERM_UPDATE))
                                .executes(ctx -> send(ctx.getSource(), "&f[&cML&f] -> &aMob limiting enabled&r",
                                        MLConfig::enableMobLimiting, true)))
                        .then(Commands.literal("disable")
                                .requires(cs -> hasPerm(cs.getPlayer(), PERM_UPDATE))
                                .executes(ctx -> send(ctx.getSource(), "&f[&cML&f] -> &4Mob limiting disabled&r",
                                        MLConfig::enableMobLimiting, false))))

                .then(Commands.literal("mobclear")
                        .then(Commands.literal("enable")
                                .requires(cs -> hasPerm(cs.getPlayer(), PERM_UPDATE))
                                .executes(ctx -> send(ctx.getSource(), "&f[&cML&f] -> &aMob clearing enabled&r",
                                        MLConfig::enableMobClearing, true)))
                        .then(Commands.literal("disable")
                                .requires(cs -> hasPerm(cs.getPlayer(), PERM_UPDATE))
                                .executes(ctx -> send(ctx.getSource(), "&f[&cML&f] -> &4Mob clearing disabled&r",
                                        MLConfig::enableMobClearing, false))))

                .then(Commands.literal("set")
                        .then(Commands.literal("moblimit")
                                .requires(cs -> hasPerm(cs.getPlayer(), PERM_SET))
                                .then(Commands.argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(ctx -> {
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            MLConfig.setMobLimit(value);
                                            ctx.getSource().sendSuccess(
                                                    () -> MLColor.parse("&f[&cML&f] -> Mob limit set to &b" + value),
                                                    false);
                                            return 1;
                                        })))
                        .then(Commands.literal("mobclearinterval")
                                .requires(cs -> hasPerm(cs.getPlayer(), PERM_SET))
                                .then(Commands.argument("value", IntegerArgumentType.integer(1, 3600))
                                        .executes(ctx -> {
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            MLConfig.setClearTimer(value);
                                            ctx.getSource().sendSuccess(
                                                    () -> MLColor.parse("&f[&cML&f] -> Mob clear interval set to &b"
                                                            + value + "&f seconds"),
                                                    false);
                                            return 1;
                                        }))))

                .then(Commands.literal("status")
                        .requires(cs -> hasPerm(cs.getPlayer(), PERM_UPDATE))
                        .executes(ctx -> {
                            CommandSourceStack source = ctx.getSource();
                            source.sendSuccess(() -> MLColor.parse("&f[&cML&f] &7Mob Limiting: &b"
                                    + (MLConfig.isMobLimitingEnabled() ? "Enabled" : "Disabled")), false);
                            source.sendSuccess(() -> MLColor.parse("&f[&cML&f] &7Mob Clearing: &b"
                                    + (MLConfig.isMobClearingEnabled() ? "Enabled" : "Disabled")), false);
                            source.sendSuccess(
                                    () -> MLColor.parse("&f[&cML&f] &7Mob Limit: &b" + MLConfig.getMobLimit()), false);
                            source.sendSuccess(
                                    () -> MLColor
                                            .parse("&f[&cML&f] &7Clear Interval: &b" + MLConfig.getClearTimer() + "s"),
                                    false);
                            return 1;
                        }))

                .then(Commands.literal("forceclear")
                        .requires(cs -> hasPerm(cs.getPlayer(), PERM_UPDATE))
                        .executes(ctx -> {
                            for (ServerLevel level : ctx.getSource().getServer().getAllLevels()) {
                                MLClearHandler.forceClear(level);
                            }
                            ctx.getSource().sendSuccess(
                                    () -> MLColor.parse("&f[&cML&f] -> &aForcing mobcleanup"), false);
                            return 1;
                        }))

                .then(Commands.literal("whitelist")
                        .requires(cs -> hasPerm(cs.getPlayer(), PERM_UPDATE))

                        .then(Commands.literal("list")
                                .executes(ctx -> {
                                    var list = MLConfig.getWhiteList();
                                    if (list.isEmpty()) {
                                        ctx.getSource().sendSuccess(
                                                () -> MLColor.parse("&f[&cML&f] -> &7Whitelist is empty"),
                                                false);
                                    } else {
                                        ctx.getSource().sendSuccess(
                                                () -> MLColor.parse("&f[&cML&f] -> &aWhitelisted entries:"),
                                                false);
                                        for (String entry : list) {
                                            ctx.getSource().sendSuccess(
                                                    () -> MLColor.parse("&f[&cML&f]   - &e" + entry),
                                                    false);
                                        }
                                    }
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
