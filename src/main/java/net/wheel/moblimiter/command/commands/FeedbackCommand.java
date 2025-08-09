package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import net.wheel.moblimiter.command.CommandSrc;
import net.wheel.moblimiter.command.util.CMD;
import net.wheel.moblimiter.config.MLConfig;
import net.wheel.moblimiter.util.Messages;

public final class FeedbackCommand {
    public static void regCMD(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(literal("feedback")
                .requires(CMD.require(CommandSrc.PERM_FEEDBACK))
                .then(literal("enable")
                        .executes(ctx -> {
                            if (!(ctx.getSource().getEntity() instanceof ServerPlayer sp))
                                return CMD.fail(ctx, Messages.text("feedback.playeronly"));
                            MLConfig.setFeedback(sp.getUUID(), true);
                            CMD.saveConfig();
                            return CMD.success(ctx, Messages.text("feedback.enabled"));
                        }))
                .then(literal("disable")
                        .executes(ctx -> {
                            if (!(ctx.getSource().getEntity() instanceof ServerPlayer sp))
                                return CMD.fail(ctx, Messages.text("feedback.playeronly"));
                            MLConfig.setFeedback(sp.getUUID(), false);
                            CMD.saveConfig();
                            return CMD.success(ctx, Messages.text("feedback.disabled"));
                        })));
    }
}
