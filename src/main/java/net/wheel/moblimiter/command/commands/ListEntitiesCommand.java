package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import net.wheel.moblimiter.command.util.CMD;
import net.wheel.moblimiter.util.MLColor;
import net.wheel.moblimiter.util.Messages;

public final class ListEntitiesCommand {
    public static void regCMD(LiteralArgumentBuilder<CommandSourceStack> root, String perm) {
        root.then(literal("listentities")
                .requires(CMD.require(perm))
                .then(argument("target", StringArgumentType.word())
                        .suggests((ctx, b) -> suggestWorlds(ctx.getSource(), b))
                        .executes(ctx -> {
                            String target = StringArgumentType.getString(ctx, "target");
                            var src = ctx.getSource();

                            if ("all".equalsIgnoreCase(target)) {
                                src.sendSuccess(
                                        () -> MLColor.parse(Messages.text("listentities.header")),
                                        false);
                                for (ServerLevel level : src.getServer().getAllLevels()) {
                                    int count = nonPlayer(level);
                                    String name = level.dimension().location().toString();
                                    src.sendSuccess(
                                            () -> MLColor.parse(Messages.text("listentities.line", name, count)),
                                            false);
                                }
                                return 1;
                            } else {
                                ServerLevel match = null;
                                for (ServerLevel level : src.getServer().getAllLevels()) {
                                    String path = level.dimension().location().getPath();
                                    String full = level.dimension().location().toString();
                                    if (path.equalsIgnoreCase(target) || full.equalsIgnoreCase(target)) {
                                        match = level;
                                        break;
                                    }
                                }
                                if (match == null) {
                                    src.sendSuccess(() -> MLColor.parse(Messages.text("listentities.notfound", target)),
                                            false);
                                    return 1;
                                }
                                int count = nonPlayer(match);
                                String name = match.dimension().location().toString();
                                src.sendSuccess(() -> MLColor.parse(Messages.text("listentities.single", name, count)),
                                        false);
                                return 1;
                            }
                        })));
    }

    private static int nonPlayer(ServerLevel level) {
        int count = 0;
        for (Entity e : level.getEntities().getAll()) {
            if (!(e instanceof Player)) {
                count++;
            }
        }
        return count;
    }

    private static CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestWorlds(
            CommandSourceStack src, SuggestionsBuilder b) {

        b.suggest("all");

        Set<String> seen = new HashSet<>();
        for (ServerLevel level : src.getServer().getAllLevels()) {
            String path = level.dimension().location().getPath();
            String full = level.dimension().location().toString();

            if (seen.add(path)) {
                b.suggest(path);
            }
            if (seen.add(full)) {
                b.suggest(full);
            }
        }

        return b.buildFuture();
    }

}
