package net.wheel.moblimiter.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import net.wheel.moblimiter.config.MLConfig;

public final class MLWhiteListHandler {
    public static boolean whiteListed(Entity entity) {
        ResourceLocation id = EntityType.getKey(entity.getType());
        String full = id.toString().toLowerCase();
        String ns = id.getNamespace().toLowerCase();
        var wl = MLConfig.getWhiteList();
        return wl.contains(full) || wl.contains(ns);
    }
}
