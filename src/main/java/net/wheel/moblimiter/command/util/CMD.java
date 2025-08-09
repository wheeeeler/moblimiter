package net.wheel.moblimiter.command.util;

import java.util.function.Predicate;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;

import net.wheel.moblimiter.Moblim;
import net.wheel.moblimiter.util.MLColor;

public final class CMD {
    private CMD() {
    }

    public static Predicate<CommandSourceStack> require(String permNode) {
        return cs -> CommandPerms.has(cs, permNode);
    }

    public static int callback(CommandContext<CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendSuccess(() -> MLColor.parse(msg), false);
        return 1;
    }

    public static int success(CommandContext<CommandSourceStack> ctx, String colored) {
        return callback(ctx, colored);
    }

    public static int fail(CommandContext<CommandSourceStack> ctx, String colored) {
        return callback(ctx, colored);
    }

    public static void saveConfig() {
        Moblim.INSTANCE().getConfigManager().saveAll();
    }
}
