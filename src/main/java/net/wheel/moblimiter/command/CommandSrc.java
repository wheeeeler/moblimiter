package net.wheel.moblimiter.command;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;

import net.wheel.moblimiter.command.commands.*;

public final class CommandSrc {
    private static final String ROOT = "moblimiter";
    private static final String ALIAS = "ml";
    public static final String PERM_ADMIN = "moblimiter.admin";
    public static final String PERM_FEEDBACK = "moblimiter.feedback";

    public static void register(CommandDispatcher<CommandSourceStack> disp) {
        var full = literal(ROOT);
        var sAlias = literal(ALIAS);

        regCMDS(full, PERM_ADMIN);
        regCMDS(sAlias, PERM_ADMIN);

        disp.register(full);
        disp.register(sAlias);
    }

    private static void regCMDS(LiteralArgumentBuilder<CommandSourceStack> root, String perm) {
        UpdateCommand.regCMD(root, perm);
        StatusCommand.regCMD(root, perm);
        ForceClearCommand.regCMD(root, perm);
        ReloadCommand.regCMD(root, perm);
        ListEntitiesCommand.regCMD(root, perm);
        AuthorCommand.regCMD(root, perm);
        FeedbackCommand.regCMD(root);
        HelpCommand.regCMD(root);
    }
}
