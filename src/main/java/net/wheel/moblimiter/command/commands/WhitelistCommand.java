package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
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
                        .then(argument("entry", StringArgumentType.greedyString())
                                .suggests(WhitelistCommand::suggestEntries)
                                .executes(ctx -> {
                                    String raw = StringArgumentType.getString(ctx, "entry").toLowerCase(Locale.ROOT)
                                            .trim();
                                    String entry = resolveKey(raw);
                                    if (entry == null) {
                                        return CMD.success(ctx, Messages.text("whitelist.invalid", raw));
                                    }
                                    var list = MLConfig.getWhiteList();
                                    if (list.contains(entry)) {
                                        return CMD.success(ctx, Messages.text("whitelist.already", entry));
                                    }
                                    list.add(entry);
                                    CMD.saveConfig();
                                    return CMD.success(ctx, Messages.text("whitelist.added", entry));
                                })))
                .then(literal("remove")
                        .then(argument("entry", StringArgumentType.greedyString())
                                .suggests(WhitelistCommand::suggestEntries)
                                .executes(ctx -> {
                                    String raw = StringArgumentType.getString(ctx, "entry").toLowerCase(Locale.ROOT)
                                            .trim();
                                    String entry = resolveKeyForRemoval(raw);
                                    var list = MLConfig.getWhiteList();
                                    if (!list.remove(entry)) {
                                        return CMD.success(ctx, Messages.text("whitelist.missing", entry));
                                    }
                                    CMD.saveConfig();
                                    return CMD.success(ctx, Messages.text("whitelist.removed", entry));
                                })));
    }

    private static CompletableFuture<Suggestions> suggestEntries(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder builder) {
        Set<String> namespaces = new HashSet<>();
        Set<String> simpleNames = new HashSet<>();
        for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            builder.suggest(id.toString());
            namespaces.add(id.getNamespace());
            simpleNames.add(id.getPath());
        }
        for (String ns : namespaces)
            builder.suggest(ns);
        for (String p : simpleNames)
            builder.suggest(p);
        return builder.buildFuture();
    }

    private static String resolveKey(String input) {
        String s = input.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty())
            return null;

        if (s.indexOf(':') > 0) {
            ResourceLocation rl = ResourceLocation.tryParse(s);
            if (rl != null && BuiltInRegistries.ENTITY_TYPE.containsKey(rl))
                return rl.toString();
            String[] parts = s.split(":", 2);
            String swapped = parts[1] + ":" + parts[0];
            ResourceLocation rl2 = ResourceLocation.tryParse(swapped);
            if (rl2 != null && BuiltInRegistries.ENTITY_TYPE.containsKey(rl2))
                return rl2.toString();
            return null;
        } else {
            for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet())
                if (id.getNamespace().equalsIgnoreCase(s))
                    return s;
            ResourceLocation unique = null;
            for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
                if (id.getPath().equalsIgnoreCase(s)) {
                    if (unique != null)
                        return null;
                    unique = id;
                }
            }
            return unique != null ? unique.toString() : null;
        }
    }

    private static String resolveKeyForRemoval(String input) {
        String s = input.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty())
            return s;
        if (s.indexOf(':') > 0) {
            ResourceLocation rl = ResourceLocation.tryParse(s);
            return rl != null ? rl.toString() : s;
        }
        return s;
    }
}
