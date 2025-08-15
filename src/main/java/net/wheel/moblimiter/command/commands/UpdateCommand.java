package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.arguments.IntegerArgumentType;
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
                        .then(literal("list")
                                .executes(ctx -> listStrict(ctx.getSource(),
                                        MLConfig.getEntityStrictClear(), MLConfig.getModStrictClear(),
                                        "AutoClear")))
                        .then(literal("remove")
                                .then(argument("entry", StringArgumentType.greedyString())
                                        .suggests(UpdateCommand::suggestStrictKeys)
                                        .executes(ctx -> {
                                            String raw = StringArgumentType.getString(ctx, "entry")
                                                    .toLowerCase(Locale.ROOT).trim();
                                            String resolved = resolveStrictKey(raw);
                                            if (resolved == null) {
                                                return CMD.fail(ctx, Messages.text("strict.invalid", raw));
                                            }
                                            boolean removed;
                                            if (resolved.indexOf(':') > 0) {
                                                removed = MLConfig.getEntityStrictClear().remove(resolved) != null;
                                            } else {
                                                removed = MLConfig.getModStrictClear().remove(resolved) != null;
                                            }
                                            CMD.saveConfig();
                                            return CMD.success(ctx, removed
                                                    ? Messages.text("strict.autoclear.removed", resolved)
                                                    : Messages.text("strict.autoclear.missing", resolved));
                                        })))
                        .then(argument("entry", StringArgumentType.string())
                                .suggests(UpdateCommand::suggestStrictKeys)
                                .then(argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(ctx -> {
                                            String raw = StringArgumentType.getString(ctx, "entry")
                                                    .toLowerCase(Locale.ROOT).trim();
                                            int v = IntegerArgumentType.getInteger(ctx, "value");
                                            String resolved = resolveStrictKey(raw);
                                            if (resolved == null) {
                                                return CMD.fail(ctx, Messages.text("strict.invalid", raw));
                                            }
                                            if (resolved.indexOf(':') > 0) {
                                                MLConfig.getEntityStrictClear().put(resolved, v);
                                            } else {
                                                MLConfig.getModStrictClear().put(resolved, v);
                                            }
                                            CMD.saveConfig();
                                            return CMD.success(ctx, Messages.text("strict.autoclear.set", resolved, v));
                                        }))))
                .then(literal("list")
                        .requires(CMD.require(perm))
                        .then(literal("strictlimit")
                                .executes(ctx -> listStrict(ctx.getSource(),
                                        MLConfig.getEntityStrictClear(), MLConfig.getModStrictClear(),
                                        "AutoClear"))))
                .then(literal("remove")
                        .requires(CMD.require(perm))
                        .then(literal("strictlimit")
                                .then(argument("entry", StringArgumentType.greedyString())
                                        .suggests(UpdateCommand::suggestStrictKeys)
                                        .executes(ctx -> {
                                            String raw = StringArgumentType.getString(ctx, "entry")
                                                    .toLowerCase(Locale.ROOT).trim();
                                            String resolved = resolveStrictKey(raw);
                                            if (resolved == null) {
                                                return CMD.fail(ctx, Messages.text("strict.invalid", raw));
                                            }
                                            boolean removed;
                                            if (resolved.indexOf(':') > 0) {
                                                removed = MLConfig.getEntityStrictClear().remove(resolved) != null;
                                            } else {
                                                removed = MLConfig.getModStrictClear().remove(resolved) != null;
                                            }
                                            CMD.saveConfig();
                                            return CMD.success(ctx, removed
                                                    ? Messages.text("strict.autoclear.removed", resolved)
                                                    : Messages.text("strict.autoclear.missing", resolved));
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
                        .then(literal("list")
                                .executes(ctx -> listStrict(ctx.getSource(),
                                        MLConfig.getEntityStrictSpawn(), MLConfig.getModStrictSpawn(),
                                        "EntityLimit")))
                        .then(literal("remove")
                                .then(argument("entry", StringArgumentType.greedyString())
                                        .suggests(UpdateCommand::suggestStrictKeys)
                                        .executes(ctx -> {
                                            String raw = StringArgumentType.getString(ctx, "entry")
                                                    .toLowerCase(Locale.ROOT).trim();
                                            String resolved = resolveStrictKey(raw);
                                            if (resolved == null) {
                                                return CMD.fail(ctx, Messages.text("strict.invalid", raw));
                                            }
                                            boolean removed;
                                            if (resolved.indexOf(':') > 0) {
                                                removed = MLConfig.getEntityStrictSpawn().remove(resolved) != null;
                                            } else {
                                                removed = MLConfig.getModStrictSpawn().remove(resolved) != null;
                                            }
                                            CMD.saveConfig();
                                            return CMD.success(ctx, removed
                                                    ? Messages.text("strict.entitylimit.removed", resolved)
                                                    : Messages.text("strict.entitylimit.missing", resolved));
                                        })))
                        .then(argument("entry", StringArgumentType.string())
                                .suggests(UpdateCommand::suggestStrictKeys)
                                .then(argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(ctx -> {
                                            String raw = StringArgumentType.getString(ctx, "entry")
                                                    .toLowerCase(Locale.ROOT).trim();
                                            int v = IntegerArgumentType.getInteger(ctx, "value");
                                            String resolved = resolveStrictKey(raw);
                                            if (resolved == null) {
                                                return CMD.fail(ctx, Messages.text("strict.invalid", raw));
                                            }
                                            if (resolved.indexOf(':') > 0) {
                                                MLConfig.getEntityStrictSpawn().put(resolved, v);
                                            } else {
                                                MLConfig.getModStrictSpawn().put(resolved, v);
                                            }
                                            CMD.saveConfig();
                                            return CMD.success(ctx,
                                                    Messages.text("strict.entitylimit.set", resolved, v));
                                        }))))
                .then(literal("list")
                        .requires(CMD.require(perm))
                        .then(literal("strictlimit")
                                .executes(ctx -> listStrict(ctx.getSource(),
                                        MLConfig.getEntityStrictSpawn(), MLConfig.getModStrictSpawn(),
                                        "EntityLimit"))))
                .then(literal("remove")
                        .requires(CMD.require(perm))
                        .then(literal("strictlimit")
                                .then(argument("entry", StringArgumentType.greedyString())
                                        .suggests(UpdateCommand::suggestStrictKeys)
                                        .executes(ctx -> {
                                            String raw = StringArgumentType.getString(ctx, "entry")
                                                    .toLowerCase(Locale.ROOT).trim();
                                            String resolved = resolveStrictKey(raw);
                                            if (resolved == null) {
                                                return CMD.fail(ctx, Messages.text("strict.invalid", raw));
                                            }
                                            boolean removed;
                                            if (resolved.indexOf(':') > 0) {
                                                removed = MLConfig.getEntityStrictSpawn().remove(resolved) != null;
                                            } else {
                                                removed = MLConfig.getModStrictSpawn().remove(resolved) != null;
                                            }
                                            CMD.saveConfig();
                                            return CMD.success(ctx, removed
                                                    ? Messages.text("strict.entitylimit.removed", resolved)
                                                    : Messages.text("strict.entitylimit.missing", resolved));
                                        }))));

        update.then(autoclear);
        update.then(entitylimit);
        update.then(net.wheel.moblimiter.command.commands.WhitelistCommand.node(perm));

        root.then(update);
    }

    private static int listStrict(CommandSourceStack src,
            Map<String, Integer> byEntity,
            Map<String, Integer> byMod,
            String title) {
        if (byEntity.isEmpty() && byMod.isEmpty()) {
            src.sendSuccess(() -> MLColor.parse(Messages.text("strict.empty", title)), false);
            return 1;
        }
        src.sendSuccess(() -> MLColor.parse(Messages.text("strict.header", title)), false);
        if (!byEntity.isEmpty()) {
            src.sendSuccess(() -> MLColor.parse(Messages.text("strict.entities.header", title)), false);
            for (Map.Entry<String, Integer> e : byEntity.entrySet()) {
                src.sendSuccess(() -> MLColor.parse(Messages.text("strict.entities.line", e.getKey(), e.getValue())),
                        false);
            }
        }
        if (!byMod.isEmpty()) {
            src.sendSuccess(() -> MLColor.parse(Messages.text("strict.mods.header", title)), false);
            for (Map.Entry<String, Integer> e : byMod.entrySet()) {
                src.sendSuccess(() -> MLColor.parse(Messages.text("strict.mods.line", e.getKey(), e.getValue())),
                        false);
            }
        }
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestCurrentClearLimit(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder b) {
        b.suggest(Integer.toString(MLConfig.getClearLimit()));
        return b.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestStrictKeys(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder b) {
        Set<String> namespaces = new HashSet<>();
        Set<String> simpleNames = new HashSet<>();
        for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            b.suggest(id.toString());
            namespaces.add(id.getNamespace());
            simpleNames.add(id.getPath());
        }
        for (String ns : namespaces)
            b.suggest(ns);
        for (String p : simpleNames)
            b.suggest(p);
        return b.buildFuture();
    }

    private static String resolveStrictKey(String input) {
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
}
