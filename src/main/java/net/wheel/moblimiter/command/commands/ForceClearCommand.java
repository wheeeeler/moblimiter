package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

import net.wheel.moblimiter.command.util.CMD;
import net.wheel.moblimiter.handler.MLClearHandler;
import net.wheel.moblimiter.util.Messages;

public final class ForceClearCommand {
    public static void regCMD(LiteralArgumentBuilder<CommandSourceStack> root, String perm) {
        root.then(literal("forceclear")
                .requires(CMD.require(perm))
                .executes(ctx -> {
                    for (ServerLevel level : ctx.getSource().getServer().getAllLevels()) {
                        MLClearHandler.forceClear(level);
                    }
                    return CMD.success(ctx, Messages.text("forceclear.start"));
                }));
    }
}
