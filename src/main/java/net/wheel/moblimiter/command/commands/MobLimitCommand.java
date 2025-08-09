package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;

import net.wheel.moblimiter.command.util.CMD;
import net.wheel.moblimiter.config.MLConfig;

public final class MobLimitCommand {
    public static void attachTo(LiteralArgumentBuilder<CommandSourceStack> root, String perm) {
        root.then(literal("moblimit")
                .then(literal("enable")
                        .requires(CMD.require(perm))
                        .executes(ctx -> {
                            MLConfig.enableMobLimiting(true);
                            CMD.saveConfig();
                            return CMD.success(ctx, "&f[&cML&f] -> &aMob limiting enabled&r");
                        }))
                .then(literal("disable")
                        .requires(CMD.require(perm))
                        .executes(ctx -> {
                            MLConfig.enableMobLimiting(false);
                            CMD.saveConfig();
                            return CMD.success(ctx, "&f[&cML&f] -> &4Mob limiting disabled&r");
                        })));
    }
}
