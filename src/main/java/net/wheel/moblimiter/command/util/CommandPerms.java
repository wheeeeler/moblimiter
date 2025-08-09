package net.wheel.moblimiter.command.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public final class CommandPerms {
    public static boolean has(CommandSourceStack src, String node) {
        var ent = src.getEntity();
        if (!(ent instanceof ServerPlayer sp))
            return true;
        Boolean b = tryBukkit(sp, node);
        if (b != null)
            return b;
        b = tryLPMod(sp, node);
        if (b != null)
            return b;
        return sp.hasPermissions(4);
    }

    public static boolean has(ServerPlayer sp, String node) {
        Boolean b = tryBukkit(sp, node);
        if (b != null)
            return b;
        b = tryLPMod(sp, node);
        if (b != null)
            return b;
        return sp.hasPermissions(4);
    }

    private static Boolean tryBukkit(ServerPlayer sp, String node) {
        try {
            Player bukkit = Bukkit.getPlayerExact(sp.getGameProfile().getName());
            if (bukkit != null && org.bukkit.Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
                return bukkit.hasPermission(node);
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static Boolean tryLPMod(ServerPlayer sp, String node) {
        try {
            net.luckperms.api.LuckPerms api = net.luckperms.api.LuckPermsProvider.get();
            net.luckperms.api.model.user.User user = api.getUserManager().getUser(sp.getUUID());
            if (user == null)
                return false;
            net.luckperms.api.query.QueryOptions qo = api.getContextManager().getQueryOptions(user)
                    .orElse(api.getContextManager().getStaticQueryOptions());
            return user.getCachedData().getPermissionData(qo).checkPermission(node).asBoolean();
        } catch (Throwable ignored) {
        }
        return null;
    }
}
