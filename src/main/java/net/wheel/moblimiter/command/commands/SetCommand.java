package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;

import net.wheel.moblimiter.command.util.CMD;
import net.wheel.moblimiter.config.MLConfig;

public final class SetCommand {
    public static void attachTo(LiteralArgumentBuilder<CommandSourceStack> root, String perm) {
        root.then(literal("set")
                .then(literal("moblimit")
                        .requires(CMD.require(perm))
                        .then(argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                .executes(ctx -> {
                                    int v = IntegerArgumentType.getInteger(ctx, "value");
                                    MLConfig.setMobLimit(v);
                                    CMD.saveConfig();
                                    return CMD.success(ctx, "&f[&cML&f] -> Mob limit set to &b" + v);
                                })))
                .then(literal("mobclearinterval")
                        .requires(CMD.require(perm))
                        .then(argument("value", IntegerArgumentType.integer(1, 3600))
                                .executes(ctx -> {
                                    int v = IntegerArgumentType.getInteger(ctx, "value");
                                    MLConfig.setClearTimer(v);
                                    CMD.saveConfig();
                                    return CMD.success(ctx,
                                            "&f[&cML&f] -> Mob clear interval set to &b" + v + "&f seconds");
                                }))));
    }
}
