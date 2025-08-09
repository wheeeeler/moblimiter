package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;

import net.wheel.moblimiter.command.util.CMD;
import net.wheel.moblimiter.config.MLConfig;
import net.wheel.moblimiter.util.MLColor;
import net.wheel.moblimiter.util.Messages;

public final class StatusCommand {
    public static void regCMD(LiteralArgumentBuilder<CommandSourceStack> root, String perm) {
        root.then(literal("status")
                .requires(CMD.require(perm))
                .executes(ctx -> {
                    var src = ctx.getSource();
                    String enabled = Messages.text("status.enabled");
                    String disabled = Messages.text("status.disabled");
                    src.sendSuccess(() -> MLColor.parse(Messages.text("status.moblimiting",
                            MLConfig.isMobLimitingEnabled() ? enabled : disabled)), false);
                    src.sendSuccess(() -> MLColor.parse(Messages.text("status.mobclearing",
                            MLConfig.isMobClearingEnabled() ? enabled : disabled)), false);
                    src.sendSuccess(() -> MLColor.parse(Messages.text("status.moblimit",
                            MLConfig.getMobLimit())), false);
                    src.sendSuccess(() -> MLColor.parse(Messages.text("status.clearlimit",
                            MLConfig.getClearLimit())), false);
                    src.sendSuccess(() -> MLColor.parse(Messages.text("status.clearinterval",
                            MLConfig.getClearTimer())), false);
                    return 1;
                }));
    }
}
