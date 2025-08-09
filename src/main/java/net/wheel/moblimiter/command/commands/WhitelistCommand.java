package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import net.wheel.moblimiter.command.util.CMD;
import net.wheel.moblimiter.config.MLConfig;
import net.wheel.moblimiter.util.MLColor;
import net.wheel.moblimiter.util.Messages;

public final class WhitelistCommand {
    public static void attachTo(LiteralArgumentBuilder<CommandSourceStack> root, String perm) {
        root.then(node(perm));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> node(String perm) {
        return literal("whitelist")
                .requires(CMD.require(perm))
                .then(literal("list")
                        .executes(ctx -> {
                            var list = MLConfig.getWhiteList();
                            if (list.isEmpty()) {
                                return CMD.success(ctx, Messages.text("whitelist.empty"));
                            }
                            ctx.getSource().sendSuccess(() -> MLColor.parse(Messages.text("whitelist.header")), false);
                            for (String e : list) {
                                ctx.getSource().sendSuccess(() -> MLColor.parse(Messages.text("whitelist.line", e)),
                                        false);
                            }
                            return 1;
                        }))
                .then(literal("add")
                        .then(argument("entry", ResourceLocationArgument.id())
                                .suggests(WhitelistCommand::suggestEntities)
                                .executes(ctx -> {
                                    ResourceLocation rl = ResourceLocationArgument.getId(ctx, "entry");
                                    String entry = rl.toString().toLowerCase(Locale.ROOT);
                                    var list = MLConfig.getWhiteList();
                                    if (list.contains(entry)) {
                                        return CMD.success(ctx, Messages.text("whitelist.already", entry));
                                    }
                                    list.add(entry);
                                    CMD.saveConfig();
                                    return CMD.success(ctx, Messages.text("whitelist.added", entry));
                                })))
                .then(literal("remove")
                        .then(argument("entry", ResourceLocationArgument.id())
                                .suggests(WhitelistCommand::suggestEntities)
                                .executes(ctx -> {
                                    ResourceLocation rl = ResourceLocationArgument.getId(ctx, "entry");
                                    String entry = rl.toString().toLowerCase(Locale.ROOT);
                                    var list = MLConfig.getWhiteList();
                                    if (!list.remove(entry)) {
                                        return CMD.success(ctx, Messages.text("whitelist.missing", entry));
                                    }
                                    CMD.saveConfig();
                                    return CMD.success(ctx, Messages.text("whitelist.removed", entry));
                                })));
    }

    private static CompletableFuture<Suggestions> suggestEntities(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder builder) {
        for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            builder.suggest(id.toString());
        }
        return builder.buildFuture();
    }
}
