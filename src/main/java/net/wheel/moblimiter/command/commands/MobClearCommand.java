package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;

import net.wheel.moblimiter.command.util.CMD;
import net.wheel.moblimiter.config.MLConfig;
import net.wheel.moblimiter.util.Messages;

public final class MobClearCommand {
    public static void attachTo(LiteralArgumentBuilder<CommandSourceStack> root, String perm) {
        root.then(literal("mobclear")
                .then(literal("enable")
                        .requires(CMD.require(perm))
                        .executes(ctx -> {
                            MLConfig.enableMobClearing(true);
                            CMD.saveConfig();
                            return CMD.success(ctx, Messages.text("autoclear.enable"));
                        }))
                .then(literal("disable")
                        .requires(CMD.require(perm))
                        .executes(ctx -> {
                            MLConfig.enableMobClearing(false);
                            CMD.saveConfig();
                            return CMD.success(ctx, Messages.text("autoclear.disable"));
                        }))
                .then(literal("limit")
                        .requires(CMD.require(perm))
                        .then(argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                .executes(ctx -> {
                                    int v = IntegerArgumentType.getInteger(ctx, "value");
                                    MLConfig.setClearLimit(v);
                                    CMD.saveConfig();
                                    return CMD.success(ctx, Messages.text("mobclear.limit", v));
                                }))));
    }

}
