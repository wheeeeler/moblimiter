package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;

import net.wheel.moblimiter.Moblim;
import net.wheel.moblimiter.command.util.CMD;
import net.wheel.moblimiter.util.Messages;

public final class ReloadCommand {
    public static void regCMD(LiteralArgumentBuilder<CommandSourceStack> root, String perm) {
        root.then(literal("reload")
                .requires(CMD.require(perm))
                .executes(ctx -> {
                    try {
                        Moblim.INSTANCE().getConfigManager().loadAll();
                        Messages.reload();
                        return CMD.success(ctx, Messages.text("reload.ok"));
                    } catch (Throwable t) {
                        t.printStackTrace();
                        return CMD.fail(ctx, Messages.text("reload.fail"));
                    }
                }));
    }
}
