package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import net.wheel.moblimiter.command.util.CMD;

public final class AuthorCommand {
    private static final String NAME = "wheeler";
    private static final String URL = "https://github.com/wheeeeler";

    public static void regCMD(LiteralArgumentBuilder<CommandSourceStack> root, String perm) {
        root.then(literal("author")
                .requires(CMD.require(perm))
                .executes(ctx -> {
                    MutableComponent prefix = Component.literal("[")
                            .withStyle(ChatFormatting.WHITE)
                            .append(Component.literal("ML").withStyle(ChatFormatting.RED))
                            .append(Component.literal("] -> ").withStyle(ChatFormatting.WHITE));

                    MutableComponent name = Component.literal(NAME).withStyle(ChatFormatting.WHITE);

                    MutableComponent link = Component.literal(" " + URL)
                            .setStyle(Style.EMPTY
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL))
                                    .withUnderlined(true)
                                    .withColor(ChatFormatting.AQUA));

                    MutableComponent msg = prefix.append(name).append(link);

                    ctx.getSource().sendSuccess(() -> msg, false);
                    return 1;
                }));
    }
}
