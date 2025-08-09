package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.wheel.moblimiter.util.Messages;

public final class UpdateCommand {
    public static void regCMD(LiteralArgumentBuilder<CommandSourceStack> root, String perm) {
        var update = literal("update");

        var autoclear = literal("autoclear")
                .then(literal("enable")
                        .requires(CMD.require(perm))
                        .executes(ctx -> {
                            MLConfig.enableMobClearing(true);
                            CMD.saveConfig();
                            return CMD.success(ctx, Messages.text("autoclear.enable"));
                        }))
                .then(literal("disable")
                        .requires(CMD.require(perm))
                        .executes(ctx -> {
                            MLConfig.enableMobClearing(false);
                            CMD.saveConfig();
                            return CMD.success(ctx, Messages.text("autoclear.disable"));
                        }))
                .then(literal("timer")
                        .requires(CMD.require(perm))
                        .then(argument("value", IntegerArgumentType.integer(1, 3600))
                                .executes(ctx -> {
                                    int v = IntegerArgumentType.getInteger(ctx, "value");
                                    MLConfig.setClearTimer(v);
                                    CMD.saveConfig();
                                    return CMD.success(ctx, Messages.text("autoclear.timer", v));
                                })))
                .then(literal("limit")
                        .requires(CMD.require(perm))
                        .then(argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                .suggests(UpdateCommand::suggestCurrentClearLimit)
                                .executes(ctx -> {
                                    int v = IntegerArgumentType.getInteger(ctx, "value");
                                    MLConfig.setClearLimit(v);
                                    CMD.saveConfig();
                                    return CMD.success(ctx, Messages.text("autoclear.limit", v));
                                })))
                .then(literal("strictlimit")
                        .requires(CMD.require(perm))
                        .then(argument("entry", ResourceLocationArgument.id())
                                .suggests(UpdateCommand::suggestStrictKeys)
                                .then(argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(ctx -> {
                                            ResourceLocation rl = ResourceLocationArgument.getId(ctx, "entry");
                                            int v = IntegerArgumentType.getInteger(ctx, "value");
                                            String resolved = resolveStrictKey(rl.toString());
                                            if (resolved == null) {
                                                return CMD.fail(ctx, Messages.text("strict.invalid", rl.toString()));
                                            }
                                            if (resolved.indexOf(':') > 0) {
                                                MLConfig.getEntityStrictClear().put(resolved, v);
                                            } else {
                                                MLConfig.getModStrictClear().put(resolved, v);
                                            }
                                            CMD.saveConfig();
                                            return CMD.success(ctx, Messages.text("strict.autoclear.set", resolved, v));
                                        }))));

        var entitylimit = literal("entitylimit")
                .then(literal("enable")
                        .requires(CMD.require(perm))
                        .executes(ctx -> {
                            MLConfig.enableMobLimiting(true);
                            CMD.saveConfig();
                            return CMD.success(ctx, Messages.text("entitylimit.enable"));
                        }))
                .then(literal("disable")
                        .requires(CMD.require(perm))
                        .executes(ctx -> {
                            MLConfig.enableMobLimiting(false);
                            CMD.saveConfig();
                            return CMD.success(ctx, Messages.text("entitylimit.disable"));
                        }))
                .then(literal("limit")
                        .requires(CMD.require(perm))
                        .then(argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                .executes(ctx -> {
                                    int v = IntegerArgumentType.getInteger(ctx, "value");
                                    MLConfig.setMobLimit(v);
                                    CMD.saveConfig();
                                    return CMD.success(ctx, Messages.text("entitylimit.limit", v));
                                })))
                .then(literal("strictlimit")
                        .requires(CMD.require(perm))
                        .then(argument("entry", ResourceLocationArgument.id())
                                .suggests(UpdateCommand::suggestStrictKeys)
                                .then(argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(ctx -> {
                                            ResourceLocation rl = ResourceLocationArgument.getId(ctx, "entry");
                                            int v = IntegerArgumentType.getInteger(ctx, "value");
                                            String resolved = resolveStrictKey(rl.toString());
                                            if (resolved == null) {
                                                return CMD.fail(ctx, Messages.text("strict.invalid", rl.toString()));
                                            }
                                            if (resolved.indexOf(':') > 0) {
                                                MLConfig.getEntityStrictSpawn().put(resolved, v);
                                            } else {
                                                MLConfig.getModStrictSpawn().put(resolved, v);
                                            }
                                            CMD.saveConfig();
                                            return CMD.success(ctx,
                                                    Messages.text("strict.entitylimit.set", resolved, v));
                                        }))));

        update.then(autoclear);
        update.then(entitylimit);
        update.then(net.wheel.moblimiter.command.commands.WhitelistCommand.node(perm));

        root.then(update);
    }

    private static CompletableFuture<Suggestions> suggestCurrentClearLimit(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder b) {
        b.suggest(Integer.toString(MLConfig.getClearLimit()));
        return b.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestStrictKeys(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder b) {
        Set<String> namespaces = new HashSet<>();
        for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            b.suggest(id.toString());
            namespaces.add(id.getNamespace());
        }
        for (String ns : namespaces)
            b.suggest(ns);
        return b.buildFuture();
    }

    private static String resolveStrictKey(String input) {
        String s = input.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty())
            return null;

        if (s.indexOf(':') > 0) {
            ResourceLocation rl = ResourceLocation.tryParse(s);
            if (rl != null && BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) {
                return rl.toString();
            }
            String[] parts = s.split(":", 2);
            String swapped = parts[1] + ":" + parts[0];
            ResourceLocation rl2 = ResourceLocation.tryParse(swapped);
            if (rl2 != null && BuiltInRegistries.ENTITY_TYPE.containsKey(rl2)) {
                return rl2.toString();
            }
            return null;
        } else {
            for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
                if (id.getNamespace().equalsIgnoreCase(s))
                    return s;
            }
            ResourceLocation unique = null;
            for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
                if (id.getPath().equalsIgnoreCase(s)) {
                    if (unique != null)
                        return null;
                    unique = id;
                }
            }
            if (unique != null)
                return unique.toString();
            return null;
        }
    }
}
